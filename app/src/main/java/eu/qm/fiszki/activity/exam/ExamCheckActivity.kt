package eu.qm.fiszki.activity.exam

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import eu.qm.fiszki.NightModeController
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.algorithm.Algorithm
import eu.qm.fiszki.dialogs.exam.EndExamDialog
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.Flashcard

class ExamCheckActivity : AppCompatActivity() {

    private lateinit var mWord: TextView
    private lateinit var mLang: TextView
    private var mCuntRepeat = 0
    private var mNuberOfRepeat = 0
    private lateinit var mActivity: Activity
    private lateinit var mBadAnswer: ArrayList<ArrayList<*>>
    private lateinit var mRepreatCunter: TextView
    private lateinit var mDrawnCategory: Category
    private lateinit var mDrawnFlashcard: Flashcard
    private lateinit var mTranslate: TextInputEditText
    private lateinit var mGoodAnswer: ArrayList<Flashcard>
    private lateinit var mFlashcardPools: ArrayList<Flashcard>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NightModeController(this).useTheme()
        setContentView(R.layout.activity_exam_check)

        init()
        buildToolbar()
        buildDoneBtn()
        drawFlashcard()
    }

    @Suppress("UNCHECKED_CAST")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AlertDialog.Builder(mActivity)
            .setMessage(R.string.exam_check_exit_question)
            .setPositiveButton(R.string.button_action_yes) { _, _ ->
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
        mNuberOfRepeat = extras[1] as Int
        mRepreatCunter = mActivity.findViewById(R.id.exam_check_cunt_repeat)
        mLang = mActivity.findViewById(R.id.exam_check_lang)
        mWord = mActivity.findViewById(R.id.exam_check_word)
        mTranslate = mActivity.findViewById(R.id.exam_check_edit_text)
        mTranslate.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    }

    private fun buildToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setTitle(R.string.exam_check_toolbar_title)
        toolbar.setNavigationIcon(R.drawable.ic_exit_to_app_24px)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun buildDoneBtn() {
        mTranslate.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                check()
                true
            } else {
                false
            }
        }
    }

    private fun drawFlashcard() {
        mCuntRepeat++
        mDrawnFlashcard = Algorithm(mActivity).drawCardAlgorithm(mFlashcardPools)
        mDrawnCategory = CategoryRepository(mActivity)
            .getCategoryByID(mDrawnFlashcard.categoryID)!!
        setRepeatCunter()
        setLangText()
        setWord()
        mTranslate.setText("")
    }

    private fun setRepeatCunter() {
        mRepreatCunter.text =
            "${mActivity.resources.getString(R.string.exam_check_repeat_qustion_1)} $mCuntRepeat ${mActivity.resources.getString(R.string.exam_check_repeat_qustion_2)} $mNuberOfRepeat"
    }

    private fun setLangText() {
        if (mDrawnCategory.getLangFrom().isNullOrEmpty() || mDrawnCategory.getLangOn().isNullOrEmpty()) {
            mLang.text = "(${mActivity.resources.getString(R.string.category_no_lang)})"
        } else {
            mLang.text = "(${mDrawnCategory.getLangFrom()}->${mDrawnCategory.getLangOn()})"
        }
    }

    private fun setWord() {
        mWord.text = mDrawnFlashcard.getWord()
    }

    private fun check() {
        if (mTranslate.text.toString() == mDrawnFlashcard.getTranslation()) {
            eu.qm.fiszki.HapticFeedback.vibrateCorrect(mActivity)
            mGoodAnswer.add(mDrawnFlashcard)
        } else {
            eu.qm.fiszki.HapticFeedback.vibrateWrong(mActivity)
            val badAnswer = ArrayList<Any>().apply {
                add(mDrawnFlashcard)
                add(mTranslate.text.toString())
            }
            mBadAnswer.add(badAnswer)
        }
        if (mCuntRepeat == mNuberOfRepeat) {
            EndExamDialog(mActivity, mBadAnswer, mGoodAnswer).show()
        } else {
            drawFlashcard()
        }
    }
}
