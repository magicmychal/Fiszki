package eu.qm.fiszki.dialogs.check

import android.content.Context
import android.text.Html
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.qm.fiszki.R
import eu.qm.fiszki.model.flashcard.Flashcard

class FailCheckDialog(
    context: Context,
    flashcard: Flashcard
) : MaterialAlertDialogBuilder(context) {
    init {
        setTitle(R.string.alert_title_fail)
        setMessage(
            Html.fromHtml(
                "${context.resources.getString(R.string.learning_check_dialog_bad_answer_1)} " +
                    "<b>${flashcard.getTranslation()}</b><br>" +
                    context.resources.getString(R.string.learning_check_dialog_bad_answer_2),
                Html.FROM_HTML_MODE_LEGACY
            )
        )
        setCancelable(false)
        setPositiveButton(R.string.button_action_ok) { dialog, _ ->
            dialog.dismiss()
        }
    }
}
