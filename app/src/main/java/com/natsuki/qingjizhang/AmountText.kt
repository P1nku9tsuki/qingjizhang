package com.natsuki.qingjizhang

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

val TextStyle.tabular: TextStyle
    get() = copy(fontFeatureSettings = "tnum")

@Composable
fun AnimatedAmount(
    value: Double,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    prefix: String = "¥ ",
    durationMillis: Int = 600,
    heroStyle: Boolean = false,
    decimalScale: Float = 0.55f
) {
    val targetCents = (value * 100).roundToInt()
    val animatedCents by animateIntAsState(
        targetValue = targetCents,
        animationSpec = tween(durationMillis, easing = MotionScheme.EasingStandard),
        label = "AnimatedAmountCents"
    )

    val fullText = formatAmount(animatedCents / 100.0)

    if (!heroStyle) {
        Text(
            text = "$prefix$fullText",
            style = style.tabular,
            color = color,
            modifier = modifier
        )
    } else {
        val parts = fullText.split(".")
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.Bottom    // 用 baseline 对齐后这行其实无所谓
        ) {
            Text(
                text = prefix,
                style = style.tabular,
                color = color,
                modifier = Modifier.alignByBaseline()
            )
            Text(
                text = parts[0],
                style = style.tabular,
                color = color,
                modifier = Modifier.alignByBaseline()
            )
            if (parts.size > 1) {
                val decimalStyle = style.copy(
                    fontSize = (style.fontSize.value * decimalScale).sp
                ).tabular
                Text(
                    text = ".${parts[1]}",
                    style = decimalStyle,
                    color = color.copy(alpha = 0.75f),
                    modifier = Modifier.alignByBaseline()   // 👈 关键
                )
            }
        }
    }
}

@Composable
fun StaticAmount(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = style.tabular,
        color = color,
        modifier = modifier
    )
}