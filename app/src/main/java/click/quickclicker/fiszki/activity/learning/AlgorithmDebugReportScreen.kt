package click.quickclicker.fiszki.activity.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.algorithm.debug.SessionCardRecord
import click.quickclicker.fiszki.algorithm.fsrs.FsrsRating
import click.quickclicker.fiszki.algorithm.fsrs.FsrsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AlgorithmDebugReportScreen(
    sessionHistory: List<SessionCardRecord>,
    useFsrs: Boolean,
    onClose: () -> Unit
) {
    val correctCount = sessionHistory.count { it.wasCorrect }
    val totalCount = sessionHistory.size
    val correctPct = if (totalCount > 0) "${(correctCount * 100 / totalCount)}%" else "0%"
    val algorithmName = if (useFsrs) stringResource(R.string.debug_algorithm_fsrs)
    else stringResource(R.string.debug_algorithm_legacy)

    val stateDistribution = if (useFsrs) {
        sessionHistory.groupBy { it.fsrsState }
            .map { (state, cards) -> "${stateLabel(state)} ${cards.size}" }
            .joinToString(", ")
    } else ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.debug_report_title),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Session summary
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.debug_summary_algorithm, algorithmName),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.debug_summary_correct, correctCount, totalCount, correctPct),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (useFsrs && stateDistribution.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.debug_summary_states, stateDistribution),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Per-card records
            itemsIndexed(sessionHistory) { index, record ->
                CardDebugRow(record, useFsrs, index + 1)
            }

            // Close button
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(stringResource(R.string.debug_report_close))
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CardDebugRow(record: SessionCardRecord, useFsrs: Boolean, index: Int) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: index + word/translation
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#$index",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.word,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = record.translation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                // Rating badge
                if (useFsrs && record.rating != null) {
                    RatingBadge(record.rating)
                } else if (record.wasSkipped) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.errorContainer
                    ) {
                        Text(
                            text = stringResource(R.string.debug_skipped),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            Spacer(Modifier.height(8.dp))

            if (useFsrs) {
                // FSRS details
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DebugField(
                        label = stringResource(R.string.debug_card_state),
                        value = stateLabel(record.fsrsState)
                    )
                    DebugField(
                        label = stringResource(R.string.debug_card_difficulty),
                        value = "${difficultyLabel(record.difficultyAfter)} (${String.format("%.1f", record.difficultyAfter)})"
                    )
                    DebugField(
                        label = stringResource(R.string.debug_card_stability),
                        value = stringResource(R.string.debug_stability_days, String.format("%.1f", record.stabilityAfter))
                    )
                    DebugField(
                        label = stringResource(R.string.debug_card_retrievability),
                        value = "${String.format("%.0f", record.retrievability * 100)}%"
                    )
                    DebugField(
                        label = stringResource(R.string.debug_card_interval),
                        value = stringResource(R.string.debug_interval_days, record.scheduledDays)
                    )
                    DebugField(
                        label = stringResource(R.string.debug_card_reps),
                        value = "${record.reps}"
                    )
                    DebugField(
                        label = stringResource(R.string.debug_card_lapses),
                        value = "${record.lapses}"
                    )
                    DebugField(
                        label = stringResource(R.string.debug_card_attempts),
                        value = "${record.attemptCount}"
                    )
                    DebugField(
                        label = stringResource(R.string.debug_card_time),
                        value = formatElapsed(record.elapsedTimeMs)
                    )
                }
            } else {
                // Legacy mode
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DebugField(
                        label = stringResource(R.string.debug_card_priority),
                        value = "${record.priority}"
                    )
                    DebugField(
                        label = stringResource(R.string.debug_card_attempts),
                        value = "${record.attemptCount}"
                    )
                    DebugField(
                        label = stringResource(R.string.debug_card_time),
                        value = formatElapsed(record.elapsedTimeMs)
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingBadge(rating: FsrsRating) {
    val (bgColor, textColor, labelRes) = when (rating) {
        FsrsRating.Easy -> Triple(
            Color(0xFF4CAF50).copy(alpha = 0.15f),
            Color(0xFF2E7D32),
            R.string.debug_rating_easy
        )
        FsrsRating.Good -> Triple(
            Color(0xFF2196F3).copy(alpha = 0.15f),
            Color(0xFF1565C0),
            R.string.debug_rating_good
        )
        FsrsRating.Hard -> Triple(
            Color(0xFFFF9800).copy(alpha = 0.15f),
            Color(0xFFE65100),
            R.string.debug_rating_hard
        )
        FsrsRating.Again -> Triple(
            Color(0xFFF44336).copy(alpha = 0.15f),
            Color(0xFFC62828),
            R.string.debug_rating_again
        )
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DebugField(label: String, value: String) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp,
            fontSize = 10.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun stateLabel(state: FsrsState): String = when (state) {
    FsrsState.New -> stringResource(R.string.debug_state_new)
    FsrsState.Learning -> stringResource(R.string.debug_state_learning)
    FsrsState.Review -> stringResource(R.string.debug_state_review)
    FsrsState.Relearning -> stringResource(R.string.debug_state_relearning)
}

@Composable
private fun difficultyLabel(d: Double): String = when {
    d < 3.0 -> stringResource(R.string.debug_difficulty_easy)
    d < 5.0 -> stringResource(R.string.debug_difficulty_medium)
    d < 7.0 -> stringResource(R.string.debug_difficulty_hard)
    else -> stringResource(R.string.debug_difficulty_very_hard)
}

private fun formatElapsed(ms: Long): String {
    val seconds = ms / 1000
    return if (seconds < 60) "${seconds}s" else "${seconds / 60}m ${seconds % 60}s"
}
