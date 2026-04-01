package click.quickclicker.fiszki.activity.learning

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import click.quickclicker.fiszki.R
import click.quickclicker.fiszki.activity.FiszkiTheme
import io.sentry.compose.SentryTraced
import io.sentry.compose.SentryModifier.sentryTag

data class PracticeCategoryItem(
    val id: Int?,
    val displayName: String,
    val langFrom: String?,
    val langOn: String?
)

@OptIn(ExperimentalLayoutApi::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun PracticeSetupScreen(
    title: String,
    categories: List<PracticeCategoryItem>,
    onStartPractice: (strictMode: Boolean, categoryId: Int?, reversed: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var strictMode by remember { mutableStateOf(true) }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var reversed by remember { mutableStateOf(false) }

    val selectedCategory = categories.getOrNull(selectedCategoryIndex)
    val isAllSelected = selectedCategory?.id == null

    val scrollState = rememberScrollState()

    SentryTraced("practice_setup_screen") {
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
                    text = stringResource(R.string.learning_setup_label).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = stringResource(R.string.learning_setup_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.learning_setup_subtitle),
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
                        SessionTypeButton(
                            icon = Icons.Default.Bolt,
                            title = stringResource(R.string.learning_session_strict),
                            subtitle = stringResource(R.string.learning_session_strict_desc),
                            selected = strictMode,
                            onClick = { strictMode = true },
                            modifier = Modifier
                                .weight(1f)
                                .sentryTag("button_strict_mode")
                        )
                        SessionTypeButton(
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
                            FocusAreaChip(
                                label = chipLabel,
                                selected = isSelected,
                                onClick = {
                                    selectedCategoryIndex = index
                                    reversed = false
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
                                    DirectionChip(
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
                                    DirectionChip(
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

            Spacer(modifier = Modifier.height(24.dp))

            // CTA Button
            Button(
                onClick = { onStartPractice(strictMode, selectedCategory?.id, reversed) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .sentryTag("button_start_practice")
            ) {
                Text(
                    text = stringResource(R.string.learning_begin_session),
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
private fun SessionTypeButton(
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
private fun FocusAreaChip(
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
private fun DirectionChip(
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

@Preview(showBackground = true, name = "Practice Setup - English")
@Composable
fun PracticeSetupScreenPreview() {
    FiszkiTheme {
        PracticeSetupScreen(
            title = "Time to\npractice!",
            categories = listOf(
                PracticeCategoryItem(
                    id = null,
                    displayName = "All categories",
                    langFrom = null,
                    langOn = null
                ),
                PracticeCategoryItem(
                    id = 1,
                    displayName = "Spanish Basics",
                    langFrom = "English",
                    langOn = "Spanish"
                ),
                PracticeCategoryItem(
                    id = 2,
                    displayName = "French Vocabulary",
                    langFrom = "English",
                    langOn = "French"
                ),
                PracticeCategoryItem(
                    id = 3,
                    displayName = "German Grammar",
                    langFrom = "English",
                    langOn = "German"
                )
            ),
            onStartPractice = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Practice Setup - Polish")
@Composable
fun PracticeSetupScreenPreviewPolish() {
    FiszkiTheme {
        PracticeSetupScreen(
            title = "Czas na\nćwiczenia!",
            categories = listOf(
                PracticeCategoryItem(
                    id = null,
                    displayName = "Wszystkie kategorie",
                    langFrom = null,
                    langOn = null
                ),
                PracticeCategoryItem(
                    id = 1,
                    displayName = "Podstawy hiszpańskiego",
                    langFrom = "Polski",
                    langOn = "Hiszpański"
                ),
                PracticeCategoryItem(
                    id = 2,
                    displayName = "Słownictwo francuskie",
                    langFrom = "Polski",
                    langOn = "Francuski"
                )
            ),
            onStartPractice = { _, _, _ -> }
        )
    }
}
