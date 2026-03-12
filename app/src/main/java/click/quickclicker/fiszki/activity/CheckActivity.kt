package click.quickclicker.fiszki.activity

import android.app.Activity
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.textfield.TextInputEditText
import click.quickclicker.fiszki.LocalSharedPreferences
import click.quickclicker.fiszki.NightModeController
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.ui.OrientationHelper
import click.quickclicker.fiszki.activity.findCategoryColor
import click.quickclicker.fiszki.algorithm.Algorithm
import click.quickclicker.fiszki.algorithm.CatcherFlashcardToAlgorithm
import click.quickclicker.fiszki.algorithm.fsrs.FsrsRating
import click.quickclicker.fiszki.algorithm.fsrs.FsrsScheduler
import java.util.Date
import click.quickclicker.fiszki.dialogs.check.EmptyDBCheckDialog
import click.quickclicker.fiszki.dialogs.check.EmptySelectedCheckDialog
import click.quickclicker.fiszki.dialogs.check.FailCheckDialog
import click.quickclicker.fiszki.dialogs.check.PassCheckDialog
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

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
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        OrientationHelper.lockPortraitOnPhone(this)
        setContentView(R.layout.activity_check)
        handleWindowInsets()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        setToolbar()
        init()
        buildDoneKey()
        drawFlashcard()
    }

    private fun setToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.check_title)
        toolbar.setNavigationIcon(R.drawable.ic_exit_to_app_24px)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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
        mTranslate.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
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

    private fun handleWindowInsets() {
        val contentView = findViewById<View>(R.id.content_check)
        val originalBottom = contentView.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(contentView) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            v.updatePadding(bottom = originalBottom + bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun applyCategoryColor() {
        val catColor = findCategoryColor(mDrawnCategory.getColor()) ?: return
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setBackgroundColor(catColor.primary)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = false
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
        val prefs = LocalSharedPreferences(this)
        if (mTranslate.text.toString().trim() == mDrawnFlashcard.getTranslation()) {
            click.quickclicker.fiszki.HapticFeedback.vibrateCorrect(this)
            mFlashcardRepository.upFlashcardPassStatistic(mDrawnFlashcard)
            if (prefs.useFsrsAlgorithm) {
                val scheduler = FsrsScheduler()
                val updated = scheduler.schedule(mDrawnFlashcard.toFsrsCard(), FsrsRating.Good, Date())
                mDrawnFlashcard.applyFsrsCard(updated)
                mDrawnFlashcard.fsrsLastRating = FsrsRating.Good.value
                mFlashcardRepository.updateFsrsState(mDrawnFlashcard)
            } else {
                mFlashcardRepository.upFlashcardPriority(mDrawnFlashcard)
            }
            PassCheckDialog(this).show()
        } else {
            click.quickclicker.fiszki.HapticFeedback.vibrateWrong(this)
            mFlashcardRepository.upFlashcardFailStatistic(mDrawnFlashcard)
            if (prefs.useFsrsAlgorithm) {
                val scheduler = FsrsScheduler()
                val updated = scheduler.schedule(mDrawnFlashcard.toFsrsCard(), FsrsRating.Again, Date())
                mDrawnFlashcard.applyFsrsCard(updated)
                mDrawnFlashcard.fsrsLastRating = FsrsRating.Again.value
                mFlashcardRepository.updateFsrsState(mDrawnFlashcard)
            } else {
                mFlashcardRepository.downFlashcardPriority(mDrawnFlashcard)
            }
            FailCheckDialog(this, mDrawnFlashcard).show()
        }
    }
}
