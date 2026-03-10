package click.quickclicker.fiszki.database.ORM

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.dao.RuntimeExceptionDao
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.flashcard.Flashcard
import java.sql.SQLException

class DBHelper(context: Context) :
    OrmLiteSqliteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config) {

    companion object {
        private const val DATABASE_NAME = "Flashcards.db"
        private const val DATABASE_VERSION = 7
    }

    private var flashcardDao: RuntimeExceptionDao<Flashcard, Int>? = null
    private var categoryDao: RuntimeExceptionDao<Category, Int>? = null

    override fun onCreate(database: SQLiteDatabase, connectionSource: ConnectionSource) {
        try {
            TableUtils.createTable(connectionSource, Category::class.java)
            TableUtils.createTable(connectionSource, Flashcard::class.java)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun onUpgrade(
        database: SQLiteDatabase,
        connectionSource: ConnectionSource,
        oldVersion: Int,
        newVersion: Int
    ) {
        onCreate(database, connectionSource)
        if (newVersion >= 3 && oldVersion < 3) {
            getCategoryDao().executeRaw("ALTER TABLE `category` ADD COLUMN chosen BOOLEAN;")
        }
        if (newVersion >= 4 && oldVersion < 4) {
            getCategoryDao().executeRaw("ALTER TABLE `category` ADD COLUMN langFrom VARCHAR(255);")
            getCategoryDao().executeRaw("ALTER TABLE `category` ADD COLUMN langOn VARCHAR(255);")
        }
        if (newVersion >= 5 && oldVersion < 5) {
            getFlashcardDao()
                .executeRaw("ALTER TABLE `flashcard` ADD COLUMN staticFail INT(255) DEFAULT '0';")
            getFlashcardDao()
                .executeRaw("ALTER TABLE `flashcard` ADD COLUMN staticPass INT(255) DEFAULT '0';")
        }
        if (newVersion >= 6 && oldVersion < 6) {
            getCategoryDao().executeRaw("ALTER TABLE `category` ADD COLUMN color VARCHAR(255);")
        }
        if (newVersion >= 7 && oldVersion < 7) {
            getFlashcardDao().executeRaw("ALTER TABLE `flashcard` ADD COLUMN fsrsStability DOUBLE DEFAULT 0.0;")
            getFlashcardDao().executeRaw("ALTER TABLE `flashcard` ADD COLUMN fsrsDifficulty DOUBLE DEFAULT 0.0;")
            getFlashcardDao().executeRaw("ALTER TABLE `flashcard` ADD COLUMN fsrsElapsedDays INT DEFAULT 0;")
            getFlashcardDao().executeRaw("ALTER TABLE `flashcard` ADD COLUMN fsrsScheduledDays INT DEFAULT 0;")
            getFlashcardDao().executeRaw("ALTER TABLE `flashcard` ADD COLUMN fsrsReps INT DEFAULT 0;")
            getFlashcardDao().executeRaw("ALTER TABLE `flashcard` ADD COLUMN fsrsLapses INT DEFAULT 0;")
            getFlashcardDao().executeRaw("ALTER TABLE `flashcard` ADD COLUMN fsrsState INT DEFAULT 0;")
            getFlashcardDao().executeRaw("ALTER TABLE `flashcard` ADD COLUMN fsrsLastReview BIGINT DEFAULT 0;")
            getFlashcardDao().executeRaw("ALTER TABLE `flashcard` ADD COLUMN fsrsLastRating INT DEFAULT 0;")
        }
    }

    fun getFlashcardDao(): RuntimeExceptionDao<Flashcard, Int> {
        if (flashcardDao == null) {
            flashcardDao = getRuntimeExceptionDao(Flashcard::class.java)
        }
        return flashcardDao!!
    }

    fun getCategoryDao(): RuntimeExceptionDao<Category, Int> {
        if (categoryDao == null) {
            categoryDao = getRuntimeExceptionDao(Category::class.java)
        }
        return categoryDao!!
    }
}
