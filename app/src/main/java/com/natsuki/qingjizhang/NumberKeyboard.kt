package com.natsuki.qingjizhang

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun NumberKeyboard(
    onNumber: (String) -> Unit,
    onDecimal: () -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    doneEnabled: Boolean = true,
    doneLabel: String = "完成"
) {
    val context = LocalContext.current
    val surface = MiuixTheme.colorScheme.surface
    val onSurface = MiuixTheme.colorScheme.onSurface
    val primary = MiuixTheme.colorScheme.primary
    val onPrimary = MiuixTheme.colorScheme.onPrimary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(272.dp)
            .background(surface)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Column(
            modifier = Modifier
                .weight(3f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberKey("1", Modifier.weight(1f).fillMaxHeight()) {
                    vibrate(context); onNumber("1")
                }
                NumberKey("2", Modifier.weight(1f).fillMaxHeight()) {
                    vibrate(context); onNumber("2")
                }
                NumberKey("3", Modifier.weight(1f).fillMaxHeight()) {
                    vibrate(context); onNumber("3")
                }
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberKey("4", Modifier.weight(1f).fillMaxHeight()) {
                    vibrate(context); onNumber("4")
                }
                NumberKey("5", Modifier.weight(1f).fillMaxHeight()) {
                    vibrate(context); onNumber("5")
                }
                NumberKey("6", Modifier.weight(1f).fillMaxHeight()) {
                    vibrate(context); onNumber("6")
                }
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberKey("7", Modifier.weight(1f).fillMaxHeight()) {
                    vibrate(context); onNumber("7")
                }
                NumberKey("8", Modifier.weight(1f).fillMaxHeight()) {
                    vibrate(context); onNumber("8")
                }
                NumberKey("9", Modifier.weight(1f).fillMaxHeight()) {
                    vibrate(context); onNumber("9")
                }
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberKey("0", Modifier.weight(2f).fillMaxHeight()) {
                    vibrate(context); onNumber("0")
                }
                NumberKey(".", Modifier.weight(1f).fillMaxHeight()) {
                    vibrate(context); onDecimal()
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionKey(
                label = "⌫",
                modifier = Modifier.weight(1f).fillMaxWidth(),
                fontSize = 22.sp,
                onClick = {
                    vibrate(context); onBackspace()
                },
                onLongClick = {
                    vibrate(context); onClear()
                }
            )
            ActionKey(
                label = "完成",
                modifier = Modifier.weight(3f).fillMaxWidth(),
                bgColor = primary,
                textColor = onPrimary,
                fontSize = 18.sp,
                enabled = doneEnabled,
                onClick = {
                    vibrate(context); onDone()
                }
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NumberKey(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val onSurface = MiuixTheme.colorScheme.onSurface
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = tween(120, easing = MotionScheme.EasingStandard),
        label = "numScale"
    )
    val bg by animateColorAsState(
        targetValue = if (pressed) onSurface.copy(alpha = 0.14f)
        else onSurface.copy(alpha = 0.06f),
        animationSpec = tween(120, easing = MotionScheme.EasingStandard),
        label = "numBg"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = onSurface
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ActionKey(
    label: String,
    modifier: Modifier = Modifier,
    bgColor: Color? = null,
    textColor: Color? = null,
    fontSize: TextUnit = 22.sp,
    enabled: Boolean = true,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    val onSurface = MiuixTheme.colorScheme.onSurface
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val baseBg = bgColor ?: onSurface.copy(alpha = 0.06f)
    val pressedBg = if (bgColor != null) {
        bgColor.copy(alpha = 0.80f)
    } else {
        onSurface.copy(alpha = 0.14f)
    }

    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.94f else 1f,
        animationSpec = tween(120, easing = MotionScheme.EasingStandard),
        label = "actScale"
    )
    val bg by animateColorAsState(
        targetValue = if (pressed && enabled) pressedBg else baseBg,
        animationSpec = tween(120, easing = MotionScheme.EasingStandard),
        label = "actBg"
    )

    val finalText = textColor ?: onSurface
    val textAlpha = if (enabled) 1f else 0.35f

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
                onLongClick = onLongClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            color = finalText.copy(alpha = textAlpha)
        )
    }
}

