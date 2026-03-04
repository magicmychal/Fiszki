package eu.qm.fiszki.activity.learning

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

data class ShapeItem(
    val label: String,
    val color: Color,
    val shapeType: ShapeType,
    val onClick: () -> Unit
)

enum class ShapeType { BLOB, ARROW, FLOWER, HEART }

@Composable
fun LearningScreen(
    title: String,
    shapes: List<ShapeItem>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = title,
            fontSize = 57.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            lineHeight = 52.sp,
            modifier = Modifier.padding(start = 24.dp, top = 48.dp, end = 24.dp, bottom = 24.dp)
        )
        shapes.forEach { shape ->
            ShapeButton(
                item = shape,
                modifier = Modifier.fillMaxWidth().height(380.dp).padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ShapeButton(item: ShapeItem, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "morph")
    val morphProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "morph"
    )

    Box(modifier = modifier.clickable { item.onClick() }, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val s = Size(size.width * 0.85f, size.height * 0.85f)
            val o = Offset((size.width - s.width) / 2, (size.height - s.height) / 2)
            when (item.shapeType) {
                ShapeType.BLOB -> drawMorphBlob(item.color, s, o, morphProgress)
                ShapeType.ARROW -> drawMorphArrow(item.color, s, o, morphProgress)
                ShapeType.FLOWER -> drawMorphFlower(item.color, s, o, morphProgress)
                ShapeType.HEART -> drawMorphHeart(item.color, s, o, morphProgress)
            }
        }
        Text(
            text = item.label,
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 200.dp)
        )
    }
}

private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t

// Blob morphs to Pill (rounded rectangle)
private fun DrawScope.drawMorphBlob(color: Color, s: Size, o: Offset, t: Float) {
    val cx = o.x + s.width / 2; val cy = o.y + s.height / 2
    val rx = s.width / 2; val ry = s.height / 2
    // Blob control points morph toward pill (wider, flatter, more rectangular)
    val topFlatness = lerp(0.5f, 0.85f, t)
    val sideFlatness = lerp(0.55f, 0.15f, t)
    val cornerSpread = lerp(0.9f, 0.98f, t)
    val path = Path().apply {
        moveTo(cx + rx * cornerSpread, cy)
        cubicTo(cx + rx, cy - ry * sideFlatness, cx + rx * topFlatness, cy - ry, cx, cy - ry * lerp(0.85f, 0.95f, t))
        cubicTo(cx - rx * topFlatness, cy - ry, cx - rx, cy - ry * sideFlatness, cx - rx * cornerSpread, cy)
        cubicTo(cx - rx, cy + ry * sideFlatness, cx - rx * topFlatness, cy + ry, cx, cy + ry * lerp(0.9f, 0.95f, t))
        cubicTo(cx + rx * topFlatness, cy + ry, cx + rx, cy + ry * sideFlatness, cx + rx * cornerSpread, cy)
        close()
    }
    drawPath(path, color)
}

// Arrow morphs to Diamond
private fun DrawScope.drawMorphArrow(color: Color, s: Size, o: Offset, t: Float) {
    val cx = o.x + s.width / 2; val cy = o.y + s.height / 2
    val hw = s.width / 2; val hh = s.height / 2
    // Arrow: pointy right, flat left -> Diamond: pointy all 4 sides
    val rightX = o.x + lerp(s.width * 0.95f, s.width, t)
    val leftX = o.x + lerp(s.width * 0.15f, 0f, t)
    val bulge = lerp(0.25f, 0f, t) // arrow has a bulge on sides
    val path = Path().apply {
        moveTo(cx, o.y) // top
        cubicTo(cx + hw * lerp(0.3f, 0.1f, t), o.y + hh * bulge, rightX - hw * bulge, cy - hh * lerp(0.3f, 0.1f, t), rightX, cy)
        cubicTo(rightX - hw * bulge, cy + hh * lerp(0.3f, 0.1f, t), cx + hw * lerp(0.3f, 0.1f, t), o.y + s.height - hh * bulge, cx, o.y + s.height)
        cubicTo(cx - hw * lerp(0.3f, 0.1f, t), o.y + s.height - hh * bulge, leftX + hw * bulge, cy + hh * lerp(0.3f, 0.1f, t), leftX, cy)
        cubicTo(leftX + hw * bulge, cy - hh * lerp(0.3f, 0.1f, t), cx - hw * lerp(0.3f, 0.1f, t), o.y + hh * bulge, cx, o.y)
        close()
    }
    drawPath(path, color)
}

// Flower (6-sided cookie) morphs to 4-leaf clover
private fun DrawScope.drawMorphFlower(color: Color, s: Size, o: Offset, t: Float) {
    val cx = o.x + s.width / 2; val cy = o.y + s.height / 2
    val r = min(s.width, s.height) / 2
    val sidesA = 6; val sidesB = 4
    val sides = if (t < 0.5f) sidesA else sidesB
    val outerR = r * 0.95f
    val innerR = r * lerp(0.78f, 0.6f, t) // cookie indent gets deeper toward clover
    val points = sides * 2
    val path = Path().apply {
        for (i in 0 until points) {
            val isOuter = i % 2 == 0
            val rad = if (isOuter) outerR else innerR
            val angle = Math.toRadians((i * 360.0 / points) - 90.0)
            val x = cx + (rad * cos(angle)).toFloat()
            val y = cy + (rad * sin(angle)).toFloat()
            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
    drawPath(path, color)
}

// Heart morphs to Arch
private fun DrawScope.drawMorphHeart(color: Color, s: Size, o: Offset, t: Float) {
    val w = s.width; val h = s.height; val cx = o.x + w / 2
    val topDip = lerp(0.30f, 0.0f, t) // heart dip flattens to arch top
    val bottomPoint = lerp(0.9f, 1.0f, t) // bottom point flattens
    val bWidth = lerp(0.0f, 0.0f, t)
    val path = Path().apply {
        moveTo(cx, o.y + h * bottomPoint)
        // Left side
        cubicTo(
            o.x + w * bWidth, o.y + h * lerp(0.65f, 0.7f, t),
            o.x + w * bWidth, o.y + h * lerp(0.25f, 0.0f, t),
            cx - w * lerp(0.12f, 0.0f, t), o.y + h * lerp(0.25f, 0.0f, t)
        )
        cubicTo(
            o.x + w * lerp(0.15f, 0.1f, t), o.y + h * lerp(0.08f, 0.0f, t),
            cx - w * lerp(0.05f, 0.01f, t), o.y + h * lerp(0.12f, 0.0f, t),
            cx, o.y + h * topDip
        )
        // Right side
        cubicTo(
            cx + w * lerp(0.05f, 0.01f, t), o.y + h * lerp(0.12f, 0.0f, t),
            o.x + w * lerp(0.85f, 0.9f, t), o.y + h * lerp(0.08f, 0.0f, t),
            cx + w * lerp(0.12f, 0.0f, t), o.y + h * lerp(0.25f, 0.0f, t)
        )
        cubicTo(
            o.x + w * (1f - bWidth), o.y + h * lerp(0.25f, 0.0f, t),
            o.x + w * (1f - bWidth), o.y + h * lerp(0.65f, 0.7f, t),
            cx, o.y + h * bottomPoint
        )
        close()
    }
    drawPath(path, color)
}
