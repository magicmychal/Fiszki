package click.quickclicker.fiszki.model.flashcard

import com.j256.ormlite.field.DatabaseField
import click.quickclicker.fiszki.algorithm.fsrs.FsrsCard
import click.quickclicker.fiszki.algorithm.fsrs.FsrsState
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

    @DatabaseField(columnName = "fsrsStability", defaultValue = "0.0")
    var fsrsStability: Double = 0.0

    @DatabaseField(columnName = "fsrsDifficulty", defaultValue = "0.0")
    var fsrsDifficulty: Double = 0.0

    @DatabaseField(columnName = "fsrsElapsedDays", defaultValue = "0")
    var fsrsElapsedDays: Int = 0

    @DatabaseField(columnName = "fsrsScheduledDays", defaultValue = "0")
    var fsrsScheduledDays: Int = 0

    @DatabaseField(columnName = "fsrsReps", defaultValue = "0")
    var fsrsReps: Int = 0

    @DatabaseField(columnName = "fsrsLapses", defaultValue = "0")
    var fsrsLapses: Int = 0

    @DatabaseField(columnName = "fsrsState", defaultValue = "0")
    var fsrsState: Int = 0

    @DatabaseField(columnName = "fsrsLastReview", defaultValue = "0")
    var fsrsLastReview: Long = 0L

    @DatabaseField(columnName = "fsrsLastRating", defaultValue = "0")
    var fsrsLastRating: Int = 0

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

    fun toFsrsCard(): FsrsCard = FsrsCard(
        stability = fsrsStability,
        difficulty = fsrsDifficulty,
        elapsedDays = fsrsElapsedDays,
        scheduledDays = fsrsScheduledDays,
        reps = fsrsReps,
        lapses = fsrsLapses,
        state = FsrsState.entries[fsrsState],
        lastReview = fsrsLastReview
    )

    fun applyFsrsCard(card: FsrsCard) {
        fsrsStability = card.stability
        fsrsDifficulty = card.difficulty
        fsrsElapsedDays = card.elapsedDays
        fsrsScheduledDays = card.scheduledDays
        fsrsReps = card.reps
        fsrsLapses = card.lapses
        fsrsState = card.state.ordinal
        fsrsLastReview = card.lastReview
    }
}
