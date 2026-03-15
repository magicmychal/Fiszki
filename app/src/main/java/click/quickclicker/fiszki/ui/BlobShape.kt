package click.quickclicker.fiszki.ui

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * Organic blob shape matching the correct_blob_bg.xml vector drawable.
 * The original SVG path is defined in a 380x380 viewport and scaled
 * to fit whatever size the composable requests.
 */
val BlobShape: Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val sx = size.width / 380f
        val sy = size.height / 380f
        val path = Path().apply {
            moveTo(134.186f * sx, 54.5654f * sy)
            cubicTo(165.276f * sx, 24.4782f * sy, 214.724f * sx, 24.4782f * sy, 245.814f * sx, 54.5654f * sy)
            cubicTo(255.328f * sx, 63.7718f * sy, 266.984f * sx, 70.4811f * sy, 279.738f * sx, 74.0919f * sy)
            cubicTo(321.419f * sx, 85.8924f * sy, 346.142f * sx, 128.586f * sy, 335.552f * sx, 170.473f * sy)
            cubicTo(332.312f * sx, 183.291f * sy, 332.312f * sx, 196.709f * sy, 335.552f * sx, 209.527f * sy)
            cubicTo(346.142f * sx, 251.414f * sy, 321.419f * sx, 294.108f * sy, 279.738f * sx, 305.908f * sy)
            cubicTo(266.984f * sx, 309.519f * sy, 255.328f * sx, 316.228f * sy, 245.814f * sx, 325.435f * sy)
            cubicTo(214.724f * sx, 355.522f * sy, 165.276f * sx, 355.522f * sy, 134.186f * sx, 325.435f * sy)
            cubicTo(124.672f * sx, 316.228f * sy, 113.016f * sx, 309.519f * sy, 100.262f * sx, 305.908f * sy)
            cubicTo(58.5815f * sx, 294.108f * sy, 33.8578f * sx, 251.414f * sy, 44.4476f * sx, 209.527f * sy)
            cubicTo(47.6879f * sx, 196.709f * sy, 47.6879f * sx, 183.291f * sy, 44.4476f * sx, 170.473f * sy)
            cubicTo(33.8578f * sx, 128.586f * sy, 58.5815f * sx, 85.8924f * sy, 100.262f * sx, 74.0919f * sy)
            cubicTo(113.016f * sx, 70.4811f * sy, 124.672f * sx, 63.7718f * sy, 134.186f * sx, 54.5654f * sy)
            close()
        }
        return Outline.Generic(path)
    }
}
