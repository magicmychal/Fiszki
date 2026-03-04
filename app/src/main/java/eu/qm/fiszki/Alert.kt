package eu.qm.fiszki

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.text.Html
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import eu.qm.fiszki.activity.MainActivity

class Alert {

    fun pass(context: Context, message: String, title: String, nameButton: String) {
        val alertDialog = AlertDialog.Builder(context).create().apply {
            setTitle(title)
            setMessage(message)
            setCancelable(false)
            setButton(nameButton) { _, _ ->
                (context as Activity).finish()
            }
        }
        alertDialog.show()
    }

    fun fail(
        context: Context, orginalWord: String, message: String, messageCorrectIs: String,
        messeageAgain: String, title: String, nameButton: String
    ) {
        val alertDialog = AlertDialog.Builder(context).create().apply {
            setTitle(title)
            setMessage(
                Html.fromHtml(
                    "$message $messageCorrectIs <b>\"$orginalWord\"</b>. \n$messeageAgain",
                    Html.FROM_HTML_MODE_LEGACY
                )
            )
            setCancelable(false)
            setButton(nameButton) { _, _ -> }
        }
        alertDialog.show()
    }

    fun buildAlert(title: String, message: String, buttonText: String, activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(buttonText) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    fun emptyBase(context: Context, message: String, title: String, nameButton: String) {
        val alertDialog = AlertDialog.Builder(context).create().apply {
            setTitle(title)
            setCancelable(false)
            setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY))
            setButton(nameButton) { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
                (context as Activity).finish()
            }
        }
        alertDialog.show()
    }

    fun addFiszkiToFeature(activity: Activity): MaterialDialog.Builder {
        return MaterialDialog.Builder(activity)
            .title(activity.resources.getString(R.string.alert_no_category_title))
            .content(activity.resources.getString(R.string.alert_no_category_messege))
            .positiveText(activity.resources.getString(R.string.button_action_ok))
            .contentGravity(GravityEnum.START)
    }
}
