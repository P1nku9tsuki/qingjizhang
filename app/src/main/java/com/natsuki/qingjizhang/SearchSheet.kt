package com.natsuki.qingjizhang

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlinx.coroutines.delay


private enum class TimeFilter(val label: String) {
    ALL("全部"),
    TODAY("今天"),
    WEEK("本周"),
    MONTH("本月"),
    YEAR("今年")
}

private enum class TypeFilter(val label: String) {
    ALL("全部"),
    EXPENSE("支出"),
    INCOME("收入")
}

private enum class AmountFilter(val label: String) {
    ALL("不限"),
    LT_100("0-100"),
    R_100_500("100-500"),
    R_500_1000("500-1000"),
    GT_1000("1000+")
}

private enum class SortMode(val label: String) {
    TIME_DESC("时间↓"),
    AMOUNT_DESC("金额↓"),
    AMOUNT_ASC("金额↑")
}

@Composable
fun SearchSheet(
    onDismiss: () -> Unit,
    onEditBill: (BillEntity) -> Unit,
    viewModel: BillViewModel
) {
    val context = LocalContext.current
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    val textPrimary: Color = MiuixTheme.colorScheme.onSurface
    val textSecondary: Color = if (isDark) Color(0xFFAAAAAA) else Color(0xFF666666)
    val primaryColor: Color = MiuixTheme.colorScheme.primary
    val surfaceColor = MiuixTheme.colorScheme.surface

    var query by remember { mutableStateOf("") }
    var allBills by remember { mutableStateOf<List<BillEntity>>(emptyList()) }

    var timeFilter by remember { mutableStateOf(TimeFilter.ALL) }
    var typeFilter by remember { mutableStateOf(TypeFilter.ALL) }
    var amountFilter by remember { mutableStateOf(AmountFilter.ALL) }
    var sortMode by remember { mutableStateOf(SortMode.TIME_DESC) }
    var showFilterPanel by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(320)
        allBills = viewModel.getAllBillsOnce()
        delay(80)
        focusRequester.requestFocus()
    }

    val hasFilter = timeFilter != TimeFilter.ALL ||
            typeFilter != TypeFilter.ALL ||
            amountFilter != AmountFilter.ALL

    val results = remember(query, allBills, timeFilter, typeFilter, amountFilter, sortMode) {
        val q = query.trim()
        val numQuery = q.toDoubleOrNull()
        val absNumQuery = if (numQuery != null) kotlin.math.abs(numQuery) else null

        // 1. 关键词过滤
        var filtered = allBills.filter { bill ->
            if (q.isBlank()) {
                true
            } else {
                val matchTitle = bill.title.contains(q, ignoreCase = true)
                val matchCategory = bill.category.contains(q, ignoreCase = true)
                val matchAmount = if (absNumQuery != null) {
                    val billAbs = kotlin.math.abs(bill.amount)
                    billAbs == absNumQuery ||
                            billAbs.toString().startsWith(absNumQuery.toString().removeSuffix(".0")) ||
                            billAbs.toString().contains(absNumQuery.toString().removeSuffix(".0"))
                } else false
                matchTitle || matchCategory || matchAmount
            }
        }

        val now = java.time.LocalDate.now()
        filtered = when (timeFilter) {
            TimeFilter.ALL -> filtered
            TimeFilter.TODAY -> filtered.filter {
                it.date.startsWith(now.toString())
            }
            TimeFilter.WEEK -> {
                val weekStart = now.minusDays((now.dayOfWeek.value - 1).toLong())
                filtered.filter {
                    val d = it.date.substringBefore(" ")
                    try {
                        val billDate = java.time.LocalDate.parse(d)
                        !billDate.isBefore(weekStart) && !billDate.isAfter(now)
                    } catch (e: Exception) { false }
                }
            }
            TimeFilter.MONTH -> filtered.filter {
                val d = it.date.substringBefore(" ")
                d.startsWith("${now.year}-${String.format("%02d", now.monthValue)}")
            }
            TimeFilter.YEAR -> filtered.filter {
                it.date.startsWith("${now.year}-")
            }
        }

        filtered = when (typeFilter) {
            TypeFilter.ALL -> filtered
            TypeFilter.EXPENSE -> filtered.filter { it.amount < 0 }
            TypeFilter.INCOME -> filtered.filter { it.amount > 0 }
        }

        filtered = when (amountFilter) {
            AmountFilter.ALL -> filtered
            AmountFilter.LT_100 -> filtered.filter { kotlin.math.abs(it.amount) < 100 }
            AmountFilter.R_100_500 -> filtered.filter {
                val a = kotlin.math.abs(it.amount); a >= 100 && a < 500
            }
            AmountFilter.R_500_1000 -> filtered.filter {
                val a = kotlin.math.abs(it.amount); a >= 500 && a < 1000
            }
            AmountFilter.GT_1000 -> filtered.filter { kotlin.math.abs(it.amount) >= 1000 }
        }

        filtered = when (sortMode) {
            SortMode.TIME_DESC -> filtered.sortedByDescending { it.date }
            SortMode.AMOUNT_DESC -> filtered.sortedByDescending { kotlin.math.abs(it.amount) }
            SortMode.AMOUNT_ASC -> filtered.sortedBy { kotlin.math.abs(it.amount) }
        }

        filtered
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(surfaceColor)
    ) {

        Box(
            modifier = Modifier
                .padding(top = 12.dp)
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(textSecondary.copy(alpha = 0.4f))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(16.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text(
                        "备注/分类/金额",
                        color = textSecondary.copy(alpha = 0.6f),
                        fontSize = 15.sp
                    )
                },
                leadingIcon = {
                    Icon(Icons.Filled.Search, null, tint = primaryColor)
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Filled.Close, "清空", tint = textSecondary)
                        }
                    }
                },
                singleLine = true,
                textStyle = TextStyle(fontSize = 16.sp, color = textPrimary),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = textSecondary.copy(alpha = 0.3f),
                    cursorColor = primaryColor
                )
            )

            Spacer(Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (hasFilter) primaryColor.copy(alpha = 0.15f)
                        else textSecondary.copy(alpha = 0.10f)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        vibrate(context)
                        showFilterPanel = !showFilterPanel
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.FilterList,
                    contentDescription = "筛选",
                    tint = if (hasFilter) primaryColor else textSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        if (hasFilter) {
            Spacer(Modifier.height(10.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (timeFilter != TimeFilter.ALL) {
                    item {
                        FilterChip(
                            label = timeFilter.label,
                            primary = primaryColor,
                            onRemove = { timeFilter = TimeFilter.ALL }
                        )
                    }
                }
                if (typeFilter != TypeFilter.ALL) {
                    item {
                        FilterChip(
                            label = typeFilter.label,
                            primary = primaryColor,
                            onRemove = { typeFilter = TypeFilter.ALL }
                        )
                    }
                }
                if (amountFilter != AmountFilter.ALL) {
                    item {
                        FilterChip(
                            label = "¥${amountFilter.label}",
                            primary = primaryColor,
                            onRemove = { amountFilter = AmountFilter.ALL }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showFilterPanel,
            enter = expandVertically(tween(280, easing = MotionScheme.EasingEnter)) + fadeIn(tween(220)),
            exit = shrinkVertically(tween(220, easing = MotionScheme.EasingExit)) + fadeOut(tween(160))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                FilterGroup(
                    label = "时间",
                    options = TimeFilter.entries.map { it.label },
                    selectedIndex = TimeFilter.entries.indexOf(timeFilter),
                    primary = primaryColor,
                    textSecondary = textSecondary,
                    onSelect = { timeFilter = TimeFilter.entries[it] }
                )
                Spacer(Modifier.height(10.dp))
                FilterGroup(
                    label = "类型",
                    options = TypeFilter.entries.map { it.label },
                    selectedIndex = TypeFilter.entries.indexOf(typeFilter),
                    primary = primaryColor,
                    textSecondary = textSecondary,
                    onSelect = { typeFilter = TypeFilter.entries[it] }
                )
                Spacer(Modifier.height(10.dp))
                FilterGroup(
                    label = "金额",
                    options = AmountFilter.entries.map { it.label },
                    selectedIndex = AmountFilter.entries.indexOf(amountFilter),
                    primary = primaryColor,
                    textSecondary = textSecondary,
                    onSelect = { amountFilter = AmountFilter.entries[it] }
                )
                Spacer(Modifier.height(10.dp))
                FilterGroup(
                    label = "排序",
                    options = SortMode.entries.map { it.label },
                    selectedIndex = SortMode.entries.indexOf(sortMode),
                    primary = primaryColor,
                    textSecondary = textSecondary,
                    onSelect = { sortMode = SortMode.entries[it] }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        if (query.isNotBlank() || hasFilter) {
            Text(
                text = "${results.size} 条结果",
                fontSize = 12.sp,
                color = textSecondary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(6.dp))
        }

        when {
            query.isBlank() && !hasFilter -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("输入关键词或点筛选开始搜索", color = textSecondary, fontSize = 14.sp)
                }
            }
            results.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("没有找到匹配的账单", color = textSecondary, fontSize = 14.sp)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(results.size) { index ->
                        val bill = results[index]
                        SearchResultItem(
                            bill = bill,
                            textPrimary = textPrimary,
                            textSecondary = textSecondary,
                            isDark = isDark,
                            onClick = {
                                vibrate(context)
                                onEditBill(bill)
                                onDismiss()
                            }
                        )
                    }
                    item { Spacer(Modifier.height(60.dp)) }
                }
            }
        }
    }
}


@Composable
private fun FilterGroup(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    primary: Color,
    textSecondary: Color,
    onSelect: (Int) -> Unit
) {
    val context = LocalContext.current
    Column {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = textSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEachIndexed { index, option ->
                val selected = index == selectedIndex
                val bg by animateColorAsState(
                    targetValue = if (selected) primary else textSecondary.copy(alpha = 0.10f),
                    animationSpec = tween(180),
                    label = "chipBg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (selected) Color.White else textSecondary,
                    animationSpec = tween(180),
                    label = "chipText"
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(bg)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            vibrate(context)
                            onSelect(index)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        option,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    label: String,
    primary: Color,
    onRemove: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(primary.copy(alpha = 0.12f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                vibrate(context)
                onRemove()
            }
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 12.sp, color = primary, fontWeight = FontWeight.Medium)
        Spacer(Modifier.width(4.dp))
        Icon(
            Icons.Filled.Close,
            contentDescription = null,
            tint = primary,
            modifier = Modifier.size(12.dp)
        )
    }
}

@Composable
private fun SearchResultItem(
    bill: BillEntity,
    textPrimary: Color,
    textSecondary: Color,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val itemColor = if (bill.amount < 0) {
        if (isDark) Color(0xFFE57373) else Color(0xFFE53935)
    } else {
        if (isDark) Color(0xFF81C784) else Color(0xFF43A047)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(textSecondary.copy(alpha = 0.06f))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(itemColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                getCategoryIcon(bill.category),
                contentDescription = null,
                tint = itemColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(
                bill.title,
                fontWeight = FontWeight.Medium,
                color = textPrimary,
                fontSize = 15.sp
            )
            Text(
                "${bill.category} · ${bill.date}",
                fontSize = 12.sp,
                color = textSecondary
            )
        }
        Text(
            text = if (bill.amount < 0) "-¥${formatAmount(-bill.amount)}" else "+¥${formatAmount(bill.amount)}",
            color = itemColor,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}