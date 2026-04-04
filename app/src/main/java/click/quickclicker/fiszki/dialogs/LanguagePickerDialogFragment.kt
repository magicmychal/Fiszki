package click.quickclicker.fiszki.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.DialogFragment
import click.quickclicker.fiszki.activity.FiszkiTheme

/**
 * DialogFragment that hosts [LanguagePickerDialog] via Compose.
 *
 * Used on API < 33 where there is no system per-app language settings page.
 */
class LanguagePickerDialogFragment : DialogFragment() {

    /**
     * Called when the user confirms a language. Receives the BCP 47 tag
     * ("en", "pl") or empty string for system default.
     */
    var onLanguageSelected: ((tag: String) -> Unit)? = null

    private var currentTag: String = ""

    companion object {
        private const val ARG_CURRENT_TAG = "current_tag"

        fun newInstance(currentTag: String): LanguagePickerDialogFragment {
            return LanguagePickerDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CURRENT_TAG, currentTag)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentTag = arguments?.getString(ARG_CURRENT_TAG) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                FiszkiTheme {
                    LanguagePickerDialog(
                        currentTag = currentTag,
                        onConfirm = { tag ->
                            onLanguageSelected?.invoke(tag)
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

