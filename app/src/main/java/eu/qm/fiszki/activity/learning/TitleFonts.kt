package eu.qm.fiszki.activity.learning

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import eu.qm.fiszki.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// "Time" — Roboto Flex, Black, tracking -0.25px
val RobotoFlexFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Roboto Flex"),
        fontProvider = provider,
        weight = FontWeight.Black
    )
)

// "to" — Roboto Mono, Thin, tracking -11px
val RobotoMonoFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Roboto Mono"),
        fontProvider = provider,
        weight = FontWeight.Thin
    )
)

// "practice" — Roboto Serif, Italic, normal weight, tracking 2px
val RobotoSerifFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Roboto Serif"),
        fontProvider = provider,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    )
)

data class WordStyle(
    val fontFamily: FontFamily,
    val fontWeight: FontWeight,
    val fontStyle: FontStyle = FontStyle.Normal,
    val letterSpacing: androidx.compose.ui.unit.TextUnit = 0.sp
)

// Cycling list of styles matching the Figma specs
val titleWordStyles = listOf(
    WordStyle(
        fontFamily = RobotoFlexFamily,
        fontWeight = FontWeight.Black,
        letterSpacing = (-0.25).sp
    ),
    WordStyle(
        fontFamily = RobotoMonoFamily,
        fontWeight = FontWeight.Thin,
        letterSpacing = (-11).sp
    ),
    WordStyle(
        fontFamily = RobotoSerifFamily,
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Italic,
        letterSpacing = 2.sp
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
