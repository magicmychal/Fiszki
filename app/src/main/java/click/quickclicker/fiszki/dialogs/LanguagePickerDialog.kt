package click.quickclicker.fiszki.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import click.quickclicker.fiszki.R

/**
 * Language option for the picker dialog.
 *
 * @param tag BCP 47 language tag ("en", "pl") or empty string for system default.
 * @param displayName Composable-resolved display name.
 */
data class LanguageOption(val tag: String, val displayName: String)

/**
 * A Material 3 AlertDialog that lets the user pick the app language.
 *
 * @param currentTag Current BCP 47 tag, or empty string for system default.
 * @param onConfirm Called with the selected language tag (or "" for system default).
 * @param onDismiss Called when the dialog is dismissed without selection.
 */
@Composable
fun LanguagePickerDialog(
    currentTag: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        LanguageOption("", stringResource(R.string.settings_language_system_default)),
        LanguageOption("en", stringResource(R.string.settings_language_english)),
        LanguageOption("pl", stringResource(R.string.settings_language_polish)),
    )

    var selectedTag by remember { mutableStateOf(currentTag) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(selectedTag) }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        title = {
            Text(stringResource(R.string.settings_language))
        },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedTag = option.tag }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedTag == option.tag,
                            onClick = { selectedTag = option.tag }
                        )
                        Text(
                            text = option.displayName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    )
}

