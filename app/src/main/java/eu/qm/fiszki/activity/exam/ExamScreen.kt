package eu.qm.fiszki.activity.exam

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.qm.fiszki.R
import eu.qm.fiszki.activity.learning.AssetFamily
import eu.qm.fiszki.activity.learning.PracticeCategoryItem
import eu.qm.fiszki.activity.learning.buildTitleSpanStyle

data class RoundsOption(val value: Int, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamSetupScreen(
    title: String,
    categories: List<PracticeCategoryItem>,
    roundsOptions: List<RoundsOption>,
    onStartExam: (strictMode: Boolean, categoryId: Int?, reversed: Boolean, rounds: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var strictMode by remember { mutableStateOf(true) }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var langExpanded by remember { mutableStateOf(false) }
    var reversed by remember { mutableStateOf(false) }
    var selectedRoundsIndex by remember { mutableIntStateOf(0) }
    var roundsExpanded by remember { mutableStateOf(false) }

    val selectedCategory = categories.getOrNull(selectedCategoryIndex)
    val isAllSelected = selectedCategory?.id == null
    val selectedRounds = roundsOptions.getOrNull(selectedRoundsIndex)

    val scrollState = rememberScrollState()
    val lines = title.split("\n")

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scrollState)
    ) {
        // Title section
        Column(
            modifier = Modifier.padding(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 8.dp)
        ) {
            var wordIndex = 0
            lines.forEachIndexed { lineIndex, line ->
                val words = line.split(" ").filter { it.isNotEmpty() }
                if (lineIndex == 0 && words.size == 1) {
                    Text(
                        text = words[0].uppercase(),
                        fontSize = 57.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 64.sp,
                        fontFamily = AssetFamily,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 28.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    wordIndex += 1
                } else {
                    Text(
                        text = buildAnnotatedString {
                            words.forEachIndexed { i, word ->
                                withStyle(buildTitleSpanStyle(wordIndex)) { append(word) }
                                wordIndex++
                                if (i < words.size - 1) append(" ")
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
                    onCheckedChange = { strictMode = it }
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

            // Total number of rounds dropdown
            Text(
                text = stringResource(R.string.exam_rounds_label),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ExposedDropdownMenuBox(
                expanded = roundsExpanded,
                onExpandedChange = { roundsExpanded = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                OutlinedTextField(
                    value = selectedRounds?.label ?: "",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roundsExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                )
                ExposedDropdownMenu(expanded = roundsExpanded, onDismissRequest = { roundsExpanded = false }) {
                    roundsOptions.forEachIndexed { index, option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                selectedRoundsIndex = index
                                roundsExpanded = false
                            }
                        )
                    }
                }
            }

            // Go! button
            Button(
                onClick = {
                    selectedRounds?.let { rounds ->
                        onStartExam(strictMode, selectedCategory?.id, reversed, rounds.value)
                    }
                },
                enabled = selectedRounds != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.exam_start),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
