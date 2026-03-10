package eu.qm.fiszki

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import eu.qm.fiszki.database.ORM.DBHelper
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.flashcard.Flashcard
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryTest {

    private lateinit var dbHelper: DBHelper

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("Flashcards.db")
        dbHelper = DBHelper(context)
    }

    @After
    fun tearDown() {
        dbHelper.close()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase("Flashcards.db")
    }

    // --- Flashcard tests ---

    @Test
    fun addFlashcard_getAllFlashcards_returnsIt() {
        val dao = dbHelper.getFlashcardDao()
        val card = Flashcard()
        card.setWord("apple")
        card.setTranslation("jablko")
        card.categoryID = 1
        dao.create(card)

        val all = dao.queryForAll()
        assertEquals(1, all.size)
        assertEquals("apple", all[0].getWord())
    }

    @Test
    fun getFlashcardsByCategoryID_filtersCorrectly() {
        val dao = dbHelper.getFlashcardDao()

        val card1 = Flashcard().apply {
            setWord("a"); setTranslation("b"); categoryID = 1
        }
        val card2 = Flashcard().apply {
            setWord("c"); setTranslation("d"); categoryID = 2
        }
        val card3 = Flashcard().apply {
            setWord("e"); setTranslation("f"); categoryID = 1
        }
        dao.create(card1)
        dao.create(card2)
        dao.create(card3)

        val cat1Cards = ArrayList(dao.queryForEq(Flashcard.columnFlashcardCategoryID, 1))
        assertEquals(2, cat1Cards.size)

        val cat2Cards = ArrayList(dao.queryForEq(Flashcard.columnFlashcardCategoryID, 2))
        assertEquals(1, cat2Cards.size)
    }

    @Test
    fun deleteFlashcard_removesFromAll() {
        val dao = dbHelper.getFlashcardDao()
        val card = Flashcard().apply {
            setWord("x"); setTranslation("y"); categoryID = 1
        }
        dao.create(card)
        assertEquals(1, dao.queryForAll().size)

        dao.delete(card)
        assertEquals(0, dao.queryForAll().size)
    }

    @Test
    fun fsrsFields_persistCorrectly() {
        val dao = dbHelper.getFlashcardDao()
        val card = Flashcard().apply {
            setWord("test"); setTranslation("test"); categoryID = 1
            fsrsStability = 12.5
            fsrsDifficulty = 6.3
            fsrsReps = 5
            fsrsLapses = 2
            fsrsState = 2  // Review
            fsrsLastReview = 1700000000000L
        }
        dao.create(card)

        val retrieved = dao.queryForAll().first()
        assertEquals(12.5, retrieved.fsrsStability, 0.001)
        assertEquals(6.3, retrieved.fsrsDifficulty, 0.001)
        assertEquals(5, retrieved.fsrsReps)
        assertEquals(2, retrieved.fsrsLapses)
        assertEquals(2, retrieved.fsrsState)
        assertEquals(1700000000000L, retrieved.fsrsLastReview)
    }

    // --- Category tests ---

    @Test
    fun addCategory_getAllCategory_returnsIt() {
        val dao = dbHelper.getCategoryDao()
        val cat = Category().apply {
            setCategory("Animals")
            isEntryByUser = true
        }
        dao.create(cat)

        val all = dao.queryForAll()
        assertEquals(1, all.size)
        assertEquals("Animals", all[0].getCategory())
    }

    @Test
    fun getCategoryByID_returnsCorrectCategory() {
        val dao = dbHelper.getCategoryDao()
        val cat = Category().apply {
            id = 10
            setCategory("Food")
            isEntryByUser = true
        }
        dao.createIfNotExists(cat)

        val result = dao.queryForId(10)
        assertNotNull(result)
        assertEquals("Food", result.getCategory())
    }

    @Test
    fun getUserCategory_onlyReturnsUserCreated() {
        val dao = dbHelper.getCategoryDao()

        val systemCat = Category().apply {
            setCategory("System")
            isEntryByUser = false
        }
        val userCat = Category().apply {
            setCategory("My Set")
            isEntryByUser = true
        }
        dao.create(systemCat)
        dao.create(userCat)

        val userOnly = ArrayList(dao.queryForEq(Category.columnCategoryEntryByUsers, true))
        assertEquals(1, userOnly.size)
        assertEquals("My Set", userOnly[0].getCategory())
    }

    @Test
    fun deleteCategory_removesIt() {
        val dao = dbHelper.getCategoryDao()
        val cat = Category().apply {
            setCategory("ToDelete")
            isEntryByUser = true
        }
        dao.create(cat)
        assertEquals(1, dao.queryForAll().size)

        dao.delete(cat)
        assertTrue(dao.queryForAll().isEmpty())
    }
}
