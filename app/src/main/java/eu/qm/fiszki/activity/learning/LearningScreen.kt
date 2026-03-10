package eu.qm.fiszki.activity.learning

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.FiszkiTheme
import io.sentry.compose.SentryTraced
import io.sentry.compose.SentryModifier.sentryTag

data class PracticeCategoryItem(
    val id: Int?,
    val displayName: String,
    val langFrom: String?,
    val langOn: String?
)

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun PracticeSetupScreen(
    title: String,
    categories: List<PracticeCategoryItem>,
    onStartPractice: (strictMode: Boolean, categoryId: Int?, reversed: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var strictMode by remember { mutableStateOf(true) }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var langExpanded by remember { mutableStateOf(false) }
    var reversed by remember { mutableStateOf(false) }

    val selectedCategory = categories.getOrNull(selectedCategoryIndex)
    val isAllSelected = selectedCategory?.id == null

    val scrollState = rememberScrollState()
    val lines = title.split("\n")

    SentryTraced("practice_setup_screen") {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scrollState)
    ) {
        // Title section
        Column(
            modifier = Modifier.padding(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 8.dp)
        ) {
            val allWords = lines.flatMap { it.split(" ").filter { w -> w.isNotEmpty() } }
            val firstWord = allWords.firstOrNull() ?: ""
            val textMeasurer = rememberTextMeasurer()

            // First word — auto-sized to fill width
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val maxWidthPx = constraints.maxWidth.toFloat()
                val baseFontSize = 57.sp
                val baseLetterSpacing = 28.sp

                val scaledFontSize = remember(firstWord, maxWidthPx) {
                    var fontSize = baseFontSize
                    var letterSpacing = baseLetterSpacing
                    val style = TextStyle(
                        fontFamily = AssetFamily,
                        fontWeight = FontWeight.Normal
                    )
                    // Scale down until text fits
                    while (fontSize.value > 20f) {
                        val result = textMeasurer.measure(
                            text = firstWord.uppercase(),
                            style = style.copy(
                                fontSize = fontSize,
                                letterSpacing = letterSpacing
                            )
                        )
                        if (result.size.width <= maxWidthPx) break
                        val scale = 0.95f
                        fontSize = (fontSize.value * scale).sp
                        letterSpacing = (letterSpacing.value * scale).sp
                    }
                    fontSize to letterSpacing
                }

                Text(
                    text = firstWord.uppercase(),
                    fontSize = scaledFontSize.first,
                    letterSpacing = scaledFontSize.second,
                    fontFamily = AssetFamily,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = (scaledFontSize.first.value * 1.12f).sp,
                    maxLines = 1,
                    softWrap = false
                )
            }

            // Remaining lines — skip the first word
            var wordIndex = 1
            lines.forEachIndexed { lineIndex, line ->
                val words = line.split(" ").filter { it.isNotEmpty() }
                val wordsToRender = if (lineIndex == 0) words.drop(1) else words
                if (wordsToRender.isNotEmpty()) {
                    Text(
                        text = buildAnnotatedString {
                            wordsToRender.forEachIndexed { i, word ->
                                withStyle(buildTitleSpanStyle(wordIndex)) { append(word) }
                                wordIndex++
                                if (i < wordsToRender.size - 1) append(" ")
                            }
                        },
                        fontSize = 57.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 64.sp
                    )
                }
            }
        }

        // Settings section
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Strict mode — checkbox on the left
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Checkbox(
                    checked = strictMode,
                    onCheckedChange = { strictMode = it },
                    modifier = Modifier.sentryTag("checkbox_strict_mode")
                )
                Column(modifier = Modifier.padding(start = 8.dp, top = 4.dp)) {
                    Text(
                        text = stringResource(R.string.learning_strict_mode),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.learning_strict_mode_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Category dropdown
            Text(
                text = stringResource(R.string.learning_category_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                OutlinedTextField(
                    value = selectedCategory?.displayName ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    categories.forEachIndexed { index, cat ->
                        DropdownMenuItem(
                            text = { Text(cat.displayName) },
                            onClick = {
                                selectedCategoryIndex = index
                                categoryExpanded = false
                                reversed = false
                            }
                        )
                    }
                }
            }

            // Language pair (only shown when a specific category is selected)
            val langFrom = selectedCategory?.langFrom
            val langOn = selectedCategory?.langOn
            if (!isAllSelected && !langFrom.isNullOrEmpty() && !langOn.isNullOrEmpty()) {
                val normalDir = stringResource(R.string.learning_direction_format, langFrom, langOn)
                val reversedDir = stringResource(R.string.learning_direction_format, langOn, langFrom)
                val currentDir = if (reversed) reversedDir else normalDir

                Text(
                    text = stringResource(R.string.learning_language_pair_label),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.learning_language_pair_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                )
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = langExpanded,
                        onExpandedChange = { langExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = currentDir,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = langExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(expanded = langExpanded, onDismissRequest = { langExpanded = false }) {
                            DropdownMenuItem(text = { Text(normalDir) }, onClick = { reversed = false; langExpanded = false })
                            DropdownMenuItem(text = { Text(reversedDir) }, onClick = { reversed = true; langExpanded = false })
                        }
                    }
                }
            }

            // Start practice button
            Button(
                onClick = { onStartPractice(strictMode, selectedCategory?.id, reversed) },
                modifier = Modifier.fillMaxWidth().sentryTag("button_start_practice")
            ) {
                Text(
                    stringResource(R.string.learning_start_practice),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
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

