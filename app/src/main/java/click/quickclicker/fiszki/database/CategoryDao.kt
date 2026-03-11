package click.quickclicker.fiszki.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import click.quickclicker.fiszki.model.category.Category

@Dao
interface CategoryDao {

    @Query("SELECT * FROM category")
    fun getAll(): List<Category>

    @Query("SELECT COUNT(*) FROM category")
    fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIfNotExists(category: Category): Long

    @Update
    fun update(category: Category)

    @Delete
    fun delete(category: Category)

    @Query("SELECT * FROM category WHERE id = :id LIMIT 1")
    fun getById(id: Int): Category?

    @Query("SELECT * FROM category WHERE category = :name LIMIT 1")
    fun getByName(name: String): Category?

    @Query("SELECT * FROM category WHERE entryByUser = 1")
    fun getUserCategories(): List<Category>

    @Query("SELECT * FROM category WHERE chosen = 1")
    fun getChosenCategories(): List<Category>

    @Query("SELECT * FROM category WHERE langFrom = :langFrom")
    fun getByLangFrom(langFrom: String): List<Category>

    @Query("SELECT * FROM category WHERE langOn = :langOn")
    fun getByLangOn(langOn: String): List<Category>

    @Query("SELECT * FROM category WHERE langFrom = :langFrom AND langOn = :langOn")
    fun getByLangFromAndLangOn(langFrom: String, langOn: String): List<Category>
}

