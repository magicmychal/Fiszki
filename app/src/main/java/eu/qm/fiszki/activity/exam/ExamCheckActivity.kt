package eu.qm.fiszki.activity.exam

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import eu.qm.fiszki.HapticFeedback
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.algorithm.Algorithm
import eu.qm.fiszki.dialogs.exam.EndExamDialog
import eu.qm.fiszki.dialogs.exam.ExamSummaryData
import eu.qm.fiszki.dialogs.learning.BadAnswerLearnigDialog
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.Flashcard

class ExamCheckActivity : AppCompatActivity() {

    private lateinit var mWord: TextView
    private lateinit var mLang: TextView
    private lateinit var mCategory: TextView
    private var mCurrentRound = 0
    private var mTotalRounds = 0
    private var mCorrectCount = 0
    private var mWrongCount = 0
    private lateinit var mActivity: Activity
    private lateinit var mBadAnswer: ArrayList<ArrayList<*>>
    private lateinit var mDrawnCategory: Category
    private lateinit var mDrawnFlashcard: Flashcard
    private lateinit var mTranslate: TextInputEditText
    private lateinit var mGoodAnswer: ArrayList<Flashcard>
    private lateinit var mFlashcardPools: ArrayList<Flashcard>

    private lateinit var mStatusCorrect: TextView
    private lateinit var mStatusWrong: TextView
    private lateinit var mStatusRemaining: TextView
    private lateinit var mCorrectPopup: View

    private var mExamCategoryName: String? = null
    private var mExamLanguagePair: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.activity_exam_check)

        init()
        buildToolbar()
        buildDoneKey()
        buildButtons()
        drawFlashcard()
    }

    @Suppress("UNCHECKED_CAST")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AlertDialog.Builder(mActivity)
            .setMessage(R.string.exam_check_exit_question)
            .setPositiveButton(R.string.button_action_yes) { _, _ ->
                super.onBackPressed()
                ChangeActivityManager(mActivity).exitExamCheck()
            }
            .setNegativeButton(R.string.button_action_no) { _, _ -> }
            .show()
    }

    @Suppress("UNCHECKED_CAST")
    private fun init() {
        mActivity = this
        mGoodAnswer = ArrayList()
        mBadAnswer = ArrayList()
        val extras = mActivity.intent
            .getSerializableExtra(ChangeActivityManager.EXAM_REPEAT_KEY_INTENT) as ArrayList<*>
        mFlashcardPools = extras[0] as ArrayList<Flashcard>
        mTotalRounds = extras[1] as Int
        mExamCategoryName = if (extras.size > 2) extras[2] as? String else null
        mExamLanguagePair = if (extras.size > 3) extras[3] as? String else null
        mLang = findViewById(R.id.exam_check_lang)
        mWord = findViewById(R.id.exam_check_word)
        mCategory = findViewById(R.id.exam_check_category_text)
        mTranslate = findViewById(R.id.exam_check_edit_text)
        mTranslate.inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        mStatusCorrect = findViewById(R.id.status_correct_text)
        mStatusWrong = findViewById(R.id.status_wrong_text)
        mStatusRemaining = findViewById(R.id.status_remaining_text)
        mCorrectPopup = findViewById(R.id.correct_popup_container)
        updateStatusCard()
    }

    private fun buildToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        @Suppress("DEPRECATION")
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun buildDoneKey() {
        mTranslate.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                check()
                true
            } else {
                false
            }
        }
    }

    private fun buildButtons() {
        findViewById<MaterialButton>(R.id.btn_check).setOnClickListener { check() }
    }

    private fun drawFlashcard() {
        mCurrentRound++
        mDrawnFlashcard = Algorithm(mActivity).drawCardAlgorithm(mFlashcardPools)
        mDrawnCategory = CategoryRepository(mActivity)
            .getCategoryByID(mDrawnFlashcard.categoryID)!!
        setLangText()
        setCategoryText()
        setWordText()
        mTranslate.setText("")
        mTranslate.requestFocus()
    }

    private fun setLangText() {
        mLang.text = if (mDrawnCategory.getLangFrom().isNullOrEmpty() || mDrawnCategory.getLangOn().isNullOrEmpty()) {
            getString(R.string.learning_check_lang_translate)
        } else {
            "${getString(R.string.learning_check_lang_translate_1)} ${mDrawnCategory.getLangFrom()} " +
                    "${getString(R.string.learning_check_lang_translate_2)} ${mDrawnCategory.getLangOn()}"
        }
    }

    private fun setCategoryText() {
        mCategory.text = "(${mDrawnCategory.getCategory()})"
    }

    private fun setWordText() {
        mWord.text = mDrawnFlashcard.getWord()
    }

    private fun updateStatusCard() {
        val remaining = mTotalRounds - mCurrentRound
        mStatusCorrect.text = getString(R.string.learning_check_status_correct, mCorrectCount)
        mStatusWrong.text = getString(R.string.exam_check_status_wrong, mWrongCount)
        mStatusRemaining.text = getString(R.string.exam_check_status_remaining, remaining)
    }

    private fun check() {
        val answer = mTranslate.text.toString().trim()
        val correctAnswer = mDrawnFlashcard.getTranslation()
        if (answer.equals(correctAnswer, ignoreCase = true)) {
            HapticFeedback.vibrateCorrect(mActivity)
            mGoodAnswer.add(mDrawnFlashcard)
            mCorrectCount++
            updateStatusCard()
            showCorrectPopup()
        } else {
            HapticFeedback.vibrateWrong(mActivity)
            val badAnswer = ArrayList<Any>().apply {
                add(mDrawnFlashcard)
                add(answer)
            }
            mBadAnswer.add(badAnswer)
            mWrongCount++
            updateStatusCard()
            showWrongAnswerDialog(correctAnswer, answer)
        }
    }

    private fun showWrongAnswerDialog(correctAnswer: String, userAnswer: String) {
        setButtonsEnabled(false)
        com.google.android.material.dialog.MaterialAlertDialogBuilder(mActivity)
            .setTitle(R.string.alert_title_fail)
            .setMessage(BadAnswerLearnigDialog.buildDiffMessage(mActivity, correctAnswer, userAnswer))
            .setCancelable(false)
            .setPositiveButton(R.string.button_action_ok) { dialog, _ ->
                dialog.dismiss()
                setButtonsEnabled(true)
                if (mCurrentRound == mTotalRounds) {
                    showExamSummary()
                } else {
                    drawFlashcard()
                }
            }
            .show()
    }

    private fun showExamSummary() {
        val summaryData = ExamSummaryData(
            categoryName = mExamCategoryName ?: getString(R.string.learning_category_all),
            languagePair = mExamLanguagePair,
            totalShown = mTotalRounds,
            correctCount = mCorrectCount,
            incorrectCount = mWrongCount,
            incorrectAnswers = mBadAnswer
        )
        EndExamDialog(mActivity, summaryData, mGoodAnswer).show()
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        mTranslate.isEnabled = enabled
        findViewById<MaterialButton>(R.id.btn_check).isEnabled = enabled
    }

    private fun showCorrectPopup() {
        setButtonsEnabled(false)

        mCorrectPopup.alpha = 0f
        mCorrectPopup.visibility = View.VISIBLE

        mCorrectPopup.animate()
            .alpha(1f)
            .setDuration(300L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        mCorrectPopup.animate()
                            .alpha(0f)
                            .setDuration(400L)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    mCorrectPopup.visibility = View.GONE
                                    setButtonsEnabled(true)
                                    if (mCurrentRound == mTotalRounds) {
                                        showExamSummary()
                                    } else {
                                        drawFlashcard()
                                    }
                                }
                            })
                            .start()
                    }, 1500L)
                }
            })
            .start()
    }
}
