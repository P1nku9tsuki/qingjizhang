package com.natsuki.qingjizhang

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


fun getCategoryColor(category: String): Color = CategoryStore.getColor(category)

@Composable
fun RingChartCard(
    categories: List<Pair<String, Double>>,
    totalExpense: Double,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    categoryBudgets: Map<String, Double> = emptyMap(),
    warningColor: Color = Color(0xFFF57C00),
    dangerColor: Color = Color(0xFFE53935),
    onMore: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onMore() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 总览环形图
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2f
                val center = Offset(size.width / 2f, size.height / 2f)
                val topLeft = Offset(center.x - radius, center.y - radius)
                val arcSize = Size(radius * 2f, radius * 2f)

                drawArc(
                    color = textSecondary.copy(alpha = 0.10f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )

                if (totalExpense > 0) {
                    var startAngle = -90f
                    categories.forEach { (name, value) ->
                        val sweep = (value / totalExpense * 360f).toFloat()
                        val gap = if (sweep > 6f) 3f else 0f
                        drawArc(
                            color = getCategoryColor(name),
                            startAngle = startAngle + gap / 2f,
                            sweepAngle = (sweep - gap).coerceAtLeast(0.5f),
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                        )
                        startAngle += sweep
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "本月支出",
                    fontSize = 11.sp,
                    color = textSecondary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    formatAmount(totalExpense),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    style = TextStyle(fontFeatureSettings = "tnum")
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (categories.isEmpty()) {
                Text(
                    "本月暂无支出",
                    fontSize = 13.sp,
                    color = textSecondary
                )
            } else {
                categories.take(5).forEach { (name, value) ->
                    CategoryProgressRow(
                        name = name,
                        value = value,
                        budget = categoryBudgets[name],
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        warningColor = warningColor,
                        dangerColor = dangerColor
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryProgressRow(
    name: String,
    value: Double,
    budget: Double?,
    textPrimary: Color,
    textSecondary: Color,
    warningColor: Color,
    dangerColor: Color
) {
    val hasBudget = budget != null && budget > 0
    val progress = if (hasBudget) (value / budget!!).toFloat().coerceIn(0f, 1f) else 0f
    val isOver = hasBudget && value > budget!!
    val isWarning = hasBudget && !isOver && progress >= 0.8f

    val categoryColor = getCategoryColor(name)
    val ringColor = when {
        isOver -> dangerColor
        isWarning -> warningColor
        else -> categoryColor
    }

    val animatedProgress by animateFloatAsState(
        targetValue = if (hasBudget) progress else 0f,
        animationSpec = tween(500, easing = MotionScheme.EasingStandard),
        label = "catProgress"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {

        if (hasBudget) {
            Box(
                modifier = Modifier.size(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeW = 2.5.dp.toPx()
                    val radius = (size.minDimension - strokeW) / 2f
                    val topLeft = Offset(
                        (size.width - radius * 2f) / 2f,
                        (size.height - radius * 2f) / 2f
                    )
                    val arcSize = Size(radius * 2f, radius * 2f)

                    // 背景圈
                    drawArc(
                        color = textSecondary.copy(alpha = 0.15f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeW)
                    )
                    // 进度圈
                    drawArc(
                        color = ringColor,
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeW, cap = StrokeCap.Round)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(categoryColor)
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            name,
            fontSize = 13.sp,
            color = textPrimary,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = if (hasBudget && isOver) {
                "¥${formatAmount(value)} / ¥${formatAmount(budget!!)}"
            } else {
                "¥${formatAmount(value)}"
            },
            fontSize = 13.sp,
            fontWeight = if (isOver || isWarning) FontWeight.Bold else FontWeight.Medium,
            color = if (isOver) dangerColor else if (isWarning) warningColor else textPrimary,
            style = TextStyle(fontFeatureSettings = "tnum")
        )
    }
}