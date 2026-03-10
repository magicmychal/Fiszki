package click.quickclicker.fiszki.activity.learning

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import click.quickclicker.fiszki.R

val RobotoFlexFamily = FontFamily(
    Font(R.font.roboto_flex_regular, weight = FontWeight.Black)
)

val RobotoMonoFamily = FontFamily(
    Font(R.font.roboto_mono_regular, weight = FontWeight.Thin)
)

val RobotoSerifFamily = FontFamily(
    Font(R.font.roboto_serif_regular, weight = FontWeight.Normal),
    Font(R.font.roboto_serif_italic, weight = FontWeight.Normal, style = FontStyle.Italic)
)

/** Asset — unique display font for the first title word ("TIME"/"CZAS") */
val AssetFamily = FontFamily(
    Font(R.font.asset_regular, weight = FontWeight.Normal)
)

data class WordStyle(
    val fontFamily: FontFamily,
    val fontWeight: FontWeight,
    val fontStyle: FontStyle = FontStyle.Normal,
    val letterSpacing: TextUnit = TextUnit.Unspecified
)

// Cycling list of styles — all with default letter spacing
val titleWordStyles = listOf(
    WordStyle(
        fontFamily = RobotoFlexFamily,
        fontWeight = FontWeight.Black,
        letterSpacing = TextUnit.Unspecified
    ),
    WordStyle(
        fontFamily = RobotoMonoFamily,
        fontWeight = FontWeight.Thin,
        letterSpacing = TextUnit.Unspecified
    ),
    WordStyle(
        fontFamily = RobotoSerifFamily,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        letterSpacing = TextUnit.Unspecified
    )
)

fun buildTitleSpanStyle(index: Int): SpanStyle {
    val style = titleWordStyles[index % titleWordStyles.size]
    return SpanStyle(
        fontFamily = style.fontFamily,
        fontWeight = style.fontWeight,
        fontStyle = style.fontStyle,
        letterSpacing = style.letterSpacing
    )
}
