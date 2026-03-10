package click.quickclicker.fiszki.activity

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private fun Context.resolveColor(@AttrRes attr: Int, fallback: Long): Color {
    val tv = TypedValue()
    return if (theme.resolveAttribute(attr, tv, true)) {
        Color(tv.data.toLong() or 0xFF000000L) // ensure full alpha
    } else {
        Color(fallback)
    }
}

@Composable
fun FiszkiTheme(content: @Composable () -> Unit) {
    val ctx = LocalContext.current

    // Detect dark mode from the theme's background luminance
    val surface = ctx.resolveColor(com.google.android.material.R.attr.colorSurface, 0xFFFFFBFE)
    val isDark = (surface.red * 0.299f + surface.green * 0.587f + surface.blue * 0.114f) < 0.5f

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = ctx.resolveColor(android.R.attr.colorPrimary, 0xFFD0BCFF),
            onPrimary = ctx.resolveColor(com.google.android.material.R.attr.colorOnPrimary, 0xFF381E72),
            primaryContainer = ctx.resolveColor(com.google.android.material.R.attr.colorPrimaryContainer, 0xFF4F378B),
            onPrimaryContainer = ctx.resolveColor(com.google.android.material.R.attr.colorOnPrimaryContainer, 0xFFEADDFF),
            secondary = ctx.resolveColor(com.google.android.material.R.attr.colorSecondary, 0xFFCCC2DC),
            onSecondary = ctx.resolveColor(com.google.android.material.R.attr.colorOnSecondary, 0xFF332D41),
            secondaryContainer = ctx.resolveColor(com.google.android.material.R.attr.colorSecondaryContainer, 0xFF4A4458),
            onSecondaryContainer = ctx.resolveColor(com.google.android.material.R.attr.colorOnSecondaryContainer, 0xFFE8DEF8),
            tertiary = ctx.resolveColor(com.google.android.material.R.attr.colorTertiary, 0xFFEFB8C8),
            surface = surface,
            onSurface = ctx.resolveColor(com.google.android.material.R.attr.colorOnSurface, 0xFFE6E1E5),
            surfaceVariant = ctx.resolveColor(com.google.android.material.R.attr.colorSurfaceVariant, 0xFF49454F),
            onSurfaceVariant = ctx.resolveColor(com.google.android.material.R.attr.colorOnSurfaceVariant, 0xFFCAC4D0),
            outline = ctx.resolveColor(com.google.android.material.R.attr.colorOutline, 0xFF938F99),
        )
    } else {
        lightColorScheme(
            primary = ctx.resolveColor(android.R.attr.colorPrimary, 0xFF6750A4),
            onPrimary = ctx.resolveColor(com.google.android.material.R.attr.colorOnPrimary, 0xFFFFFFFF),
            primaryContainer = ctx.resolveColor(com.google.android.material.R.attr.colorPrimaryContainer, 0xFFEADDFF),
            onPrimaryContainer = ctx.resolveColor(com.google.android.material.R.attr.colorOnPrimaryContainer, 0xFF21005D),
            secondary = ctx.resolveColor(com.google.android.material.R.attr.colorSecondary, 0xFF625B71),
            onSecondary = ctx.resolveColor(com.google.android.material.R.attr.colorOnSecondary, 0xFFFFFFFF),
            secondaryContainer = ctx.resolveColor(com.google.android.material.R.attr.colorSecondaryContainer, 0xFFE8DEF8),
            onSecondaryContainer = ctx.resolveColor(com.google.android.material.R.attr.colorOnSecondaryContainer, 0xFF1D192B),
            tertiary = ctx.resolveColor(com.google.android.material.R.attr.colorTertiary, 0xFF7D5260),
            surface = surface,
            onSurface = ctx.resolveColor(com.google.android.material.R.attr.colorOnSurface, 0xFF1C1B1F),
            surfaceVariant = ctx.resolveColor(com.google.android.material.R.attr.colorSurfaceVariant, 0xFFE7E0EC),
            onSurfaceVariant = ctx.resolveColor(com.google.android.material.R.attr.colorOnSurfaceVariant, 0xFF49454F),
            outline = ctx.resolveColor(com.google.android.material.R.attr.colorOutline, 0xFF79747E),
        )
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
