package click.quickclicker.fiszki.dialogs.check

import android.app.Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import click.quickclicker.fiszki.R

class EmptySelectedCheckDialog(mActivity: Activity) : MaterialAlertDialogBuilder(mActivity) {
    init {
        setTitle(R.string.alert_title_fail)
        setMessage(R.string.check_dialog_empty_selected)
        setPositiveButton(R.string.button_action_ok, null)
    }
}
