package dev.chris.motoevaluation.presentation.auction.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun LifecycleAwareTimer(
    durationMs: Long,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Current System Time + Duration = Target Time
    // rememberSaveable ensures this survives rotation
    val targetTime = rememberSaveable { System.currentTimeMillis() + durationMs }
    var timeLeftRatio by remember { mutableFloatStateOf(1f) }
    var timeLeftSeconds by remember { mutableIntStateOf((durationMs / 1000).toInt()) }

    LaunchedEffect(Unit) {
        while (isActive) {
            val now = System.currentTimeMillis()
            val remaining = targetTime - now

            if (remaining <= 0) {
                timeLeftRatio = 0f
                timeLeftSeconds = 0
                onFinished()
                break
            }

            timeLeftRatio = remaining.toFloat() / durationMs
            timeLeftSeconds = (remaining / 1000).toInt() + 1
            delay(16) // ~60 FPS
        }
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(50.dp)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            // Track
            drawArc(
                color = Color.LightGray.copy(alpha = 0.3f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx())
            )
            // Progress
            drawArc(
                color = if (timeLeftRatio < 0.2f) Color.Red else Color(0xFF6200EE),
                startAngle = -90f,
                sweepAngle = 360f * timeLeftRatio,
                useCenter = false,
                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = "$timeLeftSeconds",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}