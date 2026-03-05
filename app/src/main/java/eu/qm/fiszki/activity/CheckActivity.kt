package eu.qm.fiszki.activity

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.findCategoryColor
import eu.qm.fiszki.algorithm.Algorithm
import eu.qm.fiszki.algorithm.CatcherFlashcardToAlgorithm
import eu.qm.fiszki.dialogs.check.EmptyDBCheckDialog
import eu.qm.fiszki.dialogs.check.EmptySelectedCheckDialog
import eu.qm.fiszki.dialogs.check.FailCheckDialog
import eu.qm.fiszki.dialogs.check.PassCheckDialog
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class CheckActivity : AppCompatActivity() {

    private lateinit var mActivity: Activity
    private lateinit var mAlgorithm: Algorithm
    private lateinit var mCategoryRepository: CategoryRepository
    private lateinit var mFlashcardRepository: FlashcardRepository
    private lateinit var mLang: TextView
    private lateinit var mWord: TextView
    private lateinit var mCategory: TextView
    private lateinit var mTranslate: TextInputEditText
    private lateinit var mDrawnFlashcard: Flashcard
    private lateinit var mDrawnCategory: Category
    private lateinit var mPool: ArrayList<Flashcard>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.activity_check)
        setToolbar()
        init()
        buildDoneKey()
        drawFlashcard()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
    }

    private fun setToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.check_title)
        toolbar.setNavigationIcon(R.drawable.ic_exit_to_app_24px)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun init() {
        mActivity = this
        mAlgorithm = Algorithm(mActivity)
        mCategoryRepository = CategoryRepository(mActivity)
        mFlashcardRepository = FlashcardRepository(mActivity)
        mLang = mActivity.findViewById(R.id.check_lang_text)
        mWord = mActivity.findViewById(R.id.check_word_text)
        mCategory = mActivity.findViewById(R.id.check_category_text)
        mTranslate = mActivity.findViewById(R.id.check_edit_text)
        mTranslate.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        mPool = CatcherFlashcardToAlgorithm(mActivity).getFlashcardsFromChosenCategoryToNotification()
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
        if (isCondition()) {
            mDrawnFlashcard = mAlgorithm.drawCardAlgorithm(mPool)
            mDrawnCategory = mCategoryRepository.getCategoryByID(mDrawnFlashcard.categoryID)!!
            applyCategoryColor()
            setLangText()
            setCategoryText()
            setWordText()
            mTranslate.setText("")
        }
    }

    private fun applyCategoryColor() {
        val catColor = findCategoryColor(mDrawnCategory.getColor()) ?: return
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setBackgroundColor(catColor.primary)
        window.statusBarColor = catColor.primary
    }

    private fun isCondition(): Boolean {
        if (mFlashcardRepository.getAllFlashcards().isEmpty()) {
            EmptyDBCheckDialog(this).show()
            return false
        } else if (mPool.isEmpty()) {
            EmptySelectedCheckDialog(this).show()
            mPool = mFlashcardRepository.getAllFlashcards()
        }
        return true
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
            eu.qm.fiszki.HapticFeedback.vibrateCorrect(this)
            mFlashcardRepository.upFlashcardPassStatistic(mDrawnFlashcard)
            mFlashcardRepository.upFlashcardPriority(mDrawnFlashcard)
            PassCheckDialog(this).show()
        } else {
            eu.qm.fiszki.HapticFeedback.vibrateWrong(this)
            mFlashcardRepository.upFlashcardFailStatistic(mDrawnFlashcard)
            mFlashcardRepository.downFlashcardPriority(mDrawnFlashcard)
            FailCheckDialog(this, mDrawnFlashcard).show()
        }
    }
}
