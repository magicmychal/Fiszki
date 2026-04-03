package click.quickclicker.fiszki.activity.exam

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.learning.PracticeCategoryItem
import io.sentry.compose.SentryTraced
import io.sentry.compose.SentryModifier.sentryTag

data class RoundsOption(val value: Int, val label: String)

/** Sentinel value indicating "test all cards in the set". */
const val EXAM_ROUNDS_ALL = -1

@OptIn(ExperimentalLayoutApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun ExamSetupScreen(
    title: String,
    categories: List<PracticeCategoryItem>,
    onStartExam: (strictMode: Boolean, categoryId: Int?, reversed: Boolean, rounds: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var strictMode by remember { mutableStateOf(true) }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var reversed by remember { mutableStateOf(false) }
    var selectedRoundsIndex by remember { mutableIntStateOf(0) }

    val selectedCategory = categories.getOrNull(selectedCategoryIndex)
    val isAllSelected = selectedCategory?.id == null

    val allRoundsLabel = stringResource(R.string.exam_rounds_all)
    val roundsOptions = remember(selectedCategory) {
        val cardCount = selectedCategory?.cardCount ?: 0
        buildList {
            add(RoundsOption(value = EXAM_ROUNDS_ALL, label = allRoundsLabel))
            listOf(5, 10, 15, 25, 50).forEach { n ->
                if (n <= cardCount) add(RoundsOption(value = n, label = n.toString()))
            }
        }
    }
    val selectedRounds = roundsOptions.getOrNull(selectedRoundsIndex)

    val scrollState = rememberScrollState()

    SentryTraced("exam_setup_screen") {
        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header Section
            Column(
                modifier = Modifier.padding(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.exam_setup_label).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = stringResource(R.string.exam_setup_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.exam_setup_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Session Type Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = stringResource(R.string.learning_session_type),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExamSessionTypeButton(
                            icon = Icons.Default.Bolt,
                            title = stringResource(R.string.learning_session_strict),
                            subtitle = stringResource(R.string.learning_session_strict_desc),
                            selected = strictMode,
                            onClick = { strictMode = true },
                            modifier = Modifier
                                .weight(1f)
                                .sentryTag("button_strict_mode")
                        )
                        ExamSessionTypeButton(
                            icon = Icons.Default.Psychology,
                            title = stringResource(R.string.learning_session_relaxed),
                            subtitle = stringResource(R.string.learning_session_relaxed_desc),
                            selected = !strictMode,
                            onClick = { strictMode = false },
                            modifier = Modifier
                                .weight(1f)
                                .sentryTag("button_relaxed_mode")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Focus Areas Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.learning_focus_areas),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.learning_selected_count, 1),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEachIndexed { index, category ->
                            val isSelected = index == selectedCategoryIndex
                            val chipLabel = if (category.id == null) {
                                stringResource(R.string.learning_focus_all_sets)
                            } else {
                                category.displayName
                            }
                            ExamFocusAreaChip(
                                label = chipLabel,
                                selected = isSelected,
                                onClick = {
                                    selectedCategoryIndex = index
                                    reversed = false
                                    selectedRoundsIndex = 0
                                },
                                modifier = Modifier.sentryTag("chip_category_$index")
                            )
                        }
                    }
                }
            }

            // Direction Card (conditional)
            val langFrom = selectedCategory?.langFrom
            val langOn = selectedCategory?.langOn
            val showDirection = !isAllSelected && !langFrom.isNullOrEmpty() && !langOn.isNullOrEmpty()

            AnimatedVisibility(
                visible = showDirection,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (showDirection) {
                    Column {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    text = stringResource(R.string.learning_direction_label),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    ExamDirectionChip(
                                        label = stringResource(
                                            R.string.learning_direction_format,
                                            langFrom ?: "",
                                            langOn ?: ""
                                        ),
                                        selected = !reversed,
                                        onClick = { reversed = false },
                                        modifier = Modifier
                                            .weight(1f)
                                            .sentryTag("chip_direction_normal")
                                    )
                                    ExamDirectionChip(
                                        label = stringResource(
                                            R.string.learning_direction_format,
                                            langOn ?: "",
                                            langFrom ?: ""
                                        ),
                                        selected = reversed,
                                        onClick = { reversed = true },
                                        modifier = Modifier
                                            .weight(1f)
                                            .sentryTag("chip_direction_reversed")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rounds Card
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = stringResource(R.string.exam_rounds_label),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        roundsOptions.forEachIndexed { index, option ->
                            val isSelected = index == selectedRoundsIndex
                            ExamFocusAreaChip(
                                label = option.label,
                                selected = isSelected,
                                onClick = { selectedRoundsIndex = index },
                                modifier = Modifier.sentryTag("chip_rounds_$index")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CTA Button
            Button(
                onClick = {
                    selectedRounds?.let { rounds ->
                        onStartExam(strictMode, selectedCategory?.id, reversed, rounds.value)
                    }
                },
                enabled = selectedRounds != null,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .sentryTag("button_start_exam")
            ) {
                Text(
                    text = stringResource(R.string.exam_begin_session),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ExamSessionTypeButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = contentColor,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ExamFocusAreaChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val border = if (selected) {
        null
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = containerColor,
        border = border,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor
            )
        }
    }
}

@Composable
private fun ExamDirectionChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = contentColor,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 12.dp)
        )
    }
}
