package eu.qm.fiszki.dialogs.flashcard

import android.app.Activity
import com.afollestad.materialdialogs.MaterialDialog
import com.jaredrummler.materialspinner.MaterialSpinner
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.myWords.CategoryManagerSingleton
import eu.qm.fiszki.activity.myWords.flashcards.SelectedFlashcardsSingleton
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class TransformFlashcardDialog(private val mActivity: Activity) : MaterialDialog.Builder(mActivity) {

    private lateinit var mSpinner: MaterialSpinner
    private var seletedCategory: Category? = null
    private lateinit var mCategoryRepository: CategoryRepository

    init {
        title(R.string.flashcard_transform_title)
        icon(mActivity.resources.getDrawable(R.drawable.ic_transform_black))
        customView(R.layout.flashcard_transform_dialog, false)
        positiveText(R.string.flashcard_transform_button)
        positiveColor(mActivity.resources.getColor(R.color.ColorPrimaryDark))
        onPositive(changeCategory())
        initRepos()
        buildSpinner()
    }

    private fun initRepos() {
        mCategoryRepository = CategoryRepository(mActivity)
    }

    private fun buildSpinner() {
        var cuntPosition = 0
        var findPosition = false
        val categories = mCategoryRepository.getUserCategory()
        val spinnerCategories = ArrayList<String>()
        for (cat in categories) {
            if (!findPosition) {
                if (cat.id == CategoryManagerSingleton.currentCategoryId) {
                    findPosition = true
                } else {
                    cuntPosition++
                }
            }
            spinnerCategories.add(cat.getCategory())
        }

        // zabezpieczenie przed FC; Kiedy przenosimy z braku kategori
        if (!findPosition) {
            cuntPosition = 0
        }

        mSpinner = customView.findViewById(R.id.transform_spinner) as MaterialSpinner
        mSpinner.setItems(spinnerCategories)
        mSpinner.selectedIndex = cuntPosition
        mSpinner.setOnItemSelectedListener { _, position, _, _ ->
            seletedCategory = categories[position]
        }
    }

    private fun changeCategory(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { _, _ ->
            val flashcardRepository = FlashcardRepository(mActivity)
            val flashcards = SelectedFlashcardsSingleton.getFlashcards()

            // zabezpieczenie przec FC; Gdy jest jedna kategoria i nie trzeba wybierac;
            if (seletedCategory == null) {
                seletedCategory = mCategoryRepository.getUserCategory()[0]
            }

            for (card in flashcards) {
                card.categoryID = seletedCategory!!.id
                flashcardRepository.updateFlashcard(card)
            }
        }
    }
}
