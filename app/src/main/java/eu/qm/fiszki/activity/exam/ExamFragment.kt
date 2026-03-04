package eu.qm.fiszki.activity.exam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.ChangeActivityManager
import eu.qm.fiszki.activity.FiszkiTheme
import eu.qm.fiszki.activity.learning.LearningScreen
import eu.qm.fiszki.activity.learning.ShapeItem
import eu.qm.fiszki.activity.learning.ShapeType
import eu.qm.fiszki.model.category.Category
import eu.qm.fiszki.model.category.CategoryRepository
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class ExamFragment : Fragment() {

    private var selectedCategory = mutableStateOf<Category?>(null)
    private var selectedRepeat = mutableStateOf<Int?>(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildComposeContent(view as ComposeView)
    }

    private fun buildComposeContent(composeView: ComposeView) {
        val activity = requireActivity()
        val categoryRepository = CategoryRepository(activity)
        val flashcardRepository = FlashcardRepository(activity)

        composeView.setContent {
            FiszkiTheme {
                val colors = MaterialTheme.colorScheme
                val category = selectedCategory.value
                val repeat = selectedRepeat.value

                val rangeLabel = category?.getCategory()
                    ?: getString(R.string.exam_card_range_title)
                val repeatLabel = repeat?.toString()
                    ?: getString(R.string.exam_card_repeat_title)

                val shapes = mutableListOf(
                    ShapeItem(
                        label = repeatLabel,
                        color = colors.secondary,
                        shapeType = ShapeType.ARROW,
                        tooltip = getString(R.string.exam_tooltip_rounds),
                        onClick = {
                            showRepeatPicker()
                        }
                    ),
                    ShapeItem(
                        label = rangeLabel,
                        color = colors.tertiary,
                        shapeType = ShapeType.FLOWER,
                        tooltip = getString(R.string.exam_tooltip_range),
                        onClick = {
                            showRangePicker(categoryRepository)
                        }
                    )
                )

                if (category != null && repeat != null) {
                    shapes.add(
                        ShapeItem(
                            label = getString(R.string.exam_start),
                            color = colors.primary,
                            shapeType = ShapeType.BLOB,
                            onClick = {
                                val flashcards = flashcardRepository
                                    .getFlashcardsByCategoryID(category.id)
                                if (flashcards.isEmpty()) {
                                    Toast.makeText(activity, R.string.exam_range_empty_toast, Toast.LENGTH_LONG).show()
                                } else {
                                    ChangeActivityManager(activity).goToExamCheck(flashcards, repeat)
                                }
                            }
                        )
                    )
                }

                LearningScreen(
                    title = getString(R.string.exam_title),
                    shapes = shapes
                )
            }
        }
    }

    private fun showRepeatPicker() {
        val values = listOf(5, 10, 15, 25, 50)
        val items = values.map { it.toString() }.toTypedArray()
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.exam_repeat_dialog_title)
            .setItems(items) { _, which ->
                selectedRepeat.value = values[which]
            }
            .show()
    }

    private fun showRangePicker(categoryRepository: CategoryRepository) {
        val categories = categoryRepository.getAllCategory()
        val names = categories.map { it.getCategory() }.toTypedArray()
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.exam_range_dialog_title)
            .setItems(names) { _, which ->
                selectedCategory.value = categories[which]
            }
            .show()
    }
}
