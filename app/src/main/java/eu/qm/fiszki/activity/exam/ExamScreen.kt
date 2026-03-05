package eu.qm.fiszki.activity.exam

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.qm.fiszki.activity.learning.AssetFamily
import eu.qm.fiszki.activity.learning.buildTitleSpanStyle

data class ExamOptionItem(
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun ExamSetupScreen(
    title: String,
    options: List<ExamOptionItem>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val lines = title.split("\n")

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scrollState)
    ) {
        // Title section
        Column(
            modifier = Modifier.padding(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 24.dp)
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

        // Options section
        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            options.forEach { option ->
                Button(
                    onClick = option.onClick,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text(
                        text = option.label,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

