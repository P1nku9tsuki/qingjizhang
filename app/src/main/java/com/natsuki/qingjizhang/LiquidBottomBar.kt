package com.natsuki.qingjizhang

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun LiquidBottomBar(
    navItems: List<Pair<String, ImageVector>>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
    isDark: Boolean,
    hazeState: HazeState,
    modifier: Modifier = Modifier
) {
    val outerCorner = RoundedCornerShape(40.dp)

    val tintColor = if (isDark) {
        Color(0xFF1A1A1A).copy(alpha = 1.5f)
    } else {
        Color(0xFFFFFFFF).copy(alpha = 1.5f)
    }

    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.18f)
    } else {
        Color.White.copy(alpha = 0.8f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(outerCorner)
            .hazeEffect(
                state = hazeState,
                style = HazeStyle(
                    backgroundColor = tintColor,
                    tints = emptyList(),
                    blurRadius = 16.dp,
                    noiseFactor = 0f
                )
            )
            .border(0.8.dp, borderColor, outerCorner)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, (_, icon) ->
                val isSelected = selectedIndex == index

                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "bubbleScale"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onItemClick(index) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .scale(scale)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            MiuixTheme.colorScheme.primary.copy(alpha = 0f),
                                            MiuixTheme.colorScheme.primary.copy(alpha = 0.08f)
                                        )
                                    )
                                )
                                .border(
                                    width = 0.6.dp,
                                    color = MiuixTheme.colorScheme.primary.copy(alpha = 0.05f),
                                    shape = CircleShape
                                )
                        )
                    }

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) {
                            MiuixTheme.colorScheme.primary
                        } else {
                            MiuixTheme.colorScheme.onSurface
                        },
                        modifier = Modifier
                            .size(24.dp)
                            .scale(scale)
                    )
                }
            }
        }
    }
}