package click.quickclicker.fiszki

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import click.quickclicker.fiszki.database.CategoryDao
import click.quickclicker.fiszki.database.FlashcardDao
import click.quickclicker.fiszki.database.FiszkiDatabase
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.flashcard.Flashcard
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositoryTest {

    private lateinit var db: FiszkiDatabase
    private lateinit var flashcardDao: FlashcardDao
    private lateinit var categoryDao: CategoryDao

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, FiszkiDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        flashcardDao = db.flashcardDao()
        categoryDao = db.categoryDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    // --- Flashcard tests ---

    @Test
    fun addFlashcard_getAllFlashcards_returnsIt() {
        val card = Flashcard()
        card.setWord("apple")
        card.setTranslation("jablko")
        card.categoryID = 1
        flashcardDao.insert(card)

        val all = flashcardDao.getAll()
        assertEquals(1, all.size)
        assertEquals("apple", all[0].getWord())
    }

    @Test
    fun getFlashcardsByCategoryID_filtersCorrectly() {
        val card1 = Flashcard().apply {
            setWord("a"); setTranslation("b"); categoryID = 1
        }
        val card2 = Flashcard().apply {
            setWord("c"); setTranslation("d"); categoryID = 2
        }
        val card3 = Flashcard().apply {
            setWord("e"); setTranslation("f"); categoryID = 1
        }
        flashcardDao.insert(card1)
        flashcardDao.insert(card2)
        flashcardDao.insert(card3)

        val cat1Cards = flashcardDao.getByCategoryID(1)
        assertEquals(2, cat1Cards.size)

        val cat2Cards = flashcardDao.getByCategoryID(2)
        assertEquals(1, cat2Cards.size)
    }

    @Test
    fun deleteFlashcard_removesFromAll() {
        val card = Flashcard().apply {
            setWord("x"); setTranslation("y"); categoryID = 1
        }
        flashcardDao.insert(card)
        val inserted = flashcardDao.getAll()
        assertEquals(1, inserted.size)

        flashcardDao.delete(inserted[0])
        assertEquals(0, flashcardDao.getAll().size)
    }

    @Test
    fun fsrsFields_persistCorrectly() {
        val card = Flashcard().apply {
            setWord("test"); setTranslation("test"); categoryID = 1
            fsrsStability = 12.5
            fsrsDifficulty = 6.3
            fsrsReps = 5
            fsrsLapses = 2
            fsrsState = 2  // Review
            fsrsLastReview = 1700000000000L
        }
        flashcardDao.insert(card)

        val retrieved = flashcardDao.getAll().first()
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
        val cat = Category().apply {
            setCategory("Animals")
            isEntryByUser = true
        }
        categoryDao.insert(cat)

        val all = categoryDao.getAll()
        assertEquals(1, all.size)
        assertEquals("Animals", all[0].getCategory())
    }

    @Test
    fun getCategoryByID_returnsCorrectCategory() {
        val cat = Category().apply {
            id = 10
            setCategory("Food")
            isEntryByUser = true
        }
        categoryDao.insertIfNotExists(cat)

        val result = categoryDao.getById(10)
        assertNotNull(result)
        assertEquals("Food", result!!.getCategory())
    }

    @Test
    fun getUserCategory_onlyReturnsUserCreated() {
        val systemCat = Category().apply {
            setCategory("System")
            isEntryByUser = false
        }
        val userCat = Category().apply {
            setCategory("My Set")
            isEntryByUser = true
        }
        categoryDao.insert(systemCat)
        categoryDao.insert(userCat)

        val userOnly = categoryDao.getUserCategories()
        assertEquals(1, userOnly.size)
        assertEquals("My Set", userOnly[0].getCategory())
    }

    @Test
    fun deleteCategory_removesIt() {
        val cat = Category().apply {
            setCategory("ToDelete")
            isEntryByUser = true
        }
        categoryDao.insert(cat)
        assertEquals(1, categoryDao.getAll().size)

        val inserted = categoryDao.getAll()[0]
        categoryDao.delete(inserted)
        assertTrue(categoryDao.getAll().isEmpty())
    }
}
