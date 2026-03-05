package eu.qm.fiszki.dialogs.information

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.qm.fiszki.R

class InformationFlashcardDialog(context: Context) : MaterialAlertDialogBuilder(context) {
    init {
        setTitle(R.string.button_action_info)
        setMessage(R.string.info_flashcard)
        setPositiveButton(R.string.button_action_ok, null)
    }
}
