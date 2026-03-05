package eu.qm.fiszki

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.Html
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.qm.fiszki.activity.NavHostActivity

class Alert {

    fun pass(context: Context, message: String, title: String, nameButton: String) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(nameButton) { _, _ -> (context as Activity).finish() }
            .show()
    }

    fun fail(
        context: Context, orginalWord: String, message: String, messageCorrectIs: String,
        messeageAgain: String, title: String, nameButton: String
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(
                Html.fromHtml(
                    "$message $messageCorrectIs <b>\"$orginalWord\"</b>. \n$messeageAgain",
                    Html.FROM_HTML_MODE_LEGACY
                )
            )
            .setCancelable(false)
            .setPositiveButton(nameButton) { _, _ -> }
            .show()
    }

    fun buildAlert(title: String, message: String, buttonText: String, activity: Activity) {
        MaterialAlertDialogBuilder(activity)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(buttonText) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    fun emptyBase(context: Context, message: String, title: String, nameButton: String) {
        MaterialAlertDialogBuilder(context)
            .setTitle("")
            .setCancelable(false)
            .setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))
            .setPositiveButton(nameButton) { _, _ ->
                val intent = Intent(context, NavHostActivity::class.java)
                context.startActivity(intent)
                (context as Activity).finish()
            }
            .show()
    }

    fun addFiszkiToFeature(activity: Activity): MaterialAlertDialogBuilder {
        return MaterialAlertDialogBuilder(activity)
            .setTitle(activity.resources.getString(R.string.alert_no_category_title))
            .setMessage(activity.resources.getString(R.string.alert_no_category_messege))
            .setPositiveButton(activity.resources.getString(R.string.button_action_ok), null)
    }
}
