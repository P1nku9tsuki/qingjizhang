package com.natsuki.qingjizhang

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBillScreen(
    viewModel: BillViewModel,
    onSaved: () -> Unit,
    onStepChange: (Int) -> Unit = {},
    showManageCategories: Boolean = false,
    onManageCategoriesDismiss: () -> Unit = {}
) {
    // 步骤：0 类型 → 1 金额 → 2 备注 → 3 分类 → 4 自定义分类
    var currentStep by rememberSaveable { mutableIntStateOf(0) }
    var isExpense by rememberSaveable { mutableStateOf(true) }
    var amount by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("餐饮") }
    var note by rememberSaveable { mutableStateOf("") }
    var customInput by rememberSaveable { mutableStateOf("") }
    var selectedCustomIcon by remember { mutableStateOf(Icons.Filled.Category) }
    var selectedDate by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCalculator by remember { mutableStateOf(false) }

    var recurrenceType by rememberSaveable { mutableStateOf("none") }
    var showRecurrencePicker by remember { mutableStateOf(false) }

    var recommendedCategory by remember { mutableStateOf<String?>(null) }
    var recommendedKeyword by remember { mutableStateOf<String?>(null) }

    var fromTemplate by rememberSaveable { mutableStateOf(false) }

    val customExpenseCats = CategoryStore.customExpenseCats
    val customIncomeCats = CategoryStore.customIncomeCats

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val themeViewModel: ThemeViewModel = viewModel()
    val currentMode = themeViewModel.themeController.colorSchemeMode
    val isDark = when (currentMode) {
        ColorSchemeMode.Light, ColorSchemeMode.MonetLight -> false
        ColorSchemeMode.Dark, ColorSchemeMode.MonetDark -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    val textPrimary = if (isDark) Color(0xFFEEEEEE) else Color(0xFF212121)
    val textSecondary = if (isDark) Color(0xFFAAAAAA) else Color(0xFF666666)
    val primaryColor: Color = MiuixTheme.colorScheme.primary
    val onPrimaryColor = if (primaryColor.luminance() > 0.5f) Color(0xFF1A1A1A) else Color.White
    val cardBg = if (isDark) Color(0xFF2A2A2A) else Color(0xFFF5F5F5)
    val dialogBg = if (isDark) Color(0xFF2A2A2A) else Color.White

    val preferences = remember { ThemePreferences(context.applicationContext) }
    val learnedMappings by preferences.learnedMappingsFlow.collectAsState(initial = emptyMap())

    val bills by viewModel.bills.collectAsState(initial = emptyList())

    val todayStr = remember {
        val cal = java.util.Calendar.getInstance()
        String.format(
            "%04d-%02d-%02d",
            cal.get(java.util.Calendar.YEAR),
            cal.get(java.util.Calendar.MONTH) + 1,
            cal.get(java.util.Calendar.DAY_OF_MONTH)
        )
    }
    val todayCount = remember(bills, isExpense, todayStr) {
        bills.count { it.date.startsWith(todayStr) && (it.amount < 0) == isExpense }
    }
    val todayAmount = remember(bills, isExpense, todayStr) {
        bills.filter { it.date.startsWith(todayStr) && (it.amount < 0) == isExpense }
            .sumOf { kotlin.math.abs(it.amount) }
    }

    val recentTemplates = remember(bills) {
        bills
            .sortedByDescending { it.date }
            .distinctBy { it.title }
            .take(3)
    }

    LaunchedEffect(currentStep) {
        onStepChange(currentStep)
    }

    val expenseCategories = remember {
        listOf(
            "餐饮" to Icons.Filled.Restaurant,
            "交通" to Icons.Filled.DirectionsCar,
            "购物" to Icons.Filled.ShoppingBag,
            "学习" to Icons.Filled.Book,
            "娱乐" to Icons.Filled.SportsEsports,
            "医疗" to Icons.Filled.LocalHospital,
            "住房" to Icons.Filled.Home,
            "其他" to Icons.Filled.Category
        )
    }
    val incomeCategories = remember {
        listOf(
            "工资" to Icons.Filled.AttachMoney,
            "奖金" to Icons.Filled.Star,
            "投资" to Icons.Filled.TrendingUp,
            "兼职" to Icons.Filled.Work,
            "红包" to Icons.Filled.Redeem,
            "其他" to Icons.Filled.Category
        )
    }

    val currentCats = remember(isExpense, customExpenseCats.size, customIncomeCats.size) {
        val list = mutableListOf<Pair<String, ImageVector>>()
        if (isExpense) {
            list.addAll(expenseCategories)
            list.addAll(customExpenseCats)
        } else {
            list.addAll(incomeCategories)
            list.addAll(customIncomeCats)
        }
        list.add("自定义" to Icons.Filled.Add)
        list
    }

    val availableCategories = remember(currentCats) {
        currentCats.map { it.first }.toSet() - "自定义"
    }

    val iconOptions = remember {
        listOf(
            Icons.Filled.Category, Icons.Filled.Restaurant, Icons.Filled.DirectionsCar,
            Icons.Filled.ShoppingBag, Icons.Filled.Book, Icons.Filled.SportsEsports,
            Icons.Filled.LocalHospital, Icons.Filled.Home, Icons.Filled.Pets,
            Icons.Filled.Star, Icons.Filled.Favorite, Icons.Filled.Work,
            Icons.Filled.School, Icons.Filled.Coffee, Icons.Filled.Flight,
            Icons.Filled.Hotel, Icons.Filled.Cake, Icons.Filled.FitnessCenter
        )
    }

    var categoryToDelete by remember { mutableStateOf<Pair<String, ImageVector>?>(null) }

    fun saveBill(finalCategory: String, learnFromNote: Boolean) {
        val amt = amount.toDoubleOrNull() ?: return
        if (amt <= 0) return

        viewModel.addBill(
            title = note.ifBlank { finalCategory },
            amount = amt,
            category = finalCategory,
            isExpense = isExpense,
            dateMillis = selectedDate,
            recurrenceType = recurrenceType
        )

        if (learnFromNote && note.isNotBlank()) {
            val keyword = note.trim()
            if (keyword.length <= 20) {
                scope.launch {
                    preferences.saveLearnedMapping(keyword, finalCategory)
                }
            }
        }

        amount = ""
        note = ""
        category = if (isExpense) "餐饮" else "工资"
        selectedDate = System.currentTimeMillis()
        recurrenceType = "none"
        recommendedCategory = null
        recommendedKeyword = null
        fromTemplate = false
        currentStep = 0
        onSaved()
    }

    fun applyTemplate(bill: BillEntity) {
        amount = formatAmountForInput(kotlin.math.abs(bill.amount))
        category = bill.category
        note = bill.title
        isExpense = bill.amount < 0
        recommendedCategory = null
        recommendedKeyword = null
        fromTemplate = true
        currentStep = 1
    }

    BackHandler(enabled = showManageCategories) { onManageCategoriesDismiss() }
    BackHandler(enabled = !showManageCategories && currentStep > 0) {
        currentStep = when (currentStep) {
            4 -> 3
            3 -> 2
            else -> currentStep - 1
        }
    }

    if (showManageCategories) {
        ManageCategoriesScreen(
            categories = if (isExpense) customExpenseCats else customIncomeCats,
            primaryColor = primaryColor, textPrimary = textPrimary,
            textSecondary = textSecondary, cardBg = cardBg,
            onDismiss = onManageCategoriesDismiss
        )
        return
    }

    AnimatedContent(
        targetState = currentStep,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = {
            if (targetState > initialState) {
                (slideInVertically(initialOffsetY = { it }, animationSpec = tween(MotionScheme.DurationLong, easing = MotionScheme.EasingEnter)) + fadeIn(tween(MotionScheme.DurationLong)))
                    .togetherWith(slideOutVertically(targetOffsetY = { -it / 5 }, animationSpec = tween(MotionScheme.DurationLong, easing = MotionScheme.EasingExit)) + fadeOut(tween(MotionScheme.DurationLong)))
            } else {
                (slideInVertically(initialOffsetY = { -it / 5 }, animationSpec = tween(MotionScheme.DurationLong, easing = MotionScheme.EasingEnter)) + fadeIn(tween(MotionScheme.DurationLong)))
                    .togetherWith(slideOutVertically(targetOffsetY = { it }, animationSpec = tween(MotionScheme.DurationLong, easing = MotionScheme.EasingExit)) + fadeOut(tween(MotionScheme.DurationLong)))
            }
        },
        label = "AddBillStep"
    ) { step ->
        when (step) {

            // ============ 0：类型选择 + 最近常用 ============
            0 -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "记一笔",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(bottom = 40.dp)
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    BigTypeButton("支出", Icons.Filled.ArrowUpward, Color(0xFFE53935), Modifier.weight(1f)) {
                        vibrate(context)
                        isExpense = true; category = "餐饮"
                        fromTemplate = false
                        currentStep = 1
                    }
                    BigTypeButton("收入", Icons.Filled.ArrowDownward, Color(0xFF43A047), Modifier.weight(1f)) {
                        vibrate(context)
                        isExpense = false; category = "工资"
                        fromTemplate = false
                        currentStep = 1
                    }
                }

                if (recentTemplates.isNotEmpty()) {
                    Spacer(Modifier.height(40.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "最近常用",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = textSecondary
                        )
                        Spacer(Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(textSecondary.copy(alpha = 0.15f))
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        recentTemplates.forEach { bill ->
                            TemplateChip(
                                bill = bill,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                primaryColor = primaryColor,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    vibrate(context)
                                    applyTemplate(bill)
                                }
                            )
                        }
                        repeat(3 - recentTemplates.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }

            // ============ 1：金额输入 ============
            1 -> Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    IconButton(onClick = {
                        vibrate(context)
                        fromTemplate = false
                        currentStep = 0
                    }) {
                        Icon(Icons.Filled.ArrowBack, "返回", tint = textPrimary)
                    }
                    Text(
                        if (isExpense) "记支出" else "记收入",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isExpense) "支出金额" else "收入金额",
                        fontSize = 13.sp,
                        color = textSecondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "¥",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (amount.isEmpty()) textSecondary.copy(alpha = 0.4f) else primaryColor,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = amount.ifEmpty { "0.00" },
                            style = TextStyle(
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold
                            ).tabular,
                            color = if (amount.isEmpty()) textSecondary.copy(alpha = 0.35f) else textPrimary
                        )
                    }

                    if (todayCount > 0) {
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(primaryColor.copy(alpha = 0.08f))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "今天已记 $todayCount 笔${if (isExpense) "支出" else "收入"}",
                                fontSize = 12.sp,
                                color = textSecondary
                            )
                            if (todayAmount > 0) {
                                Text(
                                    text = " · ",
                                    fontSize = 12.sp,
                                    color = textSecondary.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = "¥${formatAmount(todayAmount)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = primaryColor
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(primaryColor.copy(alpha = 0.10f))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                vibrate(context)
                                showCalculator = true
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Calculate,
                            contentDescription = "计算器",
                            tint = primaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "计算器",
                            fontSize = 13.sp,
                            color = primaryColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                NumberKeyboard(
                    onNumber = { digit -> amount = appendDigit(amount, digit) },
                    onDecimal = { amount = appendDecimal(amount) },
                    onBackspace = {
                        if (amount.isNotEmpty()) amount = amount.dropLast(1)
                    },
                    onClear = { amount = "" },
                    onDone = {
                        if (amount.toDoubleOrNull()?.let { it > 0 } == true) {
                            if (fromTemplate) {
                                saveBill(category, learnFromNote = false)
                            } else {
                                currentStep = 2
                            }
                        }
                    },
                    doneEnabled = amount.toDoubleOrNull()?.let { it > 0 } == true,
                    doneLabel = if (fromTemplate) "保存" else "下一步"
                )
            }

            // ============ 2：备注 + 日期 + 同步 ============
            2 -> Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { vibrate(context); currentStep = 1 }) {
                        Icon(Icons.Filled.ArrowBack, "返回", tint = textPrimary)
                    }
                    Text("添加备注（可选）", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                }
                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBg)
                        .clickable { vibrate(context); showDatePicker = true }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.DateRange, null, tint = primaryColor, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(formatDisplayDate(selectedDate), fontSize = 16.sp, color = textPrimary, modifier = Modifier.weight(1f))
                    Text("修改", fontSize = 13.sp, color = primaryColor)
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBg)
                        .clickable {
                            vibrate(context)
                            showRecurrencePicker = true
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Refresh,
                        null,
                        tint = if (recurrenceType == "none") textSecondary else primaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("同步", fontSize = 16.sp, color = textPrimary)
                        Text(
                            recurrenceTypeLabel(recurrenceType),
                            fontSize = 12.sp,
                            color = if (recurrenceType == "none") textSecondary else primaryColor
                        )
                    }
                    Text("修改", fontSize = 13.sp, color = primaryColor)
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { newNote ->
                        note = newNote
                        val rec = CategoryRecommender.recommendWithLearned(
                            note = newNote,
                            learned = learnedMappings,
                            isExpense = isExpense,
                            availableCategories = availableCategories
                        )
                        if (rec != null) {
                            recommendedKeyword = rec.first
                            recommendedCategory = rec.second
                        } else {
                            recommendedKeyword = null
                            recommendedCategory = null
                        }
                    },
                    placeholder = {
                        Text(
                            text = if (isExpense) "比如：吃饭、购物" else "比如：工资、红包",
                            color = textSecondary.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    textStyle = TextStyle(fontSize = 18.sp, color = textPrimary),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = primaryColor, unfocusedBorderColor = textSecondary.copy(alpha = 0.3f),
                        cursorColor = primaryColor
                    )
                )

                if (recommendedCategory != null) {
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(primaryColor.copy(alpha = 0.08f))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "识别为「${recommendedCategory}」",
                            fontSize = 13.sp,
                            color = textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "点击保存将直接归类",
                            fontSize = 11.sp,
                            color = textSecondary
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                val hasRecommendation = recommendedCategory != null
                Button(
                    onClick = {
                        vibrate(context)
                        if (hasRecommendation) {
                            saveBill(recommendedCategory!!, learnFromNote = false)
                        } else {
                            currentStep = 3
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = onPrimaryColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        if (hasRecommendation) "保存" else "选择分类",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let { utcMillis ->
                                    val localDate = java.time.Instant.ofEpochMilli(utcMillis)
                                        .atZone(java.time.ZoneOffset.UTC)
                                        .toLocalDate()
                                    selectedDate = localDate
                                        .atStartOfDay(java.time.ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli()
                                }
                                showDatePicker = false
                            }) { Text("确定", color = primaryColor) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) {
                                Text("取消", color = textSecondary)
                            }
                        },
                        colors = DatePickerDefaults.colors(
                            containerColor = dialogBg
                        )
                    ) {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = false,
                            colors = DatePickerDefaults.colors(
                                containerColor = dialogBg,
                                titleContentColor = textPrimary,
                                headlineContentColor = textPrimary,
                                weekdayContentColor = textSecondary,
                                subheadContentColor = textSecondary,
                                navigationContentColor = textPrimary,
                                yearContentColor = textPrimary,
                                currentYearContentColor = primaryColor,
                                selectedYearContentColor = onPrimaryColor,
                                selectedYearContainerColor = primaryColor,
                                dayContentColor = textPrimary,
                                selectedDayContentColor = onPrimaryColor,
                                selectedDayContainerColor = primaryColor,
                                todayContentColor = primaryColor,
                                todayDateBorderColor = primaryColor
                            )
                        )
                    }
                }
            }

            // ============ 3：分类选择 ============
            3 -> Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { vibrate(context); currentStep = 2 }) {
                        Icon(Icons.Filled.ArrowBack, "返回", tint = textPrimary)
                    }
                    Text("选择分类", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                }

                if (note.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "备注：$note",
                        fontSize = 13.sp,
                        color = textSecondary,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "选好后将记住这个分类",
                        fontSize = 11.sp,
                        color = primaryColor.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(currentCats.size) { index ->
                        val (name, icon) = currentCats[index]
                        val isCustomEntry = name == "自定义"
                        val isCustomCat = !isCustomEntry && !expenseCategories.any { it.first == name } && !incomeCategories.any { it.first == name }
                        CategoryItem(
                            name = name, icon = icon, isSelected = !isCustomEntry && category == name,
                            primaryColor = primaryColor, textPrimary = textPrimary, textSecondary = textSecondary, cardBg = cardBg,
                            onClick = {
                                vibrate(context)
                                if (isCustomEntry) {
                                    customInput = ""
                                    selectedCustomIcon = Icons.Filled.Category
                                    currentStep = 4
                                } else {
                                    category = name
                                    saveBill(name, learnFromNote = true)
                                }
                            },
                            onLongClick = if (isCustomCat) { { vibrate(context); categoryToDelete = name to icon } } else null
                        )
                    }
                }
            }

            // ============ 4：自定义分类 ============
            4 -> Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { vibrate(context); currentStep = 3 }) {
                        Icon(Icons.Filled.ArrowBack, "返回", tint = textPrimary)
                    }
                    Text("自定义分类", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                }
                Spacer(Modifier.height(24.dp))
                OutlinedTextField(
                    value = customInput, onValueChange = { if (it.length <= 10) customInput = it },
                    placeholder = { Text("输入分类名称（最多10字）", color = textSecondary.copy(alpha = 0.5f)) },
                    modifier = Modifier.fillMaxWidth(), singleLine = true,
                    textStyle = TextStyle(fontSize = 18.sp, color = textPrimary),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = primaryColor, unfocusedBorderColor = textSecondary.copy(alpha = 0.3f),
                        cursorColor = primaryColor
                    )
                )
                Spacer(Modifier.height(24.dp))
                Text("选择图标", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                Spacer(Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(iconOptions.size) { idx ->
                        val ic = iconOptions[idx]
                        val isSel = selectedCustomIcon == ic
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isSel) primaryColor else cardBg)
                                .clickable {
                                    vibrate(context)
                                    selectedCustomIcon = ic
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(ic, null, tint = if (isSel) onPrimaryColor else textSecondary, modifier = Modifier.size(24.dp))
                        }
                    }
                }
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = {
                        vibrate(context)
                        val name = customInput.trim()
                        if (name.isNotEmpty()) {
                            val entry = name to selectedCustomIcon
                            if (isExpense) {
                                if (!customExpenseCats.any { it.first == name }) customExpenseCats.add(entry)
                            } else {
                                if (!customIncomeCats.any { it.first == name }) customIncomeCats.add(entry)
                            }
                            category = name
                            saveBill(name, learnFromNote = true)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = customInput.trim().isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = onPrimaryColor,
                        disabledContainerColor = primaryColor.copy(alpha = 0.3f),
                        disabledContentColor = onPrimaryColor.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("保存", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }

    // 同步频率弹窗
    if (showRecurrencePicker) {
        AlertDialog(
            onDismissRequest = { showRecurrencePicker = false },
            title = { Text("同步频率", color = textPrimary) },
            text = {
                Column {
                    RecurrenceOption("不同步", recurrenceType == "none", textPrimary, primaryColor) {
                        vibrate(context); recurrenceType = "none"; showRecurrencePicker = false
                    }
                    RecurrenceOption("每天同步", recurrenceType == "daily", textPrimary, primaryColor) {
                        vibrate(context); recurrenceType = "daily"; showRecurrencePicker = false
                    }
                    RecurrenceOption("每周同步", recurrenceType == "weekly", textPrimary, primaryColor) {
                        vibrate(context); recurrenceType = "weekly"; showRecurrencePicker = false
                    }
                    RecurrenceOption("每月同步", recurrenceType == "monthly", textPrimary, primaryColor) {
                        vibrate(context); recurrenceType = "monthly"; showRecurrencePicker = false
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRecurrencePicker = false }) {
                    Text("取消", color = textSecondary)
                }
            },
            containerColor = dialogBg,
            titleContentColor = textPrimary,
            textContentColor = textPrimary
        )
    }

    // 删除分类弹窗
    if (categoryToDelete != null) {
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("删除分类", color = textPrimary) },
            text = { Text("确定删除分类「${categoryToDelete!!.first}」吗？", color = textPrimary) },
            confirmButton = {
                TextButton(onClick = {
                    vibrate(context)
                    val name = categoryToDelete!!.first
                    if (isExpense) customExpenseCats.removeAll { it.first == name }
                    else customIncomeCats.removeAll { it.first == name }
                    if (category == name) category = if (isExpense) "餐饮" else "工资"
                    categoryToDelete = null
                }) { Text("删除", color = Color(0xFFE53935)) }
            },
            dismissButton = {
                TextButton(onClick = { vibrate(context); categoryToDelete = null }) { Text("取消", color = textSecondary) }
            },
            containerColor = dialogBg,
            titleContentColor = textPrimary,
            textContentColor = textPrimary
        )
    }

    // 计算器 overlay
    CalculatorOverlay(
        visible = showCalculator,
        onDismiss = { showCalculator = false },
        onApply = { result ->
            val cents = (result * 100).toLong()
            amount = if (cents % 100 == 0L) {
                (cents / 100).toString()
            } else {
                "%.2f".format(cents / 100.0).trimEnd('0').trimEnd('.')
            }
            showCalculator = false
        }
    )
}

// ════════════════════════════════════════════════════════════
//  顶层函数
// ════════════════════════════════════════════════════════════

@Composable
private fun TemplateChip(
    bill: BillEntity,
    textPrimary: Color,
    textSecondary: Color,
    primaryColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isExpense = bill.amount < 0
    val accentColor = if (isExpense) Color(0xFFE53935) else Color(0xFF43A047)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(primaryColor.copy(alpha = 0.06f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                getCategoryIcon(bill.category),
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = bill.title.ifBlank { bill.category },
            fontSize = 11.sp,
            color = textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = (if (isExpense) "-" else "+") + "¥${formatAmount(kotlin.math.abs(bill.amount))}",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            maxLines = 1
        )
    }
}

private fun formatAmountForInput(value: Double): String {
    val abs = kotlin.math.abs(value)
    return if (abs == abs.toLong().toDouble()) {
        abs.toLong().toString()
    } else {
        "%.2f".format(abs).trimEnd('0').trimEnd('.')
    }
}

private fun appendDigit(current: String, digit: String): String {
    if (current.length >= 10) return current
    if (current.contains(".")) {
        val decimals = current.substringAfter(".")
        if (decimals.length >= 2) return current
    }
    return when {
        current == "0" -> digit
        current.isEmpty() -> digit
        else -> current + digit
    }
}

private fun appendDecimal(current: String): String {
    if (current.contains(".")) return current
    if (current.isEmpty()) return "0."
    return "$current."
}

@Composable
private fun RecurrenceOption(
    label: String,
    selected: Boolean,
    textPrimary: Color,
    primaryColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 16.sp,
            color = if (selected) primaryColor else textPrimary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
        if (selected) {
            Icon(
                Icons.Filled.Check,
                null,
                tint = primaryColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun recurrenceTypeLabel(type: String): String {
    return when (type) {
        "daily" -> "每天"
        "weekly" -> "每周"
        "monthly" -> "每月"
        else -> "不同步"
    }
}

@Composable
fun ManageCategoriesScreen(
    categories: MutableList<Pair<String, ImageVector>>,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val selected = remember { mutableStateListOf<Int>() }
    val onPrimaryColor = if (primaryColor.luminance() > 0.5f) Color(0xFF1A1A1A) else Color.White

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text("管理分类", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textPrimary)
        Spacer(Modifier.height(8.dp))
        Text("点击圆点选择要删除的分类", fontSize = 14.sp, color = textSecondary)
        Spacer(Modifier.height(24.dp))

        if (categories.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("还没有自定义分类", color = textSecondary)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(categories.size) { idx ->
                    val (name, icon) = categories[idx]
                    val isSel = selected.contains(idx)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSel) primaryColor.copy(alpha = 0.15f) else cardBg)
                            .border(if (isSel) 1.5.dp else 0.dp, if (isSel) primaryColor else Color.Transparent, RoundedCornerShape(16.dp))
                            .clickable { vibrate(context); if (isSel) selected.remove(idx) else selected.add(idx) }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(if (isSel) primaryColor else Color.Transparent)
                                .border(2.dp, if (isSel) primaryColor else textSecondary.copy(alpha = 0.5f), CircleShape)
                        )
                        Spacer(Modifier.width(16.dp))
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape).background(primaryColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(icon, null, tint = primaryColor, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(name, fontSize = 16.sp, color = textPrimary)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        if (categories.isNotEmpty()) {
            Button(
                onClick = {
                    vibrate(context)
                    selected.sortedDescending().forEach { if (it in categories.indices) categories.removeAt(it) }
                    selected.clear()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp), enabled = selected.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFE53935).copy(alpha = 0.3f),
                    disabledContentColor = Color.White.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) { Text("删除选中 (${selected.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(12.dp))
        }
        Button(
            onClick = { vibrate(context); onDismiss() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryColor, contentColor = onPrimaryColor),
            shape = RoundedCornerShape(16.dp)
        ) { Text("完成", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
    }
}

@Composable
fun CategoryItem(
    name: String,
    icon: ImageVector,
    isSelected: Boolean,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "catScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) primaryColor.copy(alpha = 0.15f) else cardBg)
            .border(1.5.dp, if (isSelected) primaryColor else Color.Transparent, RoundedCornerShape(16.dp))
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { isPressed = true; onClick() },
                onLongClick = onLongClick?.let { { isPressed = true; it() } }
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isSelected) primaryColor.copy(alpha = 0.25f) else textSecondary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = if (isSelected) primaryColor else textSecondary, modifier = Modifier.size(20.dp))
        }
        Text(
            name,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) primaryColor else textPrimary,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(Modifier.weight(1f))
        if (isSelected) Icon(Icons.Filled.Check, null, tint = primaryColor, modifier = Modifier.size(20.dp))
    }
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

@Composable
fun BigTypeButton(
    title: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "buttonScale"
    )
    Card(
        modifier = modifier
            .height(160.dp)
            .scale(scale)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                isPressed = true
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, title, tint = color, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(16.dp))
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(200)
            isPressed = false
        }
    }
}

fun formatDisplayDate(millis: Long): String {
    return try {
        val target = java.time.Instant.ofEpochMilli(millis)
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate()
        val today = java.time.LocalDate.now()
        val yesterday = today.minusDays(1)

        when (target) {
            today -> "今天"
            yesterday -> "昨天"
            else -> {
                if (target.year == today.year) {
                    "${target.monthValue}月${target.dayOfMonth}日"
                } else {
                    "${target.year}年${target.monthValue}月${target.dayOfMonth}日"
                }
            }
        }
    } catch (e: Exception) {
        "今天"
    }
}