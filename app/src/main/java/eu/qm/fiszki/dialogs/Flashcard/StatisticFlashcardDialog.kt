package eu.qm.fiszki.dialogs.flashcard

import android.app.Activity
import android.text.Html
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.qm.fiszki.R
import eu.qm.fiszki.model.flashcard.Flashcard
import eu.qm.fiszki.model.flashcard.FlashcardRepository

class StatisticFlashcardDialog(
    activity: Activity,
    flashcard: Flashcard
) : MaterialAlertDialogBuilder(activity) {

    init {
        setTitle(R.string.flashcard_statistic_dialog_title)
        setMessage(Html.fromHtml(setContent(activity, flashcard), Html.FROM_HTML_MODE_LEGACY))
        setCancelable(false)
        setPositiveButton(R.string.button_action_ok) { dialog, _ ->
            dialog.dismiss()
        }
        setNeutralButton(R.string.flashcard_statistic_dialog_reset) { dialog, _ ->
            FlashcardRepository(activity).resetFlashcardStatistic(flashcard)
            dialog.dismiss()
        }
    }

    private fun setContent(activity: Activity, flashcard: Flashcard): String {
        return "${activity.resources.getString(R.string.flashcard_statistic_dialog_pass)} " +
            "<font color='#63d471'>${flashcard.staticPass}</font><br>" +
            "${activity.resources.getString(R.string.flashcard_statistic_dialog_fail)} " +
            "<font color='#d7263d'>${flashcard.staticFail}</font>"
    }
}
