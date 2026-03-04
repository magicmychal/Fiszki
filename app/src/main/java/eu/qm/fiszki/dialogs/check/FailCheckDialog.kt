package eu.qm.fiszki.dialogs.check

import android.content.Context
import android.text.Html
import com.afollestad.materialdialogs.MaterialDialog
import eu.qm.fiszki.R
import eu.qm.fiszki.model.flashcard.Flashcard

class FailCheckDialog(
    context: Context,
    flashcard: Flashcard
) : MaterialDialog.Builder(context) {

    init {
        title(R.string.alert_title_fail)
        content(
            Html.fromHtml(
                "${context.resources.getString(R.string.learning_check_dialog_bad_answer_1)} " +
                    "<b>${flashcard.getTranslation()}</b><br>" +
                    context.resources.getString(R.string.learning_check_dialog_bad_answer_2)
            )
        )

        positiveText(R.string.button_action_ok)
        positiveColor(context.resources.getColor(R.color.ColorPrimaryDark))
        autoDismiss(false)
        onPositive(okClick())
    }

    private fun okClick(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog, _ ->
            dialog.dismiss()
        }
    }
}
