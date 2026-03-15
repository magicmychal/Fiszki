package click.quickclicker.fiszki.activity

import android.app.Activity
import android.content.Intent
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.exam.ExamCheckActivity
import click.quickclicker.fiszki.activity.exam.ExamBadAnswerActivity
import click.quickclicker.fiszki.activity.learning.LearningCheckActivity
import click.quickclicker.fiszki.model.flashcard.Flashcard

class ChangeActivityManager(private val activity: Activity) {

    companion object {
        const val FLASHCARDS_KEY_INTENT = "FLASHCARDS"
        const val EXAM_REPEAT_KEY_INTENT = "REPEAT"
        const val EXAM_BAD_ANSWER_KEY_INTENT = "RESULTS"
        const val EXAM_SUMMARY_DATA_KEY_INTENT = "EXAM_SUMMARY_DATA"
        const val STRICT_MODE_KEY_INTENT = "STRICT_MODE"
        const val REVERSED_KEY_INTENT = "REVERSED"
    }

    fun goToLearningCheck(
        flashcards: ArrayList<Flashcard>,
        strictMode: Boolean = true,
        reversed: Boolean = false
    ) {
        val goLearning = Intent(activity, LearningCheckActivity::class.java).apply {
            putExtra(FLASHCARDS_KEY_INTENT, flashcards)
            putExtra(STRICT_MODE_KEY_INTENT, strictMode)
            putExtra(REVERSED_KEY_INTENT, reversed)
        }
        activity.startActivity(goLearning)
    }

    fun exitLearningCheck() {
        activity.finish()
    }

    fun goToExamCheck(
        flashcards: ArrayList<Flashcard>,
        repeat: Int,
        categoryName: String? = null,
        languagePair: String? = null
    ) {
        val bundle = ArrayList<Any?>().apply {
            add(flashcards)
            add(repeat)
            add(categoryName)
            add(languagePair)
        }
        val goLearning = Intent(activity, ExamCheckActivity::class.java).apply {
            putExtra(EXAM_REPEAT_KEY_INTENT, bundle)
        }
        activity.startActivity(goLearning)
        activity.finish()
    }

    fun exitExamCheck() {
        val intent = Intent(activity, NavHostActivity::class.java).apply {
            putExtra(NavHostActivity.EXTRA_TAB, R.id.nav_exam)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        activity.startActivity(intent)
        activity.finish()
    }

    fun goToExamSummary(summaryData: click.quickclicker.fiszki.dialogs.exam.ExamSummaryData) {
        val intent = Intent(activity, ExamBadAnswerActivity::class.java).apply {
            putExtra(EXAM_SUMMARY_DATA_KEY_INTENT, summaryData)
        }
        activity.startActivity(intent)
        activity.finish()
    }

    fun exitExamBadAnswer() {
        val intent = Intent(activity, NavHostActivity::class.java).apply {
            putExtra(NavHostActivity.EXTRA_TAB, R.id.nav_exam)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        activity.startActivity(intent)
        activity.finish()
    }

    fun resetMain() {
        activity.finish()
        activity.startActivity(Intent(activity, NavHostActivity::class.java))
    }
}
