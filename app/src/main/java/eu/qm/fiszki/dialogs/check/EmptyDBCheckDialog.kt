package eu.qm.fiszki.dialogs.check

import android.app.Activity
import com.afollestad.materialdialogs.MaterialDialog
import eu.qm.fiszki.R

class EmptyDBCheckDialog(private val mActivity: Activity) : MaterialDialog.Builder(mActivity) {

    init {
        title(R.string.alert_title_fail)
        content(R.string.check_dialog_emptyDB)

        positiveText(R.string.button_action_ok)
        positiveColor(context.resources.getColor(R.color.ColorPrimaryDark))
        autoDismiss(false)
        onPositive(okClick())
    }

    private fun okClick(): MaterialDialog.SingleButtonCallback {
        return MaterialDialog.SingleButtonCallback { dialog, _ ->
            dialog.dismiss()
            mActivity.finish()
        }
    }
}
