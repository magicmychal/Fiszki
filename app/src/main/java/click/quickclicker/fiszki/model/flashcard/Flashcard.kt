package click.quickclicker.fiszki.model.flashcard

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import click.quickclicker.fiszki.algorithm.fsrs.FsrsCard
import click.quickclicker.fiszki.algorithm.fsrs.FsrsState
import java.io.Serializable

@Entity(tableName = "flashcard")
class Flashcard : Serializable {

    companion object {
        const val columnFlashcardId = "id"
        const val columnFlashcardWord = "word"
        const val columnFlashcardTranslation = "translation"
        const val columnFlashcardPriority = "priority"
        const val columnFlashcardCategoryID = "categoryID"
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0

    @JvmField
    @ColumnInfo(name = "word")
    var word: String? = null

    @JvmField
    @ColumnInfo(name = "translation")
    var translation: String? = null

    @ColumnInfo(name = "priority")
    var priority: Int = 0

    @ColumnInfo(name = "categoryID")
    var categoryID: Int = 0

    @ColumnInfo(name = "staticFail", defaultValue = "0")
    var staticFail: Int = 0

    @ColumnInfo(name = "staticPass", defaultValue = "0")
    var staticPass: Int = 0

    @ColumnInfo(name = "fsrsStability", defaultValue = "0.0")
    var fsrsStability: Double = 0.0

    @ColumnInfo(name = "fsrsDifficulty", defaultValue = "0.0")
    var fsrsDifficulty: Double = 0.0

    @ColumnInfo(name = "fsrsElapsedDays", defaultValue = "0")
    var fsrsElapsedDays: Int = 0

    @ColumnInfo(name = "fsrsScheduledDays", defaultValue = "0")
    var fsrsScheduledDays: Int = 0

    @ColumnInfo(name = "fsrsReps", defaultValue = "0")
    var fsrsReps: Int = 0

    @ColumnInfo(name = "fsrsLapses", defaultValue = "0")
    var fsrsLapses: Int = 0

    @ColumnInfo(name = "fsrsState", defaultValue = "0")
    var fsrsState: Int = 0

    @ColumnInfo(name = "fsrsLastReview", defaultValue = "0")
    var fsrsLastReview: Long = 0L

    @ColumnInfo(name = "fsrsLastRating", defaultValue = "0")
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
