package click.quickclicker.fiszki.dialogs.flashcard

import android.app.Activity
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.myWords.CategoryManagerSingleton
import click.quickclicker.fiszki.activity.myWords.flashcards.SelectedFlashcardsSingleton
import click.quickclicker.fiszki.model.category.Category
import click.quickclicker.fiszki.model.category.CategoryRepository
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository

class TransformFlashcardDialog(private val mActivity: Activity) : MaterialAlertDialogBuilder(mActivity) {

    private val customView = LayoutInflater.from(mActivity).inflate(R.layout.flashcard_transform_dialog, null)
    private val mSpinner: MaterialAutoCompleteTextView = customView.findViewById(R.id.transform_spinner)
    private val mCategoryRepository = CategoryRepository(mActivity)
    private val mCategories: List<Category>
    private var mSelectedCategory: Category? = null

    init {
        mCategories = mCategoryRepository.getUserCategory()
        val names = mCategories.map { it.getCategory() }

        // pre-select the current category
        val currentIndex = mCategories.indexOfFirst { it.id == CategoryManagerSingleton.currentCategoryId }
            .coerceAtLeast(0)
        mSelectedCategory = mCategories.getOrNull(currentIndex)

        mSpinner.setAdapter(ArrayAdapter(mActivity, android.R.layout.simple_dropdown_item_1line, names))
        mSpinner.setText(names.getOrElse(currentIndex) { "" }, false)
        mSpinner.setOnItemClickListener { _, _, position, _ ->
            mSelectedCategory = mCategories.getOrNull(position)
        }

        setTitle(R.string.flashcard_transform_title)
        setIcon(ContextCompat.getDrawable(mActivity, R.drawable.ic_transform_black))
        setView(customView)
        setCancelable(true)
        setPositiveButton(R.string.flashcard_transform_button) { _, _ ->
            val target = mSelectedCategory ?: mCategories.firstOrNull() ?: return@setPositiveButton
            val flashcardRepository = FlashcardRepository(mActivity)
            for (card in SelectedFlashcardsSingleton.getFlashcards()) {
                card.categoryID = target.id
                flashcardRepository.updateFlashcard(card)
            }
        }
        setNegativeButton(android.R.string.cancel, null)
    }
}
