package eu.qm.fiszki.model.category

import com.j256.ormlite.field.DatabaseField
import java.io.Serializable

class Category : Serializable {

    companion object {
        const val columnCategoryId = "id"
        const val columnCategoryLangOn = "langOn"
        const val columnCategoryChosen = "chosen"
        const val columnCategoryLangFrom = "langFrom"
        const val columnCategoryCategory = "category"
        const val columnCategoryEntryByUsers = "entryByUser"
        const val columnCategoryColor = "color"
    }

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    var id: Int = 0

    @DatabaseField
    private var category: String? = null

    @DatabaseField(columnName = "entryByUser", defaultValue = "true")
    var isEntryByUser: Boolean = false

    @DatabaseField(columnName = "chosen", defaultValue = "false")
    var isChosen: Boolean = false

    @DatabaseField
    private var langOn: String? = null

    @DatabaseField
    private var langFrom: String? = null

    @DatabaseField(columnName = "color")
    private var color: String? = null

    constructor()

    val categoryDB: String
        get() = category ?: ""

    fun getCategory(): String = category?.replace("%sq%", "'") ?: ""

    fun setCategory(category: String) {
        this.category = category.replace("'", "%sq%")
    }

    val langOnDB: String?
        get() = langOn

    fun getLangOn(): String? = langOn?.replace("%sq%", "'")

    fun setLangOn(langOn: String) {
        this.langOn = langOn.replace("'", "%sq%")
    }

    val langFromDB: String?
        get() = langFrom

    fun getLangFrom(): String? = langFrom?.replace("%sq%", "'")

    fun setLangFrom(langFrom: String) {
        this.langFrom = langFrom.replace("'", "%sq%")
    }

    fun getColor(): String? = color

    fun setColor(c: String?) {
        color = c
    }
}
