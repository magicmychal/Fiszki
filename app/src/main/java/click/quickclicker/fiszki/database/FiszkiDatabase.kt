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
         * using the 12-step rename strategy, preserving all user data.
         */
        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // ── category table ──
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `category_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `category` TEXT,
                        `entryByUser` INTEGER NOT NULL DEFAULT 1,
                        `chosen` INTEGER NOT NULL DEFAULT 0,
                        `langOn` TEXT,
                        `langFrom` TEXT,
                        `color` TEXT
                    )""".trimIndent()
                )
                db.execSQL(
                    """INSERT INTO `category_new` (`id`, `category`, `entryByUser`, `chosen`, `langOn`, `langFrom`, `color`)
                       SELECT `id`,
                              `category`,
                              COALESCE(`entryByUser`, 1),
                              COALESCE(`chosen`, 0),
                              `langOn`,
                              `langFrom`,
                              `color`
                       FROM `category`""".trimIndent()
                )
                db.execSQL("DROP TABLE `category`")
                db.execSQL("ALTER TABLE `category_new` RENAME TO `category`")

                // ── flashcard table ──
                db.execSQL(
                    """CREATE TABLE IF NOT EXISTS `flashcard_new` (
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
                    )""".trimIndent()
                )
                db.execSQL(
                    """INSERT INTO `flashcard_new` (`id`, `word`, `translation`, `priority`, `categoryID`,
                           `staticFail`, `staticPass`,
                           `fsrsStability`, `fsrsDifficulty`, `fsrsElapsedDays`, `fsrsScheduledDays`,
                           `fsrsReps`, `fsrsLapses`, `fsrsState`, `fsrsLastReview`, `fsrsLastRating`)
                       SELECT `id`, `word`, `translation`,
                              COALESCE(`priority`, 0),
                              COALESCE(`categoryID`, 0),
                              COALESCE(`staticFail`, 0),
                              COALESCE(`staticPass`, 0),
                              COALESCE(`fsrsStability`, 0.0),
                              COALESCE(`fsrsDifficulty`, 0.0),
                              COALESCE(`fsrsElapsedDays`, 0),
                              COALESCE(`fsrsScheduledDays`, 0),
                              COALESCE(`fsrsReps`, 0),
                              COALESCE(`fsrsLapses`, 0),
                              COALESCE(`fsrsState`, 0),
                              COALESCE(`fsrsLastReview`, 0),
                              COALESCE(`fsrsLastRating`, 0)
                       FROM `flashcard`""".trimIndent()
                )
                db.execSQL("DROP TABLE `flashcard`")
                db.execSQL("ALTER TABLE `flashcard_new` RENAME TO `flashcard`")
            }
        }
    }
}



