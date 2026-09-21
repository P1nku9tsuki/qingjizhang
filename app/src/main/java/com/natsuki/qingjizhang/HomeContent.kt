package com.natsuki.qingjizhang

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun HomeContent(
    onShowDetail: (BillEntity) -> Unit,
    hazeState: HazeState,
    onBalanceClick: () -> Unit,
    onShowMonthPicker: () -> Unit,
    onAddBill: () -> Unit = {},
    selectionMode: Boolean = false,
    selectedIds: Set<Long> = emptySet(),
    onEnterSelectionMode: (BillEntity) -> Unit = {},
    onToggleSelection: (BillEntity) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val themeViewModel: ThemeViewModel = viewModel()
    val currentMode = themeViewModel.themeController.colorSchemeMode
    val isDark = when (currentMode) {
        ColorSchemeMode.Light, ColorSchemeMode.MonetLight -> false
        ColorSchemeMode.Dark, ColorSchemeMode.MonetDark -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    val textPrimary: Color = MiuixTheme.colorScheme.onSurface
    val textSecondary: Color = if (isDark) Color(0xFFAAAAAA) else Color(0xFF666666)
    val primaryColor: Color = MiuixTheme.colorScheme.primary

    val incomeColor = if (isDark) Color(0xFF81C784) else Color(0xFF43A047)
    val expenseColor = if (isDark) Color(0xFFE57373) else Color(0xFFE53935)
    val warningColor = if (isDark) Color(0xFFFFB74D) else Color(0xFFF57C00)
    val dangerColor = if (isDark) Color(0xFFEF5350) else Color(0xFFE53935)
    val successColor = if (isDark) Color(0xFF81C784) else Color(0xFF43A047)

    val cardBgSolid = if (isDark) {
        primaryColor.copy(alpha = 0.18f).compositeOver(Color(0xFF121212))
    } else {
        primaryColor.copy(alpha = 0.10f).compositeOver(Color(0xFFFFFFFF))
    }

    val billViewModel: BillViewModel = viewModel()
    val bills by billViewModel.bills.collectAsState(initial = emptyList())
    val allBillsForSavings by billViewModel.allBillsFlow.collectAsState(initial = emptyList())
    val selectedYear by billViewModel.selectedYear.collectAsState()
    val selectedMonth by billViewModel.selectedMonth.collectAsState()

    val budgetViewModel: BudgetViewModel = viewModel()
    val monthlyBudget by budgetViewModel.budget.collectAsState()
    val categoryBudgets by budgetViewModel.categoryBudgets.collectAsState()
    val savingsGoal by budgetViewModel.savingsGoal.collectAsState()   // 👈 外层

    val totals by remember(bills) {
        derivedStateOf {
            val income = bills.filter { it.amount > 0 }.sumOf { it.amount }
            val expense = bills.filter { it.amount < 0 }.sumOf { it.amount }
            Triple(income, expense, income + expense)
        }
    }
    val totalIncome = totals.first
    val totalExpense = totals.second
    val balance = totals.third

    val spentAmount = Math.abs(totalExpense)
    val budgetProgress = if (monthlyBudget > 0) (spentAmount / monthlyBudget).toFloat().coerceIn(0f, 1f) else 0f
    val overBudget = monthlyBudget > 0 && spentAmount > monthlyBudget
    val warningBudget = monthlyBudget > 0 && budgetProgress >= 0.8f && !overBudget

    val currentMonthKey = remember(selectedYear, selectedMonth) {
        "%04d-%02d".format(selectedYear, selectedMonth)
    }

    val preferences = remember { ThemePreferences(context.applicationContext) }
    val dismissedKey by preferences.budgetAlertDismissedFlow.collectAsState(initial = "")

    val showBudgetAlert = monthlyBudget > 0 &&
            (warningBudget || overBudget) &&
            dismissedKey != currentMonthKey

    val balanceBg = primaryColor.copy(alpha = if (isDark) 0.20f else 0.15f)
    val balanceCorner = RoundedCornerShape(20.dp)

    val streak = remember(bills) { calculateStreak(bills) }

    val groupedBills = remember(bills) {
        bills.groupBy { it.date.substringBefore(" ") }
            .toList()
            .sortedByDescending { (date, _) -> date }
    }

    val topExpenseCategories = remember(bills) {
        bills.filter { it.amount < 0 }
            .groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { -it.amount } }
            .toList()
            .sortedByDescending { it.second }
            .take(5)
    }

    val listState = rememberLazyListState()
    EdgeHapticEffect(listState)

    val density = LocalDensity.current
    val itemOffsetPx = remember(density) { with(density) { 10.dp.toPx() } }

    val appearedBillIds = remember { mutableStateListOf<Long>() }
    val appearedDateHeaders = remember { mutableStateListOf<String>() }
    val appearSlotCounter = remember { androidx.compose.runtime.mutableIntStateOf(0) }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 月份切换栏
        item(key = "month_header", contentType = "header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedYear}年${selectedMonth}月",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            vibrate(context)
                            onShowMonthPicker()
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // 预算预警条
        if (showBudgetAlert) {
            item(key = "budget_alert", contentType = "alert") {
                BudgetAlertBar(
                    isOver = overBudget,
                    spentAmount = spentAmount,
                    monthlyBudget = monthlyBudget,
                    progress = budgetProgress,
                    warningColor = warningColor,
                    dangerColor = dangerColor,
                    textPrimary = textPrimary,
                    onDismiss = {
                        vibrate(context)
                        scope.launch {
                            preferences.dismissBudgetAlert(currentMonthKey)
                        }
                    }
                )
            }
        }

        // 结余卡片
        item(key = "balance_card", contentType = "balance") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(balanceCorner)
                    .background(balanceBg)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        vibrate(context)
                        onBalanceClick()
                    }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("${selectedMonth}月结余", fontSize = 14.sp, color = textSecondary)

                    AnimatedAmount(
                        value = balance,
                        style = TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = primaryColor,
                        modifier = Modifier.padding(vertical = 8.dp),
                        heroStyle = true
                    )

                    if (monthlyBudget > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "已用 ¥${formatAmount(spentAmount)} / ¥${formatAmount(monthlyBudget)}",
                                fontSize = 12.sp,
                                color = if (overBudget) dangerColor
                                else if (warningBudget) warningColor
                                else textSecondary
                            )
                            Text(
                                text = if (overBudget) "超支 ¥${formatAmount(spentAmount - monthlyBudget)}"
                                else "剩余 ¥${formatAmount(monthlyBudget - spentAmount)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (overBudget) dangerColor
                                else if (warningBudget) warningColor
                                else textSecondary
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(primaryColor.copy(alpha = 0.15f))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(budgetProgress)
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        when {
                                            overBudget -> dangerColor
                                            warningBudget -> warningColor
                                            else -> primaryColor
                                        }
                                    )
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("${selectedMonth}月收入", fontSize = 12.sp, color = textSecondary)
                            StaticAmount(
                                text = "+¥${formatAmount(totalIncome)}",
                                style = TextStyle(fontWeight = FontWeight.Bold),
                                color = incomeColor
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${selectedMonth}月支出", fontSize = 12.sp, color = textSecondary)
                            StaticAmount(
                                text = "-¥${formatAmount(Math.abs(totalExpense))}",
                                style = TextStyle(fontWeight = FontWeight.Bold),
                                color = expenseColor
                            )
                        }
                    }
                }
            }
        }

        // 👇 储蓄目标卡片
        if (savingsGoal != null) {
            item(key = "savings_goal_card", contentType = "savings") {
                SavingsGoalCard(
                    goal = savingsGoal!!,
                    allBills = allBillsForSavings,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    cardBg = cardBgSolid,
                    primaryColor = primaryColor,
                    successColor = successColor,
                    onEdit = onBalanceClick
                )
            }
        }

        // 环形图卡片
        if (topExpenseCategories.isNotEmpty()) {
            item(key = "ring_card", contentType = "ring") {
                RingChartCard(
                    categories = topExpenseCategories,
                    totalExpense = spentAmount,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    cardBg = cardBgSolid,
                    categoryBudgets = categoryBudgets,
                    warningColor = warningColor,
                    dangerColor = dangerColor,
                    onMore = onBalanceClick
                )
            }
        }

        // 连续记账天数
        item(key = "streak", contentType = "streak") {
            if (streak > 0 && !selectionMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(primaryColor.copy(alpha = 0.10f))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔥", fontSize = 16.sp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "已连续记账 $streak 天",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = primaryColor
                    )
                }
            }
        }

        // 最近账单标题
        item(key = "list_header", contentType = "header") {
            Text(
                "最近账单",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // 账单列表
        if (bills.isEmpty()) {
            item(key = "empty", contentType = "empty") {
                EmptyState(
                    primaryColor = primaryColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onAddBill = onAddBill
                )
            }
        } else {
            groupedBills.forEach { (date, dayBills) ->
                item(key = "date_header_$date", contentType = "date_header") {
                    val headerHasAppeared = date in appearedDateHeaders
                    val headerAlpha by animateFloatAsState(
                        targetValue = if (headerHasAppeared) 1f else 0f,
                        animationSpec = tween(220, easing = MotionScheme.EasingStandard),
                        label = "headerAlpha"
                    )
                    val headerOffset by animateFloatAsState(
                        targetValue = if (headerHasAppeared) 0f else 1f,
                        animationSpec = tween(260, easing = MotionScheme.EasingStandard),
                        label = "headerOffset"
                    )
                    LaunchedEffect(date) {
                        if (!headerHasAppeared) {
                            val slot = appearSlotCounter.intValue.coerceAtMost(8)
                            appearSlotCounter.intValue += 1
                            delay((slot * 40).toLong())
                            appearedDateHeaders.add(date)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = headerAlpha
                                translationY = headerOffset * itemOffsetPx
                            }
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatDateHeader(date),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = textSecondary
                        )
                        Spacer(Modifier.width(8.dp))
                        val dayExpense = dayBills.filter { it.amount < 0 }.sumOf { -it.amount }
                        val dayIncome = dayBills.filter { it.amount > 0 }.sumOf { it.amount }
                        val daySummary = buildString {
                            if (dayExpense > 0) append("支出 ¥${formatAmount(dayExpense)}")
                            if (dayIncome > 0) {
                                if (isNotEmpty()) append(" · ")
                                append("收入 ¥${formatAmount(dayIncome)}")
                            }
                        }
                        Text(
                            text = daySummary,
                            fontSize = 11.sp,
                            color = textSecondary.copy(alpha = 0.7f),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                items(
                    count = dayBills.size,
                    key = { dayBills[it].id },
                    contentType = { "bill_item" }
                ) { index ->
                    val bill = dayBills[index]
                    val itemColor = if (bill.amount < 0) expenseColor else incomeColor
                    val isSelected = bill.id in selectedIds

                    val hasAppeared = bill.id in appearedBillIds
                    val animAlpha by animateFloatAsState(
                        targetValue = if (hasAppeared) 1f else 0f,
                        animationSpec = tween(220, easing = MotionScheme.EasingStandard),
                        label = "billAlpha"
                    )
                    val animOffset by animateFloatAsState(
                        targetValue = if (hasAppeared) 0f else 1f,
                        animationSpec = tween(260, easing = MotionScheme.EasingStandard),
                        label = "billOffset"
                    )
                    LaunchedEffect(bill.id) {
                        if (!hasAppeared) {
                            val slot = appearSlotCounter.intValue.coerceAtMost(8)
                            appearSlotCounter.intValue += 1
                            delay((slot * 40).toLong())
                            appearedBillIds.add(bill.id)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = animAlpha
                                translationY = animOffset * itemOffsetPx
                            }
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (selectionMode && isSelected) {
                                    primaryColor.copy(alpha = if (isDark) 0.25f else 0.15f)
                                } else {
                                    cardBgSolid
                                }
                            )
                            .combinedClickable(
                                onClick = {
                                    vibrate(context)
                                    if (selectionMode) {
                                        onToggleSelection(bill)
                                    } else {
                                        onShowDetail(bill)
                                    }
                                },
                                onLongClick = {
                                    vibrate(context)
                                    if (!selectionMode) {
                                        onEnterSelectionMode(bill)
                                    } else {
                                        onToggleSelection(bill)
                                    }
                                }
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectionMode) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) primaryColor else Color.Transparent
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!isSelected) {
                                        androidx.compose.foundation.Canvas(
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            drawCircle(
                                                color = textSecondary.copy(alpha = 0.5f),
                                                radius = size.minDimension / 2f - 2f,
                                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f)
                                            )
                                        }
                                    } else {
                                        Icon(
                                            Icons.Filled.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                            }

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(itemColor.copy(alpha = if (isDark) 0.30f else 0.18f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    getCategoryIcon(bill.category),
                                    contentDescription = null,
                                    tint = itemColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(bill.title, fontWeight = FontWeight.Medium, color = textPrimary)
                                    if (bill.recurrenceType != "none") {
                                        Spacer(Modifier.width(6.dp))
                                        SyncTag(type = bill.recurrenceType, primaryColor = primaryColor, isDark = isDark)
                                    }
                                    if (bill.recurringId != 0L) {
                                        Spacer(Modifier.width(6.dp))
                                        AutoTag(primaryColor = primaryColor, isDark = isDark)
                                    }
                                }
                                Text(
                                    "${bill.category} · ${bill.date.substringAfter(" ")}",
                                    fontSize = 12.sp,
                                    color = textSecondary
                                )
                            }

                            StaticAmount(
                                text = if (bill.amount < 0) "-¥${formatAmount(-bill.amount)}" else "+¥${formatAmount(bill.amount)}",
                                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
                                color = itemColor
                            )
                        }
                    }
                }
            }
        }

        item(key = "bottom_spacer", contentType = "spacer") {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ============================================================
//  顶层函数
// ============================================================

@Composable
private fun BudgetAlertBar(
    isOver: Boolean,
    spentAmount: Double,
    monthlyBudget: Double,
    progress: Float,
    warningColor: Color,
    dangerColor: Color,
    textPrimary: Color,
    onDismiss: () -> Unit
) {
    val accent = if (isOver) dangerColor else warningColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(accent.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isOver) "⚠️ 本月预算已超支" else "⚠️ 本月预算快用完了",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = accent
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (isOver) {
                    "已用 ¥${formatAmount(spentAmount)} / ¥${formatAmount(monthlyBudget)}，超支 ¥${formatAmount(spentAmount - monthlyBudget)}"
                } else {
                    "已用 ${(progress * 100).toInt()}%，还剩 ¥${formatAmount(monthlyBudget - spentAmount)}"
                },
                fontSize = 12.sp,
                color = textPrimary.copy(alpha = 0.8f)
            )
        }

        Spacer(Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onDismiss()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Close,
                contentDescription = "关闭提醒",
                tint = accent,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun EmptyState(
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    onAddBill: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.AttachMoney,
                contentDescription = null,
                tint = primaryColor.copy(alpha = 0.7f),
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "还没有账单",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = textPrimary
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "记录第一笔，开始你的记账之旅",
            fontSize = 13.sp,
            color = textSecondary
        )

        Spacer(Modifier.height(20.dp))

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(primaryColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    vibrate(context)
                    onAddBill()
                }
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                "记一笔",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

private fun formatDateHeader(dateStr: String): String {
    return try {
        val date = java.time.LocalDate.parse(dateStr)
        val today = java.time.LocalDate.now()
        val yesterday = today.minusDays(1)
        val dayOfWeek = when (date.dayOfWeek.value) {
            1 -> "周一"; 2 -> "周二"; 3 -> "周三"; 4 -> "周四"
            5 -> "周五"; 6 -> "周六"; 7 -> "周日"
            else -> ""
        }
        when (date) {
            today -> "今天 · $dayOfWeek"
            yesterday -> "昨天 · $dayOfWeek"
            else -> "${date.monthValue}月${date.dayOfMonth}日 · $dayOfWeek"
        }
    } catch (e: Exception) {
        dateStr
    }
}

private fun calculateStreak(bills: List<BillEntity>): Int {
    if (bills.isEmpty()) return 0
    val datesWithBills = bills.mapNotNull {
        it.date.substringBefore(" ").takeIf { s -> s.isNotBlank() }
    }.toSet()
    if (datesWithBills.isEmpty()) return 0
    var streak = 0
    var cursor = java.time.LocalDate.now()
    if (!datesWithBills.contains(cursor.toString())) {
        cursor = cursor.minusDays(1)
    }
    while (datesWithBills.contains(cursor.toString())) {
        streak++
        cursor = cursor.minusDays(1)
    }
    return streak
}

@Composable
private fun SyncTag(type: String, primaryColor: Color, isDark: Boolean) {
    val label = when (type) {
        "daily" -> "每天"
        "weekly" -> "每周"
        "monthly" -> "每月"
        else -> "同步"
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(primaryColor.copy(alpha = if (isDark) 0.25f else 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Filled.Refresh, null, tint = primaryColor, modifier = Modifier.size(10.dp))
        Spacer(Modifier.width(2.dp))
        Text(label, fontSize = 10.sp, color = primaryColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AutoTag(primaryColor: Color, isDark: Boolean) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(primaryColor.copy(alpha = if (isDark) 0.18f else 0.10f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("自动", fontSize = 10.sp, color = primaryColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun MonthYearPickerSheet(
    initialYear: Int,
    initialMonth: Int,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    surfaceColor: Color,
    onDismiss: () -> Unit,
    onConfirm: (year: Int, month: Int) -> Unit
) {
    val years = remember { (2020..2030).toList() }
    val months = remember { (1..12).toList() }

    var selectedYear by remember { mutableIntStateOf(initialYear) }
    var selectedMonth by remember { mutableIntStateOf(initialMonth) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(surfaceColor)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(textSecondary.copy(alpha = 0.4f))
                .align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(20.dp))
        Text(
            "选择年月",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(primaryColor.copy(alpha = 0.1f))
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WheelPicker(
                    items = years.map { "$it 年" },
                    initialIndex = years.indexOf(initialYear).coerceAtLeast(0),
                    selectedColor = primaryColor,
                    unselectedColor = textSecondary,
                    onSelected = { idx -> selectedYear = years[idx] },
                    modifier = Modifier.weight(1.3f)
                )
                WheelPicker(
                    items = months.map { "$it 月" },
                    initialIndex = months.indexOf(initialMonth).coerceAtLeast(0),
                    selectedColor = primaryColor,
                    unselectedColor = textSecondary,
                    onSelected = { idx -> selectedMonth = months[idx] },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier.weight(1f).height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = textSecondary.copy(alpha = 0.15f),
                    contentColor = textPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("取消", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = { onConfirm(selectedYear, selectedMonth) },
                modifier = Modifier.weight(1f).height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("确定", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WheelPicker(
    items: List<String>,
    initialIndex: Int,
    selectedColor: Color,
    unselectedColor: Color,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    val currentIndex by remember {
        derivedStateOf { listState.firstVisibleItemIndex }
    }

    LaunchedEffect(listState) {
        var lastIndex = listState.firstVisibleItemIndex
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { idx ->
                if (idx != lastIndex) {
                    vibrate(context)
                    lastIndex = idx
                    onSelected(idx)
                }
            }
    }

    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        modifier = modifier.fillMaxHeight(),
        contentPadding = PaddingValues(vertical = 78.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items.size) { idx ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                contentAlignment = Alignment.Center
            ) {
                val isSelected = currentIndex == idx
                Text(
                    text = items[idx],
                    fontSize = if (isSelected) 20.sp else 16.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) selectedColor else unselectedColor
                )
            }
        }
    }
}

fun getCategoryIcon(category: String): ImageVector {
    //
    CategoryStore.customExpenseCats.firstOrNull { it.first == category }?.let {
        return it.second
    }
    CategoryStore.customIncomeCats.firstOrNull { it.first == category }?.let {
        return it.second
    }
    //
    return when (category) {
        "餐饮" -> Icons.Filled.Restaurant
        "交通" -> Icons.Filled.DirectionsCar
        "购物" -> Icons.Filled.ShoppingBag
        "学习" -> Icons.Filled.Book
        "收入" -> Icons.Filled.AttachMoney
        "娱乐" -> Icons.Filled.SportsEsports
        "医疗" -> Icons.Filled.LocalHospital
        "住房" -> Icons.Filled.Home
        "工资" -> Icons.Filled.AttachMoney
        "奖金" -> Icons.Filled.Star
        "投资" -> Icons.Filled.TrendingUp
        "兼职" -> Icons.Filled.Work
        "红包" -> Icons.Filled.Redeem
        else -> Icons.Filled.Category
    }
}