package com.natsuki.qingjizhang

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import kotlinx.coroutines.delay
@Composable
fun StatisticsScreen(
    viewModel: BillViewModel,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    onBack: () -> Unit,
    onShareImage: (Boolean) -> Unit = {},
    isActive: Boolean = true
) {
    val bills by viewModel.bills.collectAsState(initial = emptyList())

    var isYearMode by remember { mutableStateOf(false) }

    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()

    val expenseBills = bills.filter { it.amount < 0 }
    val incomeBills = bills.filter { it.amount > 0 }

    val totalExpense = expenseBills.sumOf { -it.amount }
    val totalIncome = incomeBills.sumOf { it.amount }

    val expenseByCategory = expenseBills.groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { -it.amount } }
        .toList()
        .sortedByDescending { it.second }

    val incomeByCategory = incomeBills.groupBy { it.category }
        .mapValues { (_, list) -> list.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部栏
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "返回", tint = textPrimary)
            }
            Text(
                "统计分析",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onShareImage(isYearMode) }) {
                Icon(Icons.Filled.Share, "分享账单图片", tint = textPrimary)
            }
        }

        // 月 / 年 切换按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmallTabButton(
                label = "月",
                selected = !isYearMode,
                primaryColor = primaryColor,
                onClick = { isYearMode = false }
            )
            Spacer(Modifier.width(12.dp))
            SmallTabButton(
                label = "年",
                selected = isYearMode,
                primaryColor = primaryColor,
                onClick = { isYearMode = true }
            )
        }

        AnimatedContent(
            targetState = isYearMode,
            transitionSpec = {
                if (targetState) {
                    (slideInHorizontally(
                        initialOffsetX = { it / 6 },
                        animationSpec = tween(
                            durationMillis = MotionScheme.DurationMedium,
                            easing = MotionScheme.EasingEnter
                        )
                    ) + fadeIn(tween(
                        durationMillis = MotionScheme.DurationMedium - 40,
                        easing = MotionScheme.EasingEnter
                    )))
                        .togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { -it / 6 },
                                animationSpec = tween(
                                    durationMillis = MotionScheme.DurationMedium,
                                    easing = MotionScheme.EasingExit
                                )
                            ) + fadeOut(tween(
                                durationMillis = MotionScheme.DurationShort,
                                easing = MotionScheme.EasingExit
                            ))
                        )
                } else {
                    (slideInHorizontally(
                        initialOffsetX = { -it / 6 },
                        animationSpec = tween(
                            durationMillis = MotionScheme.DurationMedium,
                            easing = MotionScheme.EasingEnter
                        )
                    ) + fadeIn(tween(
                        durationMillis = MotionScheme.DurationMedium - 40,
                        easing = MotionScheme.EasingEnter
                    )))
                        .togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { it / 6 },
                                animationSpec = tween(
                                    durationMillis = MotionScheme.DurationMedium,
                                    easing = MotionScheme.EasingExit
                                )
                            ) + fadeOut(tween(
                                durationMillis = MotionScheme.DurationShort,
                                easing = MotionScheme.EasingExit
                            ))
                        )
                }
            },
            label = "MonthYearSwitch"
        ) { yearMode ->
            val listState = rememberLazyListState()
            EdgeHapticEffect(listState)

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!yearMode) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = cardBg,
                            tonalElevation = 0.dp,
                            shadowElevation = 0.dp
                        ) {
                            MonthlyTrendChart(
                                viewModel = viewModel,
                                cardBg = cardBg,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SummaryCard(
                                "总支出", totalExpense, Color(0xFFE53935), cardBg, textSecondary, Modifier.weight(1f),
                                isActive = isActive
                            )
                            SummaryCard(
                                "总收入", totalIncome, Color(0xFF43A047), cardBg, textSecondary, Modifier.weight(1f),
                                isActive = isActive
                            )
                        }
                    }

                    if (expenseByCategory.isNotEmpty()) {
                        item {
                            Text("支出分类", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.padding(top = 8.dp))
                        }
                        items(expenseByCategory.size) { idx ->
                            val (cat, amount) = expenseByCategory[idx]
                            CategoryBar(cat, amount, totalExpense, textPrimary, textSecondary, cardBg, primaryColor)
                        }
                    }

                    if (incomeByCategory.isNotEmpty()) {
                        item {
                            Text("收入分类", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textPrimary, modifier = Modifier.padding(top = 12.dp))
                        }
                        items(incomeByCategory.size) { idx ->
                            val (cat, amount) = incomeByCategory[idx]
                            CategoryBar(cat, amount, totalIncome, textPrimary, textSecondary, cardBg, Color(0xFF43A047))
                        }
                    }
                } else {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = cardBg,
                            tonalElevation = 0.dp,
                            shadowElevation = 0.dp
                        ) {
                            YearlyStatsView(
                                viewModel = viewModel,
                                primaryColor = primaryColor,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary
                            )
                        }
                    }
                }

                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = cardBg,
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        HeatmapChart(
                            viewModel = viewModel,
                            primaryColor = primaryColor,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            mode = if (yearMode) HeatmapMode.YEAR else HeatmapMode.MONTH,
                            year = selectedYear,
                            month = selectedMonth
                        )
                    }
                }

                item { Spacer(Modifier.height(180.dp)) }
            }
        }
    }
}

@Composable
private fun SmallTabButton(
    label: String,
    selected: Boolean,
    primaryColor: Color,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) primaryColor else Color.Transparent,
        animationSpec = tween(MotionScheme.DurationShort),
        label = "tabBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) Color.White else primaryColor,
        animationSpec = tween(MotionScheme.DurationShort),
        label = "tabText"
    )
    val borderAlpha by animateFloatAsState(
        targetValue = if (selected) 0f else 0.5f,
        animationSpec = tween(MotionScheme.DurationShort),
        label = "tabBorder"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(
                width = 1.dp,
                color = primaryColor.copy(alpha = borderAlpha),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: Double,
    color: Color,
    cardBg: Color,
    textSecondary: Color,
    modifier: Modifier = Modifier,
    isActive: Boolean = true
) {
    var displayAmount by remember { mutableStateOf(0.0) }


    LaunchedEffect(isActive, amount) {
        if (isActive) {
            displayAmount = 0.0
            delay(20)
        }
        displayAmount = amount
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = cardBg,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 13.sp, color = textSecondary)
            Spacer(Modifier.height(6.dp))
            AnimatedAmount(
                value = displayAmount,
                style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

@Composable
private fun CategoryBar(
    category: String, amount: Double, total: Double,
    textPrimary: Color, textSecondary: Color, cardBg: Color, barColor: Color
) {
    val percent = if (total > 0) (amount / total).toFloat() else 0f
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = cardBg, tonalElevation = 0.dp, shadowElevation = 0.dp) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(barColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(getCategoryIcon(category), null, tint = barColor, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(category, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = textPrimary, modifier = Modifier.weight(1f))
                Text("¥$amount", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = textPrimary)
            }
            Spacer(Modifier.height(10.dp))
            Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(barColor.copy(alpha = 0.15f))) {
                Box(modifier = Modifier.fillMaxWidth(percent).fillMaxHeight().clip(RoundedCornerShape(4.dp)).background(barColor))
            }
            Spacer(Modifier.height(6.dp))
            Text("${(percent * 100).toInt()}%", fontSize = 12.sp, color = textSecondary)
        }
    }
}