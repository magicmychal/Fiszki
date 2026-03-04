package eu.qm.fiszki.activity.learning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
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
        return inflater.inflate(R.layout.activity_learning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFlashcardRepository = FlashcardRepository(requireActivity())
        buildComposeContent(view)
    }

    private fun buildComposeContent(view: View) {
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)
        val activity = requireActivity()

        val shapes = listOf(
            ShapeItem(
                label = getString(R.string.learning_try_all),
                color = Color(0xFF6750A4),
                shapeType = ShapeType.BLOB,
                onClick = {
                    ChangeActivityManager(activity).goToLearningCheck(mFlashcardRepository.getAllFlashcards())
                }
            ),
            ShapeItem(
                label = getString(R.string.learning_by_language),
                color = Color(0xFF625B71),
                shapeType = ShapeType.ARROW,
                onClick = {
                    ByLanguageLearningDialog(activity).show()
                }
            ),
            ShapeItem(
                label = getString(R.string.learning_by_category),
                color = Color(0xFF7D5260),
                shapeType = ShapeType.FLOWER,
                onClick = {
                    ByCategoryLearningDialog(activity).show()
                }
            ),
            ShapeItem(
                label = getString(R.string.learning_chat),
                color = Color(0xFFD0BCFF),
                shapeType = ShapeType.HEART,
                onClick = {
                    ChangeActivityManager(activity).goToChatMode(mFlashcardRepository.getAllFlashcards())
                }
            )
        )

        composeView.setContent {
            LearningScreen(
                title = getString(R.string.learning_title),
                shapes = shapes
            )
        }
    }
}
