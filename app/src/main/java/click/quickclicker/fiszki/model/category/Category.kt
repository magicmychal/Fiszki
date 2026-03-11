package click.quickclicker.fiszki.model.category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "category")
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

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @JvmField
    @ColumnInfo(name = "category")
    var category: String? = null

    @ColumnInfo(name = "entryByUser", defaultValue = "1")
    var isEntryByUser: Boolean = false

    @ColumnInfo(name = "chosen", defaultValue = "0")
    var isChosen: Boolean = false

    @JvmField
    @ColumnInfo(name = "langOn")
    var langOn: String? = null

    @JvmField
    @ColumnInfo(name = "langFrom")
    var langFrom: String? = null

    @JvmField
    @ColumnInfo(name = "color")
    var color: String? = null

    constructor()

    @Ignore
    constructor(id: Int, category: String?, isEntryByUser: Boolean, isChosen: Boolean,
                langOn: String?, langFrom: String?, color: String?) {
        this.id = id
        this.category = category
        this.isEntryByUser = isEntryByUser
        this.isChosen = isChosen
        this.langOn = langOn
        this.langFrom = langFrom
        this.color = color
    }

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
