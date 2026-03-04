package eu.qm.fiszki.activity

import android.app.Activity
import android.content.Intent
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.chat.ChatActivity
import eu.qm.fiszki.activity.exam.ExamCheckActivity
import eu.qm.fiszki.activity.exam.ExamBadAnswerActivity
import eu.qm.fiszki.activity.learning.LearningCheckActivity
import eu.qm.fiszki.model.flashcard.Flashcard

class ChangeActivityManager(private val activity: Activity) {

    companion object {
        const val FLASHCARDS_KEY_INTENT = "FLASHCARDS"
        const val EXAM_REPEAT_KEY_INTENT = "REPEAT"
        const val EXAM_BAD_ANSWER_KEY_INTENT = "RESULTS"
    }

    fun goToLearningCheck(flashcards: ArrayList<Flashcard>) {
        val goLearning = Intent(activity, LearningCheckActivity::class.java).apply {
            putExtra(FLASHCARDS_KEY_INTENT, flashcards)
        }
        activity.startActivity(goLearning)
        activity.finish()
        activity.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    fun exitLearningCheck() {
        val intent = Intent(activity, NavHostActivity::class.java).apply {
            putExtra(NavHostActivity.EXTRA_TAB, R.id.nav_learning)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        activity.startActivity(intent)
        activity.finish()
        activity.overridePendingTransition(R.anim.right_out, R.anim.left_in)
    }

    fun goToExamCheck(flashcards: ArrayList<Flashcard>, repeat: Int) {
        val bundle = ArrayList<Any>().apply {
            add(flashcards)
            add(repeat)
        }
        val goLearning = Intent(activity, ExamCheckActivity::class.java).apply {
            putExtra(EXAM_REPEAT_KEY_INTENT, bundle)
        }
        activity.startActivity(goLearning)
        activity.finish()
        activity.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    fun exitExamCheck() {
        val intent = Intent(activity, NavHostActivity::class.java).apply {
            putExtra(NavHostActivity.EXTRA_TAB, R.id.nav_exam)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        activity.startActivity(intent)
        activity.finish()
        activity.overridePendingTransition(R.anim.right_out, R.anim.left_in)
    }

    fun goToExamBadAnswer(badAnswer: ArrayList<*>) {
        val goLearning = Intent(activity, ExamBadAnswerActivity::class.java).apply {
            putExtra(EXAM_BAD_ANSWER_KEY_INTENT, badAnswer)
        }
        activity.startActivity(goLearning)
        activity.finish()
        activity.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    fun exitExamBadAnswer() {
        val intent = Intent(activity, NavHostActivity::class.java).apply {
            putExtra(NavHostActivity.EXTRA_TAB, R.id.nav_exam)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        activity.startActivity(intent)
        activity.finish()
        activity.overridePendingTransition(R.anim.right_out, R.anim.left_in)
    }

    fun goToChatMode(flashcards: ArrayList<Flashcard>) {
        val goChat = Intent(activity, ChatActivity::class.java).apply {
            putExtra(FLASHCARDS_KEY_INTENT, flashcards)
        }
        activity.startActivity(goChat)
        activity.finish()
        activity.overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    fun exitChatMode() {
        val intent = Intent(activity, NavHostActivity::class.java).apply {
            putExtra(NavHostActivity.EXTRA_TAB, R.id.nav_learning)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        activity.startActivity(intent)
        activity.finish()
        activity.overridePendingTransition(R.anim.right_out, R.anim.left_in)
    }

    fun resetMain() {
        activity.finish()
        activity.startActivity(Intent(activity, NavHostActivity::class.java))
    }
}
