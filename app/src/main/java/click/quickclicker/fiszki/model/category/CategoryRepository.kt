package click.quickclicker.fiszki.model.category

import android.content.Context
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.database.FiszkiDatabase

class CategoryRepository(private val context: Context) {

    companion object {
        const val addCategoryName = "ADDNEWCATEGORY"
    }

    private val dao = FiszkiDatabase.getInstance(context).categoryDao()

    fun getAllCategory(): ArrayList<Category> = ArrayList(dao.getAll())

    fun addCategory(category: Category) {
        dao.insert(category)
    }

    fun countCategory(): Int = dao.count()

    fun addSystemCategory() {
        val firstCategory = Category().apply {
            id = 1
            setCategory(context.resources.getString(R.string.uncategory))
            isEntryByUser = false
        }
        dao.insertIfNotExists(firstCategory)
        if (getCategoryByID(1)?.categoryDB != firstCategory.categoryDB) {
            updateCategory(firstCategory)
        }
        // delete addCategory from version<1.7
        val addCategory = dao.getById(2)
        if (addCategory != null && addCategory.getCategory() == addCategoryName && !addCategory.isEntryByUser) {
            dao.delete(addCategory)
        }
    }

    fun getCategoryByName(name: String): Category? {
        return dao.getByName(name)
    }

    fun getCategoryByID(id: Int): Category? {
        return dao.getById(id)
    }

    fun getUserCategory(): ArrayList<Category> {
        return ArrayList(dao.getUserCategories())
    }

    fun updateCategory(category: Category) {
        dao.update(category)
    }

    fun deleteCategory(category: Category) {
        dao.delete(category)
    }

    fun deleteCategories(categories: ArrayList<Category>) {
        for (category in categories) {
            dao.delete(category)
        }
    }

    fun getChosenCategory(): ArrayList<Category> {
        return ArrayList(dao.getChosenCategories())
    }

    fun getCategoryByLang(langFrom: String, langOn: String): ArrayList<Category> {
        return ArrayList(dao.getByLangFromAndLangOn(langFrom, langOn))
    }

    fun getCategoryByLangFrom(langFrom: String): ArrayList<Category> {
        return ArrayList(dao.getByLangFrom(langFrom))
    }

    fun getCategoryByLangOn(langOn: String): ArrayList<Category> {
        return ArrayList(dao.getByLangOn(langOn))
    }
}
