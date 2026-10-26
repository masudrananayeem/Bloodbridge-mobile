package com.bloodbridge.app.ui

import com.bloodbridge.app.i18n.t
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloodbridge.app.data.DONATION_COOLDOWN_DAYS
import com.bloodbridge.app.data.daysUntilEligible
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

private const val BODY_TOP = 6f
private const val BODY_BOTTOM = 254f
private const val MIN_LEVEL = 18f

private fun bodyPath(): Path = Path().apply {
    addOval(Rect(Offset(60f, 25f), 17f))
    addRoundRect(RoundRect(53f, 40f, 67f, 54f, CornerRadius(5f)))
    addRoundRect(RoundRect(31f, 50f, 89f, 142f, CornerRadius(14f)))
    addRoundRect(RoundRect(9f, 55f, 26f, 141f, CornerRadius(8.5f)))
    addRoundRect(RoundRect(94f, 55f, 111f, 141f, CornerRadius(8.5f)))
    addRoundRect(RoundRect(35f, 134f, 58f, 254f, CornerRadius(11f)))
    addRoundRect(RoundRect(62f, 134f, 85f, 254f, CornerRadius(11f)))
}

private fun wavePath(top: Float, amp: Float, phase: Float): Path = Path().apply {
    moveTo(-10f, top)
    var x = -10f
    while (x <= 130f) {
        lineTo(x, top + amp * sin((x / 60f) * 2f * PI.toFloat() + phase))
        x += 2f
    }
    lineTo(130f, 270f)
    lineTo(-10f, 270f)
    close()
}

/** Human body that drains when you donate and refills over 120 days, with a waving blood surface. */
@Composable
fun BloodLevelBody(lastDonationDate: String?, modifier: Modifier = Modifier) {
    val daysLeft = daysUntilEligible(lastDonationDate)
    val recovering = daysLeft > 0
    val elapsed = if (recovering) DONATION_COOLDOWN_DAYS - daysLeft else DONATION_COOLDOWN_DAYS
    val pct = if (recovering) MIN_LEVEL + (100f - MIN_LEVEL) * elapsed / DONATION_COOLDOWN_DAYS else 100f

    val level by animateFloatAsState(targetValue = pct, animationSpec = tween(1600), label = "level")
    val transition = rememberInfiniteTransition(label = "wave")
    val phase1 by transition.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(2300, easing = LinearEasing), RepeatMode.Restart), label = "p1",
    )
    val phase2 by transition.animateFloat(
        initialValue = 2f * PI.toFloat(), targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(3400, easing = LinearEasing), RepeatMode.Restart), label = "p2",
    )

    val track = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
    val outline = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.30f)
    val body = remember { bodyPath() }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Canvas(modifier = Modifier.height(190.dp).width(88.dp)) {
            val scale = size.height / 260f
            val dx = (size.width - 120f * scale) / 2f
            withTransform({
                translate(left = dx, top = 0f)
                scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
            }) {
                drawPath(body, color = track)
                val top = BODY_BOTTOM - (BODY_BOTTOM - BODY_TOP) * level / 100f
                clipPath(body) {
                    drawPath(wavePath(top, 3.5f, phase2), color = Color(0x66EF4444))
                    drawPath(
                        wavePath(top, 2.6f, phase1),
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFFF87171), Color(0xFFDC2626), Color(0xFF7F1D1D)),
                            startY = top, endY = BODY_BOTTOM,
                        ),
                    )
                }
                drawPath(body, color = outline, style = Stroke(width = 1.5f))
            }
        }
        Text("${level.roundToInt()}%", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Text(
            if (recovering) "${t("Recovering")} · $daysLeft ${t("days")}" else t("Fully recovered"),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
