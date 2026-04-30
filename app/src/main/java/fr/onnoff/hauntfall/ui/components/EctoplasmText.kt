package fr.onnoff.hauntfall.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/**
 * Texte "ectoplasme" : chaque lettre ondule verticalement avec un déphasage,
 * la luminosité pulse en vague de gauche à droite, et des gouttes tombent
 * sous le texte de façon irrégulière.
 */
@Composable
fun EctoplasmText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    baseColor: Color = Color(0xFF6BD9A8),
    highlightColor: Color = Color(0xFFB8F0D6),
    waveAmplitudeDp: Float = 2.5f,
    waveSpeedMs: Int = 2400
) {
    val infinite = rememberInfiniteTransition(label = "ectoplasm")
    val phase by infinite.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = waveSpeedMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )
    val dripCycle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drip"
    )

    // Seeds stables par lettre pour gouttes pseudo-aléatoires reproductibles
    val dripSeeds = remember(text) {
        text.mapIndexed { i, _ -> Random(i * 31L + 17L).nextFloat() }
    }

    Box(modifier = modifier) {
        Row {
            text.forEachIndexed { i, ch ->
                val perChar = i * 0.45f
                val w = sin((phase + perChar).toDouble()).toFloat()
                val offsetY = (w * waveAmplitudeDp).dp
                val brightness: Float = 0.55f + ((w + 1f) / 2f) * 0.45f
                val color = lerpColor(baseColor, highlightColor, brightness)
                Text(
                    text = ch.toString(),
                    fontSize = fontSize,
                    style = TextStyle(
                        color = color,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.offset(y = offsetY)
                )
            }
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawDrips(text.length, dripSeeds, dripCycle, baseColor, highlightColor, fontSize.value)
        }
    }
}

private fun DrawScope.drawDrips(
    charCount: Int,
    seeds: List<Float>,
    cycle: Float,
    baseColor: Color,
    highlightColor: Color,
    fontSizeSp: Float
) {
    if (charCount == 0) return
    val w = size.width
    val h = size.height
    val charWidth = w / charCount
    val baselineY = h * 0.55f
    val maxFallPx = (fontSizeSp * 1.6f) * density
    val dropRadiusPx = (fontSizeSp * 0.18f) * density

    seeds.forEachIndexed { i, seed ->
        // Chaque lettre a sa propre fenêtre de chute (décalée par seed)
        val localCycle = ((cycle + seed) % 1f)
        // Une lettre sur ~3 émet une goutte par cycle
        if (seed < 0.35f) {
            val progress = localCycle
            val centerX = charWidth * (i + 0.5f)
            val y = baselineY + maxFallPx * progress
            val alpha = (1f - progress).coerceIn(0f, 1f)
            val growing = (progress * 2f).coerceAtMost(1f)
            val r = dropRadiusPx * (0.4f + growing * 0.6f)

            // Goutte allongée verticalement (ovale)
            drawOval(
                color = highlightColor.copy(alpha = alpha * 0.85f),
                topLeft = Offset(centerX - r * 0.6f, y - r),
                size = Size(r * 1.2f, r * 1.8f)
            )
            // Petit halo
            drawCircle(
                color = baseColor.copy(alpha = alpha * 0.25f),
                radius = r * 1.6f,
                center = Offset(centerX, y)
            )
        }
    }
}

private fun lerpColor(a: Color, b: Color, t: Float): Color {
    val tt = t.coerceIn(0f, 1f)
    return Color(
        red = a.red + (b.red - a.red) * tt,
        green = a.green + (b.green - a.green) * tt,
        blue = a.blue + (b.blue - a.blue) * tt,
        alpha = a.alpha + (b.alpha - a.alpha) * tt
    )
}
