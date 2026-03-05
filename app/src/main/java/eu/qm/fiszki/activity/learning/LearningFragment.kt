package eu.qm.fiszki.activity.learning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.activity.FiszkiTheme
import eu.qm.fiszki.dialogs.learning.ByCategoryLearningDialog
import eu.qm.fiszki.dialogs.learning.ByLanguageLearningDialog
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class LearningFragment : Fragment() {

    private lateinit var mFlashcardRepository: FlashcardRepository

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
        buildComposeContent(view as ComposeView)
    }

    private fun buildComposeContent(composeView: ComposeView) {
        val activity = requireActivity()

        composeView.setContent {
            FiszkiTheme {
                val colors = MaterialTheme.colorScheme
                val shapes = listOf(
                    ShapeItem(
                        label = getString(R.string.learning_try_all),
                        color = colors.primary,
                        shapeType = ShapeType.BLOB,
                        onClick = {
                            val flashcards = mFlashcardRepository.getAllFlashcards()
                            if (flashcards.isEmpty()) {
                                Toast.makeText(activity, R.string.learning_no_flashcards, Toast.LENGTH_LONG).show()
                            } else {
                                ChangeActivityManager(activity).goToLearningCheck(flashcards)
                            }
                        }
                    ),
                    ShapeItem(
                        label = getString(R.string.learning_by_language),
                        color = colors.secondary,
                        shapeType = ShapeType.ARROW,
                        onClick = {
                            ByLanguageLearningDialog(activity).show()
                        }
                    ),
                    ShapeItem(
                        label = getString(R.string.learning_by_category),
                        color = colors.tertiary,
                        shapeType = ShapeType.FLOWER,
                        onClick = {
                            ByCategoryLearningDialog(activity).show()
                        }
                    ),
                    ShapeItem(
                        label = getString(R.string.learning_chat),
                        color = colors.primaryContainer,
                        shapeType = ShapeType.HEART,
                        onClick = {
                            val flashcards = mFlashcardRepository.getAllFlashcards()
                            if (flashcards.isEmpty()) {
                                Toast.makeText(activity, R.string.learning_no_flashcards, Toast.LENGTH_LONG).show()
                            } else {
                                ChangeActivityManager(activity).goToChatMode(flashcards)
                            }
                        }
                    )
                )

                LearningScreen(
                    title = getString(R.string.learning_title),
                    shapes = shapes
                )
            }
        }
    }
}
