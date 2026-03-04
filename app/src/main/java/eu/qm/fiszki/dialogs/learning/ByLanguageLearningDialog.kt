package eu.qm.fiszki.dialogs.learning

import android.app.Activity
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.jaredrummler.materialspinner.MaterialSpinner
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class ByLanguageLearningDialog(private val mActivity: Activity) : MaterialDialog.Builder(mActivity) {

    private lateinit var mSpinnerFrom: MaterialSpinner
    private lateinit var mSpinnerOn: MaterialSpinner
    private lateinit var mCategoryRepository: CategoryRepository
    private lateinit var mFlashcardRepository: FlashcardRepository
    private var mLearningCategory: ArrayList<Category> = ArrayList()
    private var mLearningFlashcards: ArrayList<Flashcard> = ArrayList()

    init {
        title(R.string.learning_by_lang_dialog_title)
        customView(R.layout.learning_by_lang_dialog, false)
        autoDismiss(false)
        neutralText(R.string.learning_by_category_dialog_btn_back)
        onNeutral(closeDialog())
        positiveText(R.string.learning_by_category_dialog_btn_to_learning)
        positiveColor(mActivity.resources.getColor(R.color.ColorPrimaryDark))
        onPositive(goLearning())
        initRepos()
        buildSpinnerFrom()
        buildSpinnerOn()
    }

    private fun initRepos() {
        mCategoryRepository = CategoryRepository(mActivity)
        mFlashcardRepository = FlashcardRepository(mActivity)
    }

    private fun buildSpinnerFrom() {
        mSpinnerFrom = customView.findViewById(R.id.spinner_langFrom) as MaterialSpinner
        val langFromArray = ArrayList<String>()
        for (cat in mCategoryRepository.getUserCategory()) {
            if (!cat.getLangFrom().isNullOrEmpty()) {
                langFromArray.add(cat.getLangFrom()!!)
            }
        }
        // delete recurrence
        val hs = HashSet<String>()
        hs.addAll(langFromArray)
        langFromArray.clear()
        langFromArray.add(context.getString(R.string.learning_by_lang_whichever))
        langFromArray.addAll(hs)
        mSpinnerFrom.setItems(langFromArray)
    }

    private fun buildSpinnerOn() {
        mSpinnerOn = customView.findViewById(R.id.spinner_langOn) as MaterialSpinner
        val langOnArray = ArrayList<String>()
        for (cat in mCategoryRepository.getUserCategory()) {
            if (!cat.getLangOn().isNullOrEmpty()) {
                langOnArray.add(cat.getLangOn()!!)
            }
        }
        // delete recurrence
        val hs = HashSet<String>()
        hs.addAll(langOnArray)
        langOnArray.clear()
        langOnArray.add(context.getString(R.string.learning_by_lang_whichever))
        langOnArray.addAll(hs)
        mSpinnerOn.setItems(langOnArray)
    }

    private fun goLearning(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog, _ ->
            setChosenLang()
            if (mLearningCategory.isEmpty()) {
                Toast.makeText(context, R.string.learning_by_lang_tost_empty_chose, Toast.LENGTH_LONG).show()
            } else {
                setFlashcard()
                if (mLearningFlashcards.isEmpty()) {
                    Toast.makeText(context, R.string.learning_by_lang_tost_empty_chose, Toast.LENGTH_LONG).show()
                } else {
                    dialog.dismiss()
                    ChangeActivityManager(mActivity).goToLearningCheck(mLearningFlashcards)
                }
            }
        }
    }

    private fun setChosenLang() {
        when {
            mSpinnerFrom.selectedIndex == 0 && mSpinnerOn.selectedIndex == 0 ->
                mLearningCategory.addAll(mCategoryRepository.getAllCategory())
            mSpinnerFrom.selectedIndex == 0 ->
                mLearningCategory.addAll(mCategoryRepository.getCategoryByLangOn(mSpinnerOn.text.toString()))
            mSpinnerOn.selectedIndex == 0 ->
                mLearningCategory.addAll(mCategoryRepository.getCategoryByLangFrom(mSpinnerFrom.text.toString()))
            else ->
                mLearningCategory.addAll(
                    mCategoryRepository.getCategoryByLang(
                        mSpinnerFrom.text.toString(),
                        mSpinnerOn.text.toString()
                    )
                )
        }
    }

    private fun setFlashcard() {
        for (cat in mLearningCategory) {
            mLearningFlashcards.addAll(mFlashcardRepository.getFlashcardsByCategoryID(cat.id))
        }
    }

    private fun closeDialog(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog, _ ->
            dialog.dismiss()
        }
    }
}
