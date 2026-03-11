package click.quickclicker.fiszki.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import click.quickclicker.fiszki.Checker

private val DiffRed = Color(0xFFD32F2F)
private val DiffGreen = Color(0xFF388E3C)

fun buildDiffAnnotatedString(
    text: String,
    diffs: BooleanArray?,
    isCorrect: Boolean
): AnnotatedString {
    val color = if (isCorrect) DiffGreen else DiffRed

    return buildAnnotatedString {
        if (diffs == null) {
            append(text)
            return@buildAnnotatedString
        }
        var i = 0
        while (i < text.length) {
            if (i < diffs.size && diffs[i]) {
                val start = i
                while (i < diffs.size && diffs[i]) i++
                withStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold)) {
                    append(text.substring(start, minOf(i, text.length)))
                }
            } else {
                append(text[i])
                i++
            }
        }
    }
}

fun buildUserAnswerAnnotated(userAnswer: String, correctAnswer: String): AnnotatedString {
    val (userDiffs, _) = Checker.alignDiffs(userAnswer, correctAnswer)
    return buildDiffAnnotatedString(
        text = userAnswer.ifEmpty { "\u2014" },
        diffs = if (userAnswer.isEmpty()) null else userDiffs,
        isCorrect = false
    )
}

fun buildCorrectAnswerAnnotated(correctAnswer: String, userAnswer: String): AnnotatedString {
    val (_, correctDiffs) = Checker.alignDiffs(userAnswer, correctAnswer)
    return buildDiffAnnotatedString(
        text = correctAnswer,
        diffs = if (userAnswer.isEmpty()) null else correctDiffs,
        isCorrect = true
    )
}
