package com.natsuki.qingjizhang

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun SavingsGoalCard(
    goal: SavingsGoal,
    allBills: List<BillEntity>,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    primaryColor: Color,
    successColor: Color,
    onEdit: () -> Unit
) {
    val now = System.currentTimeMillis()
    val daysSinceCreated = ((now - goal.createdAt) / 86_400_000L).coerceAtLeast(1)
    val savingsSince = remember(goal, allBills) {
        allBills
            .filter { parseBillMillis(it.date) >= goal.createdAt }
            .sumOf { it.amount }
    }
    val currentSaved = savingsSince.coerceAtLeast(0.0)
    val progress = (currentSaved / goal.amount).toFloat().coerceIn(0f, 1f)
    val isAchieved = currentSaved >= goal.amount
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(700, easing = MotionScheme.EasingStandard),
        label = "savingsProgress"
    )
    val daysLeft: Int? = if (!isAchieved && currentSaved > 0 && daysSinceCreated >= 1) {
        val dailyRate = currentSaved / daysSinceCreated
        if (dailyRate > 0) {
            ((goal.amount - currentSaved) / dailyRate).toInt().coerceAtLeast(1)
        } else null
    } else null
    val accent = if (isAchieved) successColor else primaryColor

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(cardBg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onEdit() }
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🎯", fontSize = 14.sp)
            }
            Spacer(Modifier.width(8.dp))
            Text(
                if (goal.name.isBlank()) "储蓄目标" else goal.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary,
                modifier = Modifier.weight(1f)
            )
            if (isAchieved) {
                Text(
                    "已达成",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = successColor
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                "¥${formatAmount(currentSaved)}",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = accent,
                style = TextStyle(fontFeatureSettings = "tnum")
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "/ ¥${formatAmount(goal.amount)}",
                fontSize = 13.sp,
                color = textSecondary,
                style = TextStyle(fontFeatureSettings = "tnum"),
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }

        Spacer(Modifier.height(10.dp))

        // 进度条
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(accent.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(4.dp))
                    .background(accent)
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = when {
                isAchieved -> "太棒了，目标已达成 🎉"
                daysLeft == null -> "继续记账，稍后为你估算还需多久"
                daysLeft <= 30 -> "按当前速度，还需约 $daysLeft 天"
                else -> "按当前速度，还需约 ${daysLeft / 30} 个月"
            },
            fontSize = 12.sp,
            color = textSecondary
        )
    }
}
private fun parseBillMillis(date: String): Long {
    return try {
        val fmt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        fmt.parse(date)?.time ?: 0L
    } catch (e: Exception) {
        0L
    }
}

