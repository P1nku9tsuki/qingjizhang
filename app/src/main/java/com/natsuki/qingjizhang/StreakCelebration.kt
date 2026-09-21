package com.natsuki.qingjizhang

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.PI
import kotlin.math.sin


@Composable
fun StreakCelebrationOverlay(
    milestone: Int,
    onDismiss: () -> Unit
) {
    val primary = MiuixTheme.colorScheme.primary
    LaunchedEffect(milestone) {
        delay(3500)
        onDismiss()
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(milestone) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(3200, easing = LinearEasing))
    }

    var appeared by remember { mutableStateOf(false) }
    LaunchedEffect(milestone) {
        appeared = false
        delay(60)
        appeared = true
    }

    val bgAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(350),
        label = "bgAlpha"
    )
    val contentAlpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(500, delayMillis = 150),
        label = "contentAlpha"
    )
    val numberScale by animateFloatAsState(
        targetValue = if (appeared) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "numScale"
    )

    val confettiList = remember(milestone) {
        val random = kotlin.random.Random(milestone * 31 + 7)
        List(60) {
            ConfettiPiece(
                x = random.nextFloat(),
                delay = random.nextFloat() * 0.55f,
                speed = 0.75f + random.nextFloat() * 0.55f,
                size = 6f + random.nextFloat() * 8f,
                color = CONFETTI_COLORS[random.nextInt(CONFETTI_COLORS.size)],
                swayAmplitude = 20f + random.nextFloat() * 45f,
                swayFrequency = 4f + random.nextFloat() * 4f,
                rotationSpeed = -180f + random.nextFloat() * 360f
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.72f * bgAlpha))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // ── 撒花 ──
        Canvas(modifier = Modifier.fillMaxSize()) {
            confettiList.forEach { c ->
                val t = ((progress.value - c.delay) / (1f - c.delay)).coerceIn(0f, 1f)
                if (t <= 0f) return@forEach
                val y = -50f + t * (size.height + 120f) * c.speed
                val x = c.x * size.width +
                        sin(t * c.swayFrequency * PI.toFloat()) * c.swayAmplitude
                val rotation = t * c.rotationSpeed
                val alpha = (1f - t * 0.7f).coerceAtLeast(0f)

                rotate(degrees = rotation, pivot = Offset(x, y)) {
                    drawRect(
                        color = c.color.copy(alpha = alpha),
                        topLeft = Offset(x, y),
                        size = Size(c.size, c.size * 1.6f)
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(contentAlpha)
        ) {
            Text("🔥", fontSize = 64.sp)
            Spacer(Modifier.height(20.dp))

            Text(
                text = "连续记账",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // 3 圈错开的光环
                repeat(3) { index ->
                    val phase = index * 0.18f
                    val t = ((progress.value - phase) / (1f - phase)).coerceIn(0f, 1f)
                    val scale = 0.5f + t * 1.6f
                    val alpha = (1f - t) * 0.45f
                    if (alpha > 0.02f) {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .scale(scale)
                                .background(primary.copy(alpha = alpha), CircleShape)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$milestone",
                        fontSize = 96.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.scale(numberScale)
                    )
                    Text(
                        text = " 天",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = milestoneMessage(milestone),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.72f)
            )

            Spacer(Modifier.height(44.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onDismiss() }
                    .padding(horizontal = 36.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "太棒了",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
            }
        }
    }
}

private data class ConfettiPiece(
    val x: Float,
    val delay: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val swayAmplitude: Float,
    val swayFrequency: Float,
    val rotationSpeed: Float
)

private val CONFETTI_COLORS = listOf(
    Color(0xFFE57373),
    Color(0xFFFFB74D),
    Color(0xFFFFD54F),
    Color(0xFF81C784),
    Color(0xFF64B5F6),
    Color(0xFF9575CD),
    Color(0xFFF06292)
)

private fun milestoneMessage(milestone: Int): String = when (milestone) {
    7 -> "一周啦，习惯正在养成"
    14 -> "两周啦，已经超过大部分人了"
    30 -> "一个月啦，厉害！"
    60 -> "两个月啦，自律的人最棒"
    100 -> "100 天！这是个了不起的成就"
    180 -> "半年啦，你太牛了"
    365 -> "一年啦！你是记账王者 👑"
    else -> "坚持就是胜利"
}
