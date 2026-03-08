package eu.qm.fiszki.activity

import android.app.Activity
import android.content.Intent
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.exam.ExamCheckActivity
import eu.qm.fiszki.activity.exam.ExamBadAnswerActivity
import eu.qm.fiszki.activity.learning.LearningCheckActivity
import eu.qm.fiszki.model.flashcard.Flashcard

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
        @Suppress("DEPRECATION") activity.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    fun exitLearningCheck() {
        activity.finish()
        @Suppress("DEPRECATION") activity.overridePendingTransition(R.anim.right_out, R.anim.left_in)
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
        @Suppress("DEPRECATION") activity.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    fun exitExamCheck() {
        val intent = Intent(activity, NavHostActivity::class.java).apply {
            putExtra(NavHostActivity.EXTRA_TAB, R.id.nav_exam)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        activity.startActivity(intent)
        activity.finish()
        @Suppress("DEPRECATION") activity.overridePendingTransition(R.anim.right_out, R.anim.left_in)
    }

    fun goToExamSummary(summaryData: eu.qm.fiszki.dialogs.exam.ExamSummaryData) {
        val intent = Intent(activity, ExamBadAnswerActivity::class.java).apply {
            putExtra(EXAM_SUMMARY_DATA_KEY_INTENT, summaryData)
        }
        activity.startActivity(intent)
        activity.finish()
        @Suppress("DEPRECATION") activity.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    fun exitExamBadAnswer() {
        val intent = Intent(activity, NavHostActivity::class.java).apply {
            putExtra(NavHostActivity.EXTRA_TAB, R.id.nav_exam)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        activity.startActivity(intent)
        activity.finish()
        @Suppress("DEPRECATION") activity.overridePendingTransition(R.anim.right_out, R.anim.left_in)
    }

    fun resetMain() {
        activity.finish()
        activity.startActivity(Intent(activity, NavHostActivity::class.java))
    }
}
