package click.quickclicker.fiszki.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import click.quickclicker.fiszki.model.flashcard.Flashcard

@Dao
interface FlashcardDao {

    @Query("SELECT * FROM flashcard")
    fun getAll(): List<Flashcard>

    @Query("SELECT COUNT(*) FROM flashcard")
    fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(flashcard: Flashcard)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(flashcards: List<Flashcard>)

    @Query("SELECT * FROM flashcard WHERE word = :name LIMIT 1")
    fun getByWord(name: String): Flashcard?

    @Delete
    fun delete(flashcard: Flashcard)

    @Update
    fun update(flashcard: Flashcard)

    @Query("SELECT * FROM flashcard WHERE priority = :priority")
    fun getByPriority(priority: Int): List<Flashcard>

    @Query("SELECT * FROM flashcard WHERE categoryID = :categoryID")
    fun getByCategoryID(categoryID: Int): List<Flashcard>
}

