package click.quickclicker.fiszki.activity

import android.app.Activity
import android.content.Intent
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.exam.ExamCheckActivity
import click.quickclicker.fiszki.activity.exam.ExamBadAnswerActivity
import click.quickclicker.fiszki.activity.learning.LearningCheckActivity

class ChangeActivityManager(private val activity: Activity) {

    companion object {
        const val CATEGORY_ID_KEY_INTENT = "CATEGORY_ID"
        const val FLASHCARD_IDS_KEY_INTENT = "FLASHCARD_IDS"
        const val EXAM_ROUNDS_KEY_INTENT = "EXAM_ROUNDS"
        const val EXAM_CATEGORY_NAME_KEY_INTENT = "EXAM_CATEGORY_NAME"
        const val EXAM_LANGUAGE_PAIR_KEY_INTENT = "EXAM_LANGUAGE_PAIR"
        const val EXAM_SUMMARY_DATA_KEY_INTENT = "EXAM_SUMMARY_DATA"
        const val STRICT_MODE_KEY_INTENT = "STRICT_MODE"
        const val REVERSED_KEY_INTENT = "REVERSED"
        /** Value indicating "all categories" (no specific category selected). */
        const val ALL_CATEGORIES = -1
    }

    fun goToLearningCheck(
        categoryId: Int?,
        strictMode: Boolean = true,
        reversed: Boolean = false
    ) {
        val intent = Intent(activity, LearningCheckActivity::class.java).apply {
            putExtra(CATEGORY_ID_KEY_INTENT, categoryId ?: ALL_CATEGORIES)
            putExtra(STRICT_MODE_KEY_INTENT, strictMode)
            putExtra(REVERSED_KEY_INTENT, reversed)
        }
        activity.startActivity(intent)
    }

    /** Launch practice with an explicit list of flashcard IDs (for multi-category selection). */
    fun goToLearningCheckByIds(
        flashcardIds: IntArray,
        strictMode: Boolean = true,
        reversed: Boolean = false
    ) {
        val intent = Intent(activity, LearningCheckActivity::class.java).apply {
            putExtra(FLASHCARD_IDS_KEY_INTENT, flashcardIds)
            putExtra(STRICT_MODE_KEY_INTENT, strictMode)
            putExtra(REVERSED_KEY_INTENT, reversed)
        }
        activity.startActivity(intent)
    }

    fun exitLearningCheck() {
        activity.finish()
    }

    fun goToExamCheck(
        categoryId: Int?,
        rounds: Int,
        categoryName: String? = null,
        languagePair: String? = null
    ) {
        val intent = Intent(activity, ExamCheckActivity::class.java).apply {
            putExtra(CATEGORY_ID_KEY_INTENT, categoryId ?: ALL_CATEGORIES)
            putExtra(EXAM_ROUNDS_KEY_INTENT, rounds)
            putExtra(EXAM_CATEGORY_NAME_KEY_INTENT, categoryName)
            putExtra(EXAM_LANGUAGE_PAIR_KEY_INTENT, languagePair)
        }
        activity.startActivity(intent)
        activity.finish()
    }

    /** Launch exam with an explicit list of flashcard IDs (for single-category by name). */
    fun goToExamCheckByIds(
        flashcardIds: IntArray,
        rounds: Int,
        categoryName: String? = null,
        languagePair: String? = null
    ) {
        val intent = Intent(activity, ExamCheckActivity::class.java).apply {
            putExtra(FLASHCARD_IDS_KEY_INTENT, flashcardIds)
            putExtra(EXAM_ROUNDS_KEY_INTENT, rounds)
            putExtra(EXAM_CATEGORY_NAME_KEY_INTENT, categoryName)
            putExtra(EXAM_LANGUAGE_PAIR_KEY_INTENT, languagePair)
        }
        activity.startActivity(intent)
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

    fun resetMain(tabId: Int = 0) {
        val intent = Intent(activity, NavHostActivity::class.java)
        if (tabId != 0) {
            intent.putExtra(NavHostActivity.EXTRA_TAB, tabId)
        }
        activity.finish()
        activity.startActivity(intent)
    }
}
