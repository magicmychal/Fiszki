package eu.qm.fiszki.activity.learning

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import eu.qm.fiszki.HapticFeedback
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.algorithm.Algorithm
import eu.qm.fiszki.dialogs.learning.BadAnswerLearnigDialog
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class LearningCheckActivity : AppCompatActivity() {

    private lateinit var mLang: TextView
    private lateinit var mWord: TextView
    private lateinit var mCategory: TextView
    private lateinit var mActivity: Activity
    private lateinit var mAlgorithm: Algorithm
    private lateinit var mDrawnCategory: Category
    private lateinit var mDrawnFlashcard: Flashcard
    private lateinit var mTranslate: TextInputEditText
    private lateinit var mFlashcardsPool: ArrayList<Flashcard>
    private lateinit var mCategoryRepository: CategoryRepository
    private lateinit var mFlashcardRepository: FlashcardRepository

    private lateinit var mStatusCorrect: TextView
    private lateinit var mStatusTotal: TextView
    private lateinit var mCorrectPopup: View

    private var mCorrectCount = 0
    private var mTotalCount = 0
    private var mStrictMode = true
    private var mReversed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.activity_learning_check)
        init()
        buildToolbar()
        buildDoneKey()
        buildButtons()
        drawFlashcard()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        ChangeActivityManager(mActivity).exitLearningCheck()
    }

    @Suppress("UNCHECKED_CAST")
    private fun init() {
        mActivity = this
        mAlgorithm = Algorithm(mActivity)
        mCategoryRepository = CategoryRepository(mActivity)
        mFlashcardRepository = FlashcardRepository(mActivity)
        mLang = findViewById(R.id.learning_check_lang_text)
        mWord = findViewById(R.id.learning_check_word_text)
        mCategory = findViewById(R.id.learning_check_category_text)
        mTranslate = findViewById(R.id.learning_check_edit_text)
        mTranslate.inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        mStatusCorrect = findViewById(R.id.status_correct_text)
        mStatusTotal = findViewById(R.id.status_total_text)
        mCorrectPopup = findViewById(R.id.correct_popup_container)
        @Suppress("DEPRECATION")
        mFlashcardsPool = intent.getSerializableExtra(ChangeActivityManager.FLASHCARDS_KEY_INTENT)
                as ArrayList<Flashcard>
        mStrictMode = intent.getBooleanExtra(ChangeActivityManager.STRICT_MODE_KEY_INTENT, true)
        mReversed = intent.getBooleanExtra(ChangeActivityManager.REVERSED_KEY_INTENT, false)
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
        @Suppress("DEPRECATION")
        findViewById<MaterialButton>(R.id.btn_finish).setOnClickListener { onBackPressed() }
        findViewById<MaterialButton>(R.id.btn_skip).setOnClickListener { drawFlashcard() }
        findViewById<MaterialButton>(R.id.btn_check).setOnClickListener { check() }
    }

    fun drawFlashcard() {
        mDrawnFlashcard = mAlgorithm.drawCardAlgorithm(mFlashcardsPool)
        mDrawnCategory = mCategoryRepository.getCategoryByID(mDrawnFlashcard.categoryID)!!
        setLangText()
        setCategoryText()
        setWordText()
        mTranslate.setText("")
        focusAnswerInput()
    }

    private fun focusAnswerInput() {
        mTranslate.requestFocus()
        mTranslate.post {
            val imm = getSystemService(InputMethodManager::class.java)
            imm?.showSoftInput(mTranslate, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun setLangText() {
        val langFrom = if (mReversed) mDrawnCategory.getLangOn() else mDrawnCategory.getLangFrom()
        val langOn = if (mReversed) mDrawnCategory.getLangFrom() else mDrawnCategory.getLangOn()
        mLang.text = if (langFrom.isNullOrEmpty() || langOn.isNullOrEmpty()) {
            getString(R.string.learning_check_lang_translate)
        } else {
            "${getString(R.string.learning_check_lang_translate_1)} $langFrom " +
                    "${getString(R.string.learning_check_lang_translate_2)} $langOn"
        }
    }

    private fun setCategoryText() {
        mCategory.text = "(${mDrawnCategory.getCategory()})"
    }

    private fun setWordText() {
        mWord.text = if (mReversed) mDrawnFlashcard.getTranslation() else mDrawnFlashcard.getWord()
    }

    private fun updateStatusCard() {
        mStatusCorrect.text = getString(R.string.learning_check_status_correct, mCorrectCount)
        mStatusTotal.text = getString(R.string.learning_check_status_total, mTotalCount)
    }

    private fun check() {
        val answer = mTranslate.text.toString().trim()
        val expectedAnswer = if (mReversed) mDrawnFlashcard.getWord() else mDrawnFlashcard.getTranslation()
        val checker = eu.qm.fiszki.Checker()
        if (checker.check(expectedAnswer, answer, mStrictMode)) {
            HapticFeedback.vibrateCorrect(mActivity)
            mFlashcardRepository.upFlashcardPassStatistic(mDrawnFlashcard)
            mFlashcardRepository.upFlashcardPriority(mDrawnFlashcard)
            mCorrectCount++
            mTotalCount++
            updateStatusCard()
            showCorrectPopup()
        } else {
            HapticFeedback.vibrateWrong(mActivity)
            mFlashcardRepository.upFlashcardFailStatistic(mDrawnFlashcard)
            mFlashcardRepository.downFlashcardPriority(mDrawnFlashcard)
            mTotalCount++
            updateStatusCard()
            BadAnswerLearnigDialog(mActivity, mDrawnFlashcard, this, expectedAnswer, answer)
        }
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        mTranslate.isEnabled = enabled
        findViewById<MaterialButton>(R.id.btn_finish).isEnabled = enabled
        findViewById<MaterialButton>(R.id.btn_skip).isEnabled = enabled
        findViewById<MaterialButton>(R.id.btn_check).isEnabled = enabled
    }

    private fun showCorrectPopup() {
        setButtonsEnabled(false)

        mCorrectPopup.alpha = 0f
        mCorrectPopup.visibility = View.VISIBLE

        // Fade in over 300 ms
        mCorrectPopup.animate()
            .alpha(1f)
            .setDuration(300L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Hold for 1 500 ms then fade out
                    Handler(Looper.getMainLooper()).postDelayed({
                        mCorrectPopup.animate()
                            .alpha(0f)
                            .setDuration(400L)
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationEnd(animation: Animator) {
                                    mCorrectPopup.visibility = View.GONE
                                    setButtonsEnabled(true)
                                    drawFlashcard()
                                }
                            })
                            .start()
                    }, 1500L)
                }
            })
            .start()
    }
}
