package eu.qm.fiszki.activity.learning

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.TextUnit
import eu.qm.fiszki.R

private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val RobotoFlexFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Roboto Flex"),
        fontProvider = provider,
        weight = FontWeight.Black
    )
)

val RobotoMonoFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Roboto Mono"),
        fontProvider = provider,
        weight = FontWeight.Thin
    )
)

val RobotoSerifFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Roboto Serif"),
        fontProvider = provider,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    )
)

/** Asset — unique display font for "Time" title, fetched from Google Fonts */
val AssetFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Asset"),
        fontProvider = provider,
        weight = FontWeight.Normal
    )
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
