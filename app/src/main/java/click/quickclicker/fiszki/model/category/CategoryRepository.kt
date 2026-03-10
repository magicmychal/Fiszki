package click.quickclicker.fiszki.model.category

import android.content.Context
import com.j256.ormlite.android.apptools.OpenHelperManager
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.database.ORM.DBHelper

class CategoryRepository(private val context: Context) {

    companion object {
        const val addCategoryName = "ADDNEWCATEGORY"
    }

    private val dbHelper: DBHelper = OpenHelperManager.getHelper(context, DBHelper::class.java)
    private val categoryDao = dbHelper.getCategoryDao()

    fun getAllCategory(): ArrayList<Category> = ArrayList(categoryDao.queryForAll())

    fun addCategory(category: Category) {
        categoryDao.create(category)
    }

    fun countCategory(): Int = categoryDao.countOf().toInt()

    fun addSystemCategory() {
        val firstCategory = Category().apply {
            id = 1
            setCategory(context.resources.getString(R.string.uncategory))
            isEntryByUser = false
        }
        categoryDao.createIfNotExists(firstCategory)
        if (getCategoryByID(1)?.categoryDB != firstCategory.categoryDB) {
            updateCategory(firstCategory)
        }
        // delete addCategory from version<1.7
        val addCategory = categoryDao.queryForId(2)
        if (addCategory != null && addCategory.getCategory() == addCategoryName && !addCategory.isEntryByUser) {
            categoryDao.delete(addCategory)
        }
    }

    fun getCategoryByName(name: String): Category? {
        val arrayList = ArrayList(categoryDao.queryForEq(Category.columnCategoryCategory, name))
        return if (arrayList.isNotEmpty()) arrayList[0] else null
    }

    fun getCategoryByID(id: Int): Category? {
        val arrayList = ArrayList(categoryDao.queryForEq(Category.columnCategoryId, id))
        return if (arrayList.isNotEmpty()) arrayList[0] else null
    }

    fun getUserCategory(): ArrayList<Category> {
        return ArrayList(categoryDao.queryForEq(Category.columnCategoryEntryByUsers, true))
    }

    fun updateCategory(category: Category) {
        categoryDao.update(category)
    }

    fun deleteCategory(category: Category) {
        categoryDao.delete(category)
    }

    fun deleteCategories(categories: ArrayList<Category>) {
        for (category in categories) {
            categoryDao.delete(category)
        }
    }

    fun getChosenCategory(): ArrayList<Category> {
        return ArrayList(categoryDao.queryForEq(Category.columnCategoryChosen, true))
    }

    fun getCategoryByLang(langFrom: String, langOn: String): ArrayList<Category> {
        val categories = ArrayList<Category>()
        val categoryFrom = ArrayList(categoryDao.queryForEq(Category.columnCategoryLangFrom, langFrom))
        for (cat in categoryFrom) {
            if (cat.getLangOn() != null && cat.getLangOn() == langOn) {
                categories.add(cat)
            }
        }
        return categories
    }

    fun getCategoryByLangFrom(langFrom: String): ArrayList<Category> {
        return ArrayList(categoryDao.queryForEq(Category.columnCategoryLangFrom, langFrom))
    }

    fun getCategoryByLangOn(langOn: String): ArrayList<Category> {
        return ArrayList(categoryDao.queryForEq(Category.columnCategoryLangOn, langOn))
    }
}
