package click.quickclicker.fiszki.dialogs.flashcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.FiszkiTheme
import click.quickclicker.fiszki.activity.learning.RobotoSerifFamily
import click.quickclicker.fiszki.model.flashcard.Flashcard
import click.quickclicker.fiszki.model.flashcard.FlashcardRepository
import click.quickclicker.fiszki.model.flashcard.ValidationFlashcards

class EditFlashcardDialogFragment : DialogFragment() {

    var onDismissed: (() -> Unit)? = null
    var onDeleted: ((Flashcard) -> Unit)? = null

    private var flashcardId: Int = 0
    private var flashcard: Flashcard? = null

    companion object {
        private const val ARG_FLASHCARD_ID = "flashcard_id"

        fun newInstance(flashcardId: Int): EditFlashcardDialogFragment {
            return EditFlashcardDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_FLASHCARD_ID, flashcardId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        flashcardId = arguments?.getInt(ARG_FLASHCARD_ID) ?: 0
        flashcard = FlashcardRepository(requireContext()).getFlashcardByID(flashcardId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FiszkiTheme {
                    val fc = flashcard
                    if (fc != null) {
                        EditFlashcardDialogContent(
                            flashcard = fc,
                            onSave = { word, translation ->
                                fc.setWord(word)
                                fc.setTranslation(translation)
                                val validation = ValidationFlashcards(requireContext())
                                if (validation.validateAdd(fc)) {
                                    FlashcardRepository(requireContext()).updateFlashcard(fc)
                                    Toast.makeText(requireContext(), R.string.flashcard_edit_toast, Toast.LENGTH_LONG).show()
                                    dismiss()
                                    onDismissed?.invoke()
                                }
                            },
                            onDelete = {
                                FlashcardRepository(requireContext()).deleteFlashcard(fc)
                                dismiss()
                                onDeleted?.invoke(fc)
                                onDismissed?.invoke()
                            },
                            onCancel = {
                                dismiss()
                                onDismissed?.invoke()
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}

@Composable
private fun EditFlashcardDialogContent(
    flashcard: Flashcard,
    onSave: (word: String, translation: String) -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    var word by remember { mutableStateOf(flashcard.getWord()) }
    var translation by remember { mutableStateOf(flashcard.getTranslation()) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = stringResource(R.string.flashcard_edit_title),
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = RobotoSerifFamily,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = word,
                onValueChange = { word = it },
                label = { Text(stringResource(R.string.add_new_flashcard_word)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = translation,
                onValueChange = { translation = it },
                label = { Text(stringResource(R.string.add_new_flashcard_translation)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { showDeleteConfirm = true }) {
                    Text(
                        stringResource(R.string.flashcard_delete_btn),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Button(onClick = { onSave(word.trim(), translation.trim()) }) {
                    Text(stringResource(R.string.flashcard_edit_done))
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            text = { Text(stringResource(R.string.flashcard_delete_message)) },
            confirmButton = {
                TextButton(onClick = onDelete) {
                    Text(stringResource(R.string.button_action_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(stringResource(R.string.button_action_no))
                }
            }
        )
    }
}
