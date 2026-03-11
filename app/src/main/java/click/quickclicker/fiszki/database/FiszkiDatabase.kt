package click.quickclicker.fiszki.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.flashcard.Flashcard

@Database(
    entities = [Category::class, Flashcard::class],
    version = 8,
    exportSchema = false
)
abstract class FiszkiDatabase : RoomDatabase() {

    abstract fun flashcardDao(): FlashcardDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        private const val DATABASE_NAME = "Flashcards.db"

        @Volatile
        private var instance: FiszkiDatabase? = null

        fun getInstance(context: Context): FiszkiDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        /**
         * Deletes the database file and clears the singleton so the next
         * [getInstance] call creates a fresh database.
         */
        fun resetDatabase(context: Context) {
            synchronized(this) {
                instance?.close()
                instance = null
            }
            context.applicationContext.deleteDatabase(DATABASE_NAME)
        }

        private fun buildDatabase(context: Context): FiszkiDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                FiszkiDatabase::class.java,
                DATABASE_NAME
            )
                .addMigrations(MIGRATION_7_8)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
                .build()
        }

        /**
         * Migration from ORMLite (version 7) to Room (version 8).
         *
         * ORMLite created columns with VARCHAR/SMALLINT types and without NOT NULL
         * constraints. Room expects TEXT/INTEGER with NOT NULL on non-nullable Kotlin
         * fields. SQLite doesn't support ALTER COLUMN, so we recreate both tables
         * using the rename-copy strategy, preserving all user data.
         *
         * On some devices the previous release had R8-obfuscated table names
         * (e.g. `nt` instead of `category`) because ORMLite resolves names via
         * reflection. We detect the real table names by inspecting sqlite_master
         * for tables that contain the expected columns.
         */
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val oldCategoryTable = findTableWithColumn(db, "entryByUser")
                val oldFlashcardTable = findTableWithColumn(db, "categoryID")

                migrateCategoryTable(db, oldCategoryTable)
                migrateFlashcardTable(db, oldFlashcardTable)
            }

            /**
             * Finds the user table that contains the given column name.
             * Returns null if no such table exists.
             */
            private fun findTableWithColumn(
                db: SupportSQLiteDatabase,
                columnName: String
            ): String? {
                val tables = mutableListOf<String>()
                db.query("SELECT name FROM sqlite_master WHERE type='table'").use { cursor ->
                    while (cursor.moveToNext()) {
                        val name = cursor.getString(0)
                        if (!name.startsWith("sqlite_") &&
                            !name.startsWith("android_") &&
                            !name.startsWith("room_")
                        ) {
                            tables.add(name)
                        }
                    }
                }
                for (table in tables) {
                    db.query("PRAGMA table_info(`$table`)").use { cursor ->
                        while (cursor.moveToNext()) {
                            if (cursor.getString(cursor.getColumnIndexOrThrow("name")) == columnName) {
                                return table
                            }
                        }
                    }
                }
                return null
            }

            private fun migrateCategoryTable(db: SupportSQLiteDatabase, oldTable: String?) {
                if (oldTable != null && oldTable != "category") {
                    // R8-obfuscated table — create correct table, copy data, drop old
                    db.execSQL("DROP TABLE IF EXISTS `category`")
                    db.execSQL(CREATE_CATEGORY_TABLE)
                    db.execSQL(
                        """INSERT OR IGNORE INTO `category` (`id`, `category`, `entryByUser`, `chosen`, `langOn`, `langFrom`, `color`)
                           SELECT `id`, `category`,
                                  COALESCE(`entryByUser`, 1),
                                  COALESCE(`chosen`, 0),
                                  `langOn`, `langFrom`, `color`
                           FROM `$oldTable`""".trimIndent()
                    )
                    db.execSQL("DROP TABLE IF EXISTS `$oldTable`")
                } else if (oldTable == "category") {
                    // Properly named table — rename-copy to fix NOT NULL / types
                    db.execSQL("ALTER TABLE `category` RENAME TO `_category_old`")
                    db.execSQL(CREATE_CATEGORY_TABLE)
                    db.execSQL(
                        """INSERT INTO `category` (`id`, `category`, `entryByUser`, `chosen`, `langOn`, `langFrom`, `color`)
                           SELECT `id`, `category`,
                                  COALESCE(`entryByUser`, 1),
                                  COALESCE(`chosen`, 0),
                                  `langOn`, `langFrom`, `color`
                           FROM `_category_old`""".trimIndent()
                    )
                    db.execSQL("DROP TABLE `_category_old`")
                } else {
                    // No old table found — create empty table with correct schema
                    db.execSQL(CREATE_CATEGORY_TABLE)
                }
            }

            private fun migrateFlashcardTable(db: SupportSQLiteDatabase, oldTable: String?) {
                if (oldTable != null && oldTable != "flashcard") {
                    // R8-obfuscated table — create correct table, copy data, drop old
                    val hasFsrs = getColumnNames(db, oldTable).contains("fsrsStability")
                    db.execSQL("DROP TABLE IF EXISTS `flashcard`")
                    db.execSQL(CREATE_FLASHCARD_TABLE)
                    db.execSQL(buildFlashcardInsert("`$oldTable`", hasFsrs))
                    db.execSQL("DROP TABLE IF EXISTS `$oldTable`")
                } else if (oldTable == "flashcard") {
                    // Properly named table — rename-copy
                    val hasFsrs = getColumnNames(db, oldTable).contains("fsrsStability")
                    db.execSQL("ALTER TABLE `flashcard` RENAME TO `_flashcard_old`")
                    db.execSQL(CREATE_FLASHCARD_TABLE)
                    db.execSQL(buildFlashcardInsert("`_flashcard_old`", hasFsrs))
                    db.execSQL("DROP TABLE `_flashcard_old`")
                } else {
                    // No old table found — create empty table with correct schema
                    db.execSQL(CREATE_FLASHCARD_TABLE)
                }
            }

            private fun buildFlashcardInsert(sourceTable: String, hasFsrs: Boolean): String {
                val fsrsSelect = if (hasFsrs) {
                    """,COALESCE(`fsrsStability`, 0.0),
                       COALESCE(`fsrsDifficulty`, 0.0),
                       COALESCE(`fsrsElapsedDays`, 0),
                       COALESCE(`fsrsScheduledDays`, 0),
                       COALESCE(`fsrsReps`, 0),
                       COALESCE(`fsrsLapses`, 0),
                       COALESCE(`fsrsState`, 0),
                       COALESCE(`fsrsLastReview`, 0),
                       COALESCE(`fsrsLastRating`, 0)"""
                } else {
                    ", 0.0, 0.0, 0, 0, 0, 0, 0, 0, 0"
                }
                return """INSERT OR IGNORE INTO `flashcard` (`id`, `word`, `translation`, `priority`, `categoryID`,
                       `staticFail`, `staticPass`,
                       `fsrsStability`, `fsrsDifficulty`, `fsrsElapsedDays`, `fsrsScheduledDays`,
                       `fsrsReps`, `fsrsLapses`, `fsrsState`, `fsrsLastReview`, `fsrsLastRating`)
                   SELECT `id`, `word`, `translation`,
                          COALESCE(`priority`, 0),
                          COALESCE(`categoryID`, 0),
                          COALESCE(`staticFail`, 0),
                          COALESCE(`staticPass`, 0)
                          $fsrsSelect
                   FROM $sourceTable""".trimIndent()
            }

            private fun getColumnNames(db: SupportSQLiteDatabase, table: String): Set<String> {
                val columns = mutableSetOf<String>()
                db.query("PRAGMA table_info(`$table`)").use { cursor ->
                    while (cursor.moveToNext()) {
                        columns.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
                    }
                }
                return columns
            }
        }

        private const val CREATE_CATEGORY_TABLE = """CREATE TABLE IF NOT EXISTS `category` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `category` TEXT,
            `entryByUser` INTEGER NOT NULL DEFAULT 1,
            `chosen` INTEGER NOT NULL DEFAULT 0,
            `langOn` TEXT,
            `langFrom` TEXT,
            `color` TEXT
        )"""

        private const val CREATE_FLASHCARD_TABLE = """CREATE TABLE IF NOT EXISTS `flashcard` (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
            `word` TEXT,
            `translation` TEXT,
            `priority` INTEGER NOT NULL DEFAULT 0,
            `categoryID` INTEGER NOT NULL DEFAULT 0,
            `staticFail` INTEGER NOT NULL DEFAULT 0,
            `staticPass` INTEGER NOT NULL DEFAULT 0,
            `fsrsStability` REAL NOT NULL DEFAULT 0.0,
            `fsrsDifficulty` REAL NOT NULL DEFAULT 0.0,
            `fsrsElapsedDays` INTEGER NOT NULL DEFAULT 0,
            `fsrsScheduledDays` INTEGER NOT NULL DEFAULT 0,
            `fsrsReps` INTEGER NOT NULL DEFAULT 0,
            `fsrsLapses` INTEGER NOT NULL DEFAULT 0,
            `fsrsState` INTEGER NOT NULL DEFAULT 0,
            `fsrsLastReview` INTEGER NOT NULL DEFAULT 0,
            `fsrsLastRating` INTEGER NOT NULL DEFAULT 0
        )"""
    }
}

