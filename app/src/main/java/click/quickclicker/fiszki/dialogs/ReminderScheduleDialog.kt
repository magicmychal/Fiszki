package click.quickclicker.fiszki.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import click.quickclicker.fiszki.R

data class DayToggle(
    val dayIndex: Int,  // 1=Mon..7=Sun (stored in prefs)
    val initialRes: Int
)

private val dayToggles = listOf(
    DayToggle(7, R.string.day_initial_sunday),
    DayToggle(1, R.string.day_initial_monday),
    DayToggle(2, R.string.day_initial_tuesday),
    DayToggle(3, R.string.day_initial_wednesday),
    DayToggle(4, R.string.day_initial_thursday),
    DayToggle(5, R.string.day_initial_friday),
    DayToggle(6, R.string.day_initial_saturday),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScheduleDialog(
    initialHour: Int,
    initialMinute: Int,
    selectedDays: Set<String>,
    onConfirm: (hour: Int, minute: Int, days: Set<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    val daySelection = remember {
        mutableStateListOf<Boolean>().apply {
            dayToggles.forEach { toggle ->
                add(selectedDays.contains(toggle.dayIndex.toString()))
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val days = mutableSetOf<String>()
                dayToggles.forEachIndexed { i, toggle ->
                    if (daySelection[i]) days.add(toggle.dayIndex.toString())
                }
                onConfirm(timePickerState.hour, timePickerState.minute, days)
            }) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        },
        title = {
            Text(stringResource(R.string.settings_notification_time))
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                TimePicker(state = timePickerState)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    dayToggles.forEachIndexed { index, toggle ->
                        val selected = daySelection[index]
                        val bgColor = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        }
                        val textColor = if (selected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(bgColor)
                                .clickable { daySelection[index] = !selected },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(toggle.initialRes),
                                color = textColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    )
}
