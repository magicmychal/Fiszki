package eu.qm.fiszki.activity.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.activity.FiszkiTheme
import eu.qm.fiszki.activity.learning.PracticeCategoryItem
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class ExamFragment : Fragment() {

    private lateinit var mFlashcardRepository: FlashcardRepository
    private lateinit var mCategoryRepository: CategoryRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFlashcardRepository = FlashcardRepository(requireActivity())
        mCategoryRepository = CategoryRepository(requireActivity())
        buildComposeContent(view as ComposeView)
    }

    private fun buildComposeContent(composeView: ComposeView) {
        val activity = requireActivity()

        composeView.setContent {
            FiszkiTheme {
                val allCategories = mCategoryRepository.getAllCategory()
                val categoryItems = buildList {
                    add(
                        PracticeCategoryItem(
                            id = null,
                            displayName = getString(R.string.learning_category_all),
                            langFrom = null,
                            langOn = null
                        )
                    )
                    allCategories.forEach { cat ->
                        add(
                            PracticeCategoryItem(
                                id = cat.id,
                                displayName = cat.getCategory(),
                                langFrom = cat.getLangFrom(),
                                langOn = cat.getLangOn()
                            )
                        )
                    }
                }

                val roundsOptions = listOf(5, 10, 15, 25, 50).map {
                    RoundsOption(value = it, label = it.toString())
                }

                ExamSetupScreen(
                    title = getString(R.string.exam_title),
                    categories = categoryItems,
                    roundsOptions = roundsOptions,
                    onStartExam = { strictMode, categoryId, reversed, rounds ->
                        val flashcards = if (categoryId == null) {
                            mFlashcardRepository.getAllFlashcards()
                        } else {
                            mFlashcardRepository.getFlashcardsByCategoryID(categoryId)
                        }
                        if (flashcards.isEmpty()) {
                            Toast.makeText(activity, R.string.exam_no_flashcards, Toast.LENGTH_LONG).show()
                        } else {
                            val categoryName = if (categoryId == null) {
                                getString(R.string.learning_category_all)
                            } else {
                                mCategoryRepository.getCategoryByID(categoryId)?.getCategory()
                            }
                            val category = if (categoryId != null) mCategoryRepository.getCategoryByID(categoryId) else null
                            val languagePair = if (category != null && !category.getLangFrom().isNullOrEmpty() && !category.getLangOn().isNullOrEmpty()) {
                                val from = if (reversed) category.getLangOn() else category.getLangFrom()
                                val to = if (reversed) category.getLangFrom() else category.getLangOn()
                                "$from to $to"
                            } else null
                            ChangeActivityManager(activity).goToExamCheck(flashcards, rounds, categoryName, languagePair)
                        }
                    }
                )
            }
        }
    }
}
