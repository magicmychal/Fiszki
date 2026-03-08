package eu.qm.fiszki.dialogs.check

import android.app.Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import eu.qm.fiszki.R

class EmptyDBCheckDialog(private val mActivity: Activity) : MaterialAlertDialogBuilder(mActivity) {
    init {
        setTitle(R.string.alert_title_fail)
        setMessage(R.string.check_dialog_emptyDB)
        setCancelable(false)
        setPositiveButton(R.string.button_action_ok) { dialog, _ ->
            dialog.dismiss()
            mActivity.finish()
        }
    }
}
