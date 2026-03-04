package eu.qm.fiszki.model.flashcard

import com.j256.ormlite.field.DatabaseField
import java.io.Serializable

class Flashcard : Serializable {

    companion object {
        const val columnFlashcardId = "id"
        const val columnFlashcardWord = "word"
        const val columnFlashcardTranslation = "translation"
        const val columnFlashcardPriority = "priority"
        const val columnFlashcardCategoryID = "categoryID"
    }

    @DatabaseField(generatedId = true, allowGeneratedIdInsert = true)
    var id: Int = 0

    @DatabaseField
    private var word: String? = null

    @DatabaseField
    private var translation: String? = null

    @DatabaseField
    var priority: Int = 0

    @DatabaseField
    var categoryID: Int = 0

    @DatabaseField
    var staticFail: Int = 0
        private set

    @DatabaseField
    var staticPass: Int = 0
        private set

    constructor()

    val wordDB: String
        get() = word ?: ""

    fun getWord(): String = word?.replace("%sq%", "'") ?: ""

    fun setWord(word: String) {
        this.word = word.replace("'", "%sq%")
    }

    val translationDB: String
        get() = translation ?: ""

    fun getTranslation(): String = translation?.replace("%sq%", "'") ?: ""

    fun setTranslation(translation: String) {
        this.translation = translation.replace("'", "%sq%")
    }

    fun upStaticFail() {
        staticFail++
    }

    fun upStaticPass() {
        staticPass++
    }

    fun upPriority() {
        if (priority <= 5) {
            priority++
        } else {
            priority = 5
        }
    }

    fun downPriority() {
        if (priority > 0) {
            priority--
        } else {
            priority = 0
        }
    }

    fun resetStatictic() {
        staticFail = 0
        staticPass = 0
    }
}
