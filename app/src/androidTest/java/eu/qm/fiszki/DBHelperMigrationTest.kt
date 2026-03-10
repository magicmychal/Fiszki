package eu.qm.fiszki

import android.database.Cursor
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import eu.qm.fiszki.database.ORM.DBHelper
import eu.qm.fiszki.model.flashcard.Flashcard
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DBHelperMigrationTest {

    private lateinit var dbHelper: DBHelper

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Delete any existing test DB to get a fresh schema
        context.deleteDatabase("Flashcards.db")
        dbHelper = DBHelper(context)
    }

    @After
    fun tearDown() {
        dbHelper.close()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("Flashcards.db")
    }

    @Test
    fun freshDB_flashcardTable_hasAllExpectedColumns() {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("PRAGMA table_info(flashcard)", null)
        val columns = mutableSetOf<String>()
        while (cursor.moveToNext()) {
            columns.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
        }
        cursor.close()

        val expected = setOf(
            "id", "word", "translation", "priority", "categoryID",
            "staticFail", "staticPass",
            "fsrsStability", "fsrsDifficulty", "fsrsElapsedDays",
            "fsrsScheduledDays", "fsrsReps", "fsrsLapses",
            "fsrsState", "fsrsLastReview", "fsrsLastRating"
        )
        for (col in expected) {
            assertTrue("Missing column: $col", col in columns)
        }
    }

    @Test
    fun freshDB_categoryTable_hasAllExpectedColumns() {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.rawQuery("PRAGMA table_info(category)", null)
        val columns = mutableSetOf<String>()
        while (cursor.moveToNext()) {
            columns.add(cursor.getString(cursor.getColumnIndexOrThrow("name")))
        }
        cursor.close()

        val expected = setOf(
            "id", "category", "entryByUser", "chosen",
            "langOn", "langFrom", "color"
        )
        for (col in expected) {
            assertTrue("Missing column: $col", col in columns)
        }
    }

    @Test
    fun freshDB_fsrsColumns_haveCorrectDefaults() {
        val dao = dbHelper.getFlashcardDao()
        val card = Flashcard()
        card.setWord("test")
        card.setTranslation("test")
        card.categoryID = 1
        dao.create(card)

        val retrieved = dao.queryForAll().first()
        assertEquals(0.0, retrieved.fsrsStability, 0.001)
        assertEquals(0.0, retrieved.fsrsDifficulty, 0.001)
        assertEquals(0, retrieved.fsrsElapsedDays)
        assertEquals(0, retrieved.fsrsScheduledDays)
        assertEquals(0, retrieved.fsrsReps)
        assertEquals(0, retrieved.fsrsLapses)
        assertEquals(0, retrieved.fsrsState)
        assertEquals(0L, retrieved.fsrsLastReview)
        assertEquals(0, retrieved.fsrsLastRating)
    }

    @Test
    fun basicCRUD_insertAndReadBack() {
        val dao = dbHelper.getFlashcardDao()
        val card = Flashcard()
        card.setWord("hello")
        card.setTranslation("czesc")
        card.categoryID = 1
        card.priority = 3
        dao.create(card)

        val all = dao.queryForAll()
        assertEquals(1, all.size)
        assertEquals("hello", all[0].getWord())
        assertEquals("czesc", all[0].getTranslation())
        assertEquals(1, all[0].categoryID)
        assertEquals(3, all[0].priority)
    }
}
