package eu.qm.fiszki.dialogs.flashcard

import android.app.Activity
import android.text.Html
import com.afollestad.materialdialogs.MaterialDialog
import eu.qm.fiszki.R
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class StatisticFlashcardDialog(
    activity: Activity,
    flashcard: Flashcard
) : MaterialDialog.Builder(activity) {

    init {
        title(R.string.flashcard_statistic_dialog_title)
        content(Html.fromHtml(setContent(activity, flashcard)))

        autoDismiss(false)

        positiveColor(activity.resources.getColor(R.color.ColorPrimaryDark))
        positiveText(R.string.button_action_ok)
        onPositive(okClick())

        neutralColor(activity.resources.getColor(R.color.md_red_A700))
        neutralText(R.string.flashcard_statistic_dialog_reset)
        onNeutral(resetClick(activity, flashcard))
    }

    private fun setContent(activity: Activity, flashcard: Flashcard): String {
        return "${activity.resources.getString(R.string.flashcard_statistic_dialog_pass)} " +
            "<font color='#63d471'>${flashcard.staticPass}</font><br>" +
            "${activity.resources.getString(R.string.flashcard_statistic_dialog_fail)} " +
            "<font color='#d7263d'>${flashcard.staticFail}</font>"
    }

    private fun resetClick(activity: Activity, flashcard: Flashcard): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog, _ ->
            FlashcardRepository(activity).resetFlashcardStatistic(flashcard)
            dialog.dismiss()
        }
    }

    private fun okClick(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog, _ ->
            dialog.dismiss()
        }
    }
}
