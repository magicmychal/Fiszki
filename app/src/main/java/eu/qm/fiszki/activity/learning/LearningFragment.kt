package eu.qm.fiszki.activity.learning

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
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class LearningFragment : Fragment() {

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

                PracticeSetupScreen(
                    title = getString(R.string.learning_title),
                    categories = categoryItems,
                    onStartPractice = { strictMode, categoryId, reversed ->
                        val flashcards = if (categoryId == null) {
                            mFlashcardRepository.getAllFlashcards()
                        } else {
                            mFlashcardRepository.getFlashcardsByCategoryID(categoryId)
                        }
                        if (flashcards.isEmpty()) {
                            Toast.makeText(activity, R.string.learning_no_flashcards, Toast.LENGTH_LONG).show()
                        } else {
                            ChangeActivityManager(activity).goToLearningCheck(
                                flashcards = flashcards,
                                strictMode = strictMode,
                                reversed = reversed
                            )
                        }
                    }
                )
            }
        }
    }
}
