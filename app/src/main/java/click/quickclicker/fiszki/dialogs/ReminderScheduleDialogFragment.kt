package click.quickclicker.fiszki.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import click.quickclicker.fiszki.activity.FiszkiTheme

class ReminderScheduleDialogFragment : DialogFragment() {

    var onScheduleConfirmed: ((hour: Int, minute: Int, days: Set<String>) -> Unit)? = null

    private var initialHour: Int = 9
    private var initialMinute: Int = 0
    private var selectedDays: Set<String> = emptySet()

    companion object {
        private const val ARG_HOUR = "hour"
        private const val ARG_MINUTE = "minute"
        private const val ARG_DAYS = "days"

        fun newInstance(hour: Int, minute: Int, days: Set<String>): ReminderScheduleDialogFragment {
            return ReminderScheduleDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_HOUR, hour)
                    putInt(ARG_MINUTE, minute)
                    putStringArrayList(ARG_DAYS, ArrayList(days))
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialHour = arguments?.getInt(ARG_HOUR) ?: 9
        initialMinute = arguments?.getInt(ARG_MINUTE) ?: 0
        selectedDays = arguments?.getStringArrayList(ARG_DAYS)?.toSet() ?: emptySet()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FiszkiTheme {
                    ReminderScheduleDialog(
                        initialHour = initialHour,
                        initialMinute = initialMinute,
                        selectedDays = selectedDays,
                        onConfirm = { hour, minute, days ->
                            onScheduleConfirmed?.invoke(hour, minute, days)
                            dismiss()
                        },
                        onDismiss = { dismiss() }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
