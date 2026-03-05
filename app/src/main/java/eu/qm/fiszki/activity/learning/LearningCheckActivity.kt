package eu.qm.fiszki.activity.learning

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.activity_learning_check)
        init()
        buildToolbar()
        buildDoneKey()
        drawFlashcard()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        ChangeActivityManager(mActivity).exitLearningCheck()
    }

    @Suppress("UNCHECKED_CAST")
    private fun init() {
        mActivity = this
        mAlgorithm = Algorithm(mActivity)
        mCategoryRepository = CategoryRepository(mActivity)
        mFlashcardRepository = FlashcardRepository(mActivity)
        mLang = mActivity.findViewById(R.id.learning_check_lang_text)
        mWord = mActivity.findViewById(R.id.learning_check_word_text)
        mCategory = mActivity.findViewById(R.id.learning_check_category_text)
        mTranslate = mActivity.findViewById(R.id.learning_check_edit_text)
        mTranslate.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        mFlashcardsPool = intent.getSerializableExtra(ChangeActivityManager.FLASHCARDS_KEY_INTENT)
            as ArrayList<Flashcard>
    }

    private fun buildToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.learning_check_toolbar_title)
        toolbar.setNavigationIcon(R.drawable.ic_exit_to_app_24px)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
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

    fun drawFlashcard() {
        mDrawnFlashcard = mAlgorithm.drawCardAlgorithm(mFlashcardsPool)
        mDrawnCategory = mCategoryRepository.getCategoryByID(mDrawnFlashcard.categoryID)!!
        setLangText()
        setCategoryText()
        setWordText()
        mTranslate.setText("")
    }

    private fun setLangText() {
        if (mDrawnCategory.getLangFrom().isNullOrEmpty() || mDrawnCategory.getLangOn().isNullOrEmpty()) {
            mLang.text = mActivity.resources.getString(R.string.learning_check_lang_translate)
        } else {
            mLang.text = "${mActivity.resources.getString(R.string.learning_check_lang_translate_1)} ${mDrawnCategory.getLangFrom()} ${mActivity.resources.getString(R.string.learning_check_lang_translate_2)} ${mDrawnCategory.getLangOn()}"
        }
    }

    private fun setCategoryText() {
        mCategory.text = "(${mDrawnCategory.getCategory()})"
    }

    private fun setWordText() {
        mWord.text = mDrawnFlashcard.getWord()
    }

    private fun check() {
        if (mTranslate.text.toString().trim() == mDrawnFlashcard.getTranslation()) {
            eu.qm.fiszki.HapticFeedback.vibrateCorrect(mActivity)
            Toast.makeText(mActivity, R.string.alert_message_pass, Toast.LENGTH_SHORT).show()
            mFlashcardRepository.upFlashcardPassStatistic(mDrawnFlashcard)
            mFlashcardRepository.upFlashcardPriority(mDrawnFlashcard)
            mTranslate.setText("")
            drawFlashcard()
        } else {
            eu.qm.fiszki.HapticFeedback.vibrateWrong(mActivity)
            mFlashcardRepository.upFlashcardFailStatistic(mDrawnFlashcard)
            mFlashcardRepository.downFlashcardPriority(mDrawnFlashcard)
            BadAnswerLearnigDialog(mActivity, mDrawnFlashcard, this).show()
        }
    }

    fun skipFlashcard(view: View) {
        drawFlashcard()
        Toast.makeText(this, R.string.learning_check_menu_skip_toast, Toast.LENGTH_SHORT).show()
    }
}
