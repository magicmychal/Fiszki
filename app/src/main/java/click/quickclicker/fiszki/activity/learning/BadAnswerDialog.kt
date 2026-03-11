package click.quickclicker.fiszki.activity.learning

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.ui.buildCorrectAnswerAnnotated
import click.quickclicker.fiszki.ui.buildUserAnswerAnnotated

@Composable
fun BadAnswerDialog(
    expectedAnswer: String,
    userAnswer: String,
    onRetry: () -> Unit,
    onSkip: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.learning_check_dialog_retry_btn))
            }
        },
        dismissButton = {
            TextButton(onClick = onSkip) {
                Text(stringResource(R.string.learning_check_dialog_skip_btn_new))
            }
        },
        text = {
            Column {
                Text(
                    stringResource(R.string.learning_check_dialog_your_answer_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    buildUserAnswerAnnotated(userAnswer, expectedAnswer),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    stringResource(R.string.learning_check_dialog_bad_answer_1_new),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    buildCorrectAnswerAnnotated(expectedAnswer, userAnswer),
                    fontSize = 20.sp
                )
            }
        }
    )
}
