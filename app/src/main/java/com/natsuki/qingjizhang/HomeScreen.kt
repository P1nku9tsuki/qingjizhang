package com.natsuki.qingjizhang

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun HomeScreen(
    initialAction: String? = null,
    onActionConsumed: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val selectedIndex = pagerState.currentPage

    var showThemePage by rememberSaveable { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showStatistics by remember { mutableStateOf(false) }
    var showOpenSource by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showCategoryColorSheet by remember { mutableStateOf(false) }
    var editingBill by remember { mutableStateOf<BillEntity?>(null) }
    var showEditPage by remember { mutableStateOf(false) }
    var detailBill by remember { mutableStateOf<BillEntity?>(null) }
    var showDetailSheet by remember { mutableStateOf(false) }
    var showBudgetPage by remember { mutableStateOf(false) }
    var budgetPreviousIndex by rememberSaveable { mutableIntStateOf(3) }
    var showSavingsGoalPage by remember { mutableStateOf(false) }
    var showAboutPage by remember { mutableStateOf(false) }
    var pendingUpdate by remember { mutableStateOf<UpdateInfo?>(null) }
    var checkingUpdate by remember { mutableStateOf(false) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showSearchSheet by remember { mutableStateOf(false) }
    var addBillStep by remember { mutableIntStateOf(0) }
    var showManageCategories by remember { mutableStateOf(false) }
    var selectionMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateListOf<Long>() }
    var showDeleteSelectedDialog by remember { mutableStateOf(false) }
    var billToDelete by remember { mutableStateOf<BillEntity?>(null) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var celebrationMilestone by remember { mutableStateOf<Int?>(null) }


    val hazeState = rememberHazeState()
    val themeViewModel: ThemeViewModel = viewModel()
    val billViewModel: BillViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val categoryColorPrefs = remember { ThemePreferences(context.applicationContext) }
    val categoryColorsMap by categoryColorPrefs.categoryColorsFlow.collectAsState(initial = emptyMap())
    LaunchedEffect(categoryColorsMap) {
        CategoryStore.categoryColors.clear()
        CategoryStore.categoryColors.putAll(categoryColorsMap)
    }

    val primaryColorArgb = MiuixTheme.colorScheme.primary.toArgb()
    val allBills by billViewModel.bills.collectAsState(initial = emptyList())
    val celebratedMilestones by categoryColorPrefs.celebratedStreakMilestonesFlow
        .collectAsState(initial = emptySet())
    val streakForCelebration = remember(allBills) { calculateStreakForCelebration(allBills) }

    LaunchedEffect(streakForCelebration, celebratedMilestones) {
        val milestones = listOf(7, 14, 30, 60, 100, 180, 365)
        val toCelebrate = milestones.lastOrNull {
            it <= streakForCelebration && it !in celebratedMilestones
        }
        if (toCelebrate != null) {
            delay(700)
            celebrationMilestone = toCelebrate
            categoryColorPrefs.markStreakMilestoneCelebrated(toCelebrate)
        }
    }

    LaunchedEffect(Unit) {
        billViewModel.checkAndGenerateRecurring()
    }

    //
    LaunchedEffect(Unit) {
        val prefs = ThemePreferences(context.applicationContext)
        val enabled = prefs.updateCheckEnabledFlow.first()
        val repo = prefs.githubRepoFlow.first()
        if (!enabled || repo.isBlank()) return@LaunchedEffect

        //
        val lastCheck = prefs.lastUpdateCheckFlow.first()
        val now = System.currentTimeMillis()
        if (now - lastCheck < 6 * 60 * 60 * 1000L) return@LaunchedEffect

        val currentVersion = UpdateChecker.getCurrentVersion(context)
        val info = UpdateChecker.checkForUpdate(currentVersion, repo)
        prefs.setLastUpdateCheck(now)

        if (info != null) {
            delay(1500)
            pendingUpdate = info
        }
    }

    //
    LaunchedEffect(selectedIndex) {
        if (selectionMode && selectedIndex != 0) {
            selectionMode = false
            selectedIds.clear()
        }
    }

    LaunchedEffect(initialAction) {
        when (initialAction) {
            "com.natsuki.qingjizhang.SHORTCUT_ADD_BILL" -> {
                showStatistics = false
                showThemePage = false
                showOpenSource = false
                showBudgetPage = false
                pagerState.scrollToPage(1)
                onActionConsumed()
            }
            "com.natsuki.qingjizhang.SHORTCUT_STATS" -> {
                pagerState.scrollToPage(2)
                onActionConsumed()
            }
            "com.natsuki.qingjizhang.SHORTCUT_SETTINGS" -> {
                pagerState.scrollToPage(3)
                onActionConsumed()
            }
            else -> {  }
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val bills = billViewModel.getAllBillsOnce()
                val success = BackupManager.exportToUri(context, uri, bills)
                Toast.makeText(
                    context,
                    if (success) "导出成功（${bills.size} 条）" else "导出失败",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val bills = BackupManager.importFromUri(context, uri)
            if (bills.isNotEmpty()) {
                billViewModel.importBills(bills)
                Toast.makeText(context, "导入成功（${bills.size} 条）", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "导入失败或文件为空", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val csvExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val bills = billViewModel.getAllBillsOnce()
                val success = BillShareHelper.exportCsv(context, uri, bills)
                Toast.makeText(
                    context,
                    if (success) "CSV 导出成功（${bills.size} 条）" else "CSV 导出失败",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val currentMode = themeViewModel.themeController.colorSchemeMode
    val isAppDark = when (currentMode) {
        ColorSchemeMode.Light, ColorSchemeMode.MonetLight -> false
        ColorSchemeMode.Dark, ColorSchemeMode.MonetDark -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    val shouldInterceptBack = selectionMode || showDetailSheet || showSearchSheet || showMonthPicker
            || showDeleteConfirmDialog || showDeleteSelectedDialog || showThemeDialog
            || showManageCategories || showEditPage || showBudgetPage || showStatistics
            || showOpenSource || showThemePage || showColorPicker || showCategoryColorSheet
            || showSavingsGoalPage || showAboutPage || pendingUpdate != null
            || selectedIndex != 0
    BackHandler(enabled = shouldInterceptBack) {
        when {
            selectionMode -> {
                selectionMode = false
                selectedIds.clear()
            }
            showDetailSheet -> showDetailSheet = false
            showSearchSheet -> showSearchSheet = false
            showMonthPicker -> showMonthPicker = false
            showDeleteConfirmDialog -> showDeleteConfirmDialog = false
            showDeleteSelectedDialog -> showDeleteSelectedDialog = false
            showThemeDialog -> showThemeDialog = false
            showColorPicker -> showColorPicker = false
            showCategoryColorSheet -> showCategoryColorSheet = false
            showManageCategories -> showManageCategories = false
            showEditPage -> showEditPage = false
            showBudgetPage -> {
                scope.launch { pagerState.scrollToPage(budgetPreviousIndex) }
                showBudgetPage = false
            }
            showSavingsGoalPage -> showSavingsGoalPage = false
            showAboutPage -> showAboutPage = false
            pendingUpdate != null -> pendingUpdate = null
            showStatistics -> showStatistics = false
            showOpenSource -> showOpenSource = false
            showThemePage -> showThemePage = false
            selectedIndex != 0 -> scope.launch { pagerState.animateScrollToPage(0) }
        }
    }

    val navItems = listOf(
        "主页" to Icons.Filled.Home,
        "记账" to Icons.Filled.Add,
        "统计" to Icons.Filled.BarChart,
        "设置" to Icons.Filled.Settings
    )

    val overlayState: String = when {
        showEditPage && editingBill != null -> "edit"
        showBudgetPage -> "budget"
        showSavingsGoalPage -> "savings"
        showAboutPage -> "about"
        showStatistics -> "statistics"
        showOpenSource -> "opensource"
        showThemePage -> "theme"
        else -> "none"
    }

    val currentTitle = if (selectionMode) {
        "已选 ${selectedIds.size} 项"
    } else when (overlayState) {
        "edit" -> "编辑账单"
        "budget" -> "月度预算"
        "savings" -> "储蓄目标"
        "about" -> "关于"
        "statistics" -> "统计分析"
        "opensource" -> "开源致谢"
        "theme" -> "主题设置"
        else -> when {
            showManageCategories -> "管理分类"
            else -> when (selectedIndex) {
                0 -> "轻记账"
                1 -> "记账"
                2 -> "统计"
                3 -> "设置"
                else -> "轻记账"
            }
        }
    }

    val anyOverlayOpen = overlayState != "none" || showManageCategories
            || showMonthPicker || showThemeDialog || showSearchSheet || showDetailSheet
            || showDeleteConfirmDialog || showDeleteSelectedDialog || showColorPicker
            || showCategoryColorSheet || pendingUpdate != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            LiquidTopBar(
                title = currentTitle,
                isAppDark = isAppDark,
                actions = {
                    when {
                        selectionMode -> {
                            TextButton(onClick = {
                                vibrate(context)
                                selectionMode = false
                                selectedIds.clear()
                            }) {
                                Text(
                                    "取消",
                                    color = if (isAppDark) Color.White else Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        anyOverlayOpen -> {}
                        selectedIndex == 1 && addBillStep == 3 -> {
                            androidx.compose.material3.IconButton(
                                onClick = {
                                    vibrate(context)
                                    showManageCategories = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "管理分类",
                                    tint = if (isAppDark) Color.White else Color.Black
                                )
                            }
                        }
                        selectedIndex != 1 -> {
                            if (selectedIndex == 0) {
                                androidx.compose.material3.IconButton(
                                    onClick = {
                                        vibrate(context)
                                        showSearchSheet = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = "搜索",
                                        tint = if (isAppDark) Color.White else Color.Black
                                    )
                                }
                            }
                            androidx.compose.material3.IconButton(
                                onClick = {
                                    vibrate(context)
                                    scope.launch { pagerState.animateScrollToPage(1) }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "记一笔",
                                    tint = if (isAppDark) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .hazeSource(hazeState)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    beyondViewportPageCount = 1,
                    userScrollEnabled = !anyOverlayOpen && !selectionMode
                ) { page ->
                    when (page) {
                        0 -> HomeContent(
                            onShowDetail = { bill ->
                                detailBill = bill
                                showDetailSheet = true
                            },
                            hazeState = hazeState,
                            onBalanceClick = { showStatistics = true },
                            onShowMonthPicker = { showMonthPicker = true },
                            onAddBill = { scope.launch { pagerState.animateScrollToPage(1) } },
                            selectionMode = selectionMode,
                            selectedIds = selectedIds.toSet(),
                            onEnterSelectionMode = { bill ->
                                selectionMode = true
                                selectedIds.clear()
                                selectedIds.add(bill.id)
                            },
                            onToggleSelection = { bill ->
                                if (selectedIds.contains(bill.id)) {
                                    selectedIds.remove(bill.id)
                                    if (selectedIds.isEmpty()) selectionMode = false
                                } else {
                                    selectedIds.add(bill.id)
                                }
                            }
                        )
                        1 -> AddBillScreen(
                            viewModel = billViewModel,
                            onSaved = { scope.launch { pagerState.animateScrollToPage(0) } },
                            onStepChange = { addBillStep = it },
                            showManageCategories = showManageCategories,
                            onManageCategoriesDismiss = { showManageCategories = false }
                        )
                        2 -> StatisticsScreen(
                            viewModel = billViewModel,
                            primaryColor = MiuixTheme.colorScheme.primary,
                            textPrimary = if (isAppDark) Color(0xFFEEEEEE) else Color(0xFF212121),
                            textSecondary = if (isAppDark) Color(0xFFAAAAAA) else Color(0xFF666666),
                            cardBg = if (isAppDark) Color(0xFF2A2A2A) else Color(0xFFF5F5F5),
                            onBack = { scope.launch { pagerState.animateScrollToPage(0) } },
                            onShareImage = { isYearMode ->
                                scope.launch {
                                    shareCurrentBills(context, billViewModel, primaryColorArgb, isYearMode)
                                }
                            },
                            isActive = selectedIndex == 2
                        )
                        3 -> SettingsScreen(
                            isThemePage = false,
                            onBack = { showThemePage = true },
                            onExport = {
                                exportLauncher.launch("qingjizhang_${System.currentTimeMillis()}.json")
                            },
                            onImport = {
                                importLauncher.launch(arrayOf("application/json"))
                            },
                            onOpenSource = { showOpenSource = true },
                            onBudgetClick = {
                                budgetPreviousIndex = selectedIndex
                                showBudgetPage = true
                            },
                            onSavingsGoalClick = { showSavingsGoalPage = true },
                            onAboutClick = { showAboutPage = true },
                            onExportCsv = {
                                csvExportLauncher.launch("qingjizhang_${System.currentTimeMillis()}.csv")
                            }
                        )
                    }
                }

                AnimatedContent(
                    targetState = overlayState,
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {
                        if (targetState != "none" && initialState == "none") {
                            slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(MotionScheme.DurationLong, easing = MotionScheme.EasingEnter)
                            ) togetherWith ExitTransition.None
                        } else if (targetState == "none" && initialState != "none") {
                            EnterTransition.None togetherWith slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingExit)
                            )
                        } else if (targetState != "none" && initialState != "none") {
                            slideInVertically(
                                initialOffsetY = { it },
                                animationSpec = tween(MotionScheme.DurationLong, easing = MotionScheme.EasingEnter)
                            ) togetherWith slideOutVertically(
                                targetOffsetY = { -it },
                                animationSpec = tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingExit)
                            )
                        } else {
                            EnterTransition.None togetherWith ExitTransition.None
                        }
                    },
                    label = "OverlayTransition"
                ) { state ->
                    if (state == "none") {
                        Box(modifier = Modifier.fillMaxSize())
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MiuixTheme.colorScheme.surface)
                        ) {
                            when (state) {
                                "edit" -> editingBill?.let { bill ->
                                    EditBillScreen(
                                        bill = bill,
                                        viewModel = billViewModel,
                                        onBack = { showEditPage = false },
                                        onSaved = { showEditPage = false }
                                    )
                                }
                                "budget" -> BudgetSettingsScreen(
                                    onBack = {
                                        scope.launch { pagerState.scrollToPage(budgetPreviousIndex) }
                                        showBudgetPage = false
                                    }
                                )
                                "savings" -> SavingsGoalSettingsScreen(
                                    onBack = { showSavingsGoalPage = false }
                                )
                                "about" -> AboutScreen(
                                    primaryColor = MiuixTheme.colorScheme.primary,
                                    textPrimary = if (isAppDark) Color(0xFFEEEEEE) else Color(0xFF212121),
                                    textSecondary = if (isAppDark) Color(0xFFAAAAAA) else Color(0xFF666666),
                                    cardBg = if (isAppDark) Color(0xFF2A2A2A) else Color(0xFFF5F5F5),
                                    onBack = { showAboutPage = false },
                                    checking = checkingUpdate,
                                    onCheckUpdate = {
                                        val prefs = ThemePreferences(context.applicationContext)
                                        scope.launch {
                                            checkingUpdate = true
                                            val repo = prefs.githubRepoFlow.first()
                                            val currentVersion = UpdateChecker.getCurrentVersion(context)
                                            val info = UpdateChecker.checkForUpdate(currentVersion, repo)
                                            prefs.setLastUpdateCheck(System.currentTimeMillis())
                                            checkingUpdate = false
                                            if (info != null) {
                                                pendingUpdate = info
                                            } else {
                                                Toast.makeText(context, "已是最新版本", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                )
                                "statistics" -> StatisticsScreen(
                                    viewModel = billViewModel,
                                    primaryColor = MiuixTheme.colorScheme.primary,
                                    textPrimary = if (isAppDark) Color(0xFFEEEEEE) else Color(0xFF212121),
                                    textSecondary = if (isAppDark) Color(0xFFAAAAAA) else Color(0xFF666666),
                                    cardBg = if (isAppDark) Color(0xFF2A2A2A) else Color(0xFFF5F5F5),
                                    onBack = { showStatistics = false },
                                    onShareImage = { isYearMode ->
                                        scope.launch {
                                            shareCurrentBills(context, billViewModel, primaryColorArgb, isYearMode)
                                        }
                                    }
                                )
                                "opensource" -> OpenSourceScreen(
                                    primaryColor = MiuixTheme.colorScheme.primary,
                                    textPrimary = if (isAppDark) Color(0xFFEEEEEE) else Color(0xFF212121),
                                    textSecondary = if (isAppDark) Color(0xFFAAAAAA) else Color(0xFF666666),
                                    cardBg = if (isAppDark) Color(0xFF2A2A2A) else Color(0xFFF5F5F5),
                                    onBack = { showOpenSource = false }
                                )
                                "theme" -> SettingsScreen(
                                    isThemePage = true,
                                    onBack = { showThemePage = false },
                                    onThemeClick = { showThemeDialog = true },
                                    onColorClick = { showColorPicker = true },
                                    onCategoryColorClick = { showCategoryColorSheet = true }
                                )
                            }
                        }
                    }
                }
            }
        }

        val shouldShowBottomBar =
            !anyOverlayOpen
                    && !selectionMode
                    && !(selectedIndex == 1 && addBillStep > 0)

        AnimatedVisibility(
            visible = shouldShowBottomBar,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingEnter)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationShort, easing = MotionScheme.EasingExit)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            LiquidBottomBar(
                navItems = navItems,
                selectedIndex = selectedIndex,
                onItemClick = { index ->
                    vibrate(context)
                    scope.launch { pagerState.animateScrollToPage(index) }
                },
                isDark = isAppDark,
                hazeState = hazeState,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
            )
        }

        AnimatedVisibility(
            visible = selectionMode,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingEnter)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationShort, easing = MotionScheme.EasingExit)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            SelectionBottomBar(
                selectedCount = selectedIds.size,
                totalCount = allBills.size,
                isAllSelected = allBills.isNotEmpty() && selectedIds.size == allBills.size,
                onToggleSelectAll = {
                    vibrate(context)
                    if (selectedIds.size == allBills.size) {
                        selectedIds.clear()
                        selectionMode = false
                    } else {
                        selectedIds.clear()
                        selectedIds.addAll(allBills.map { it.id })
                    }
                },
                onDelete = {
                    vibrate(context)
                    if (selectedIds.isNotEmpty()) showDeleteSelectedDialog = true
                }
            )
        }

        if (showDeleteConfirmDialog || showDeleteSelectedDialog || showThemeDialog
            || showMonthPicker || showSearchSheet || showDetailSheet || showColorPicker
            || showCategoryColorSheet) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showDeleteConfirmDialog = false
                        showDeleteSelectedDialog = false
                        showThemeDialog = false
                        showMonthPicker = false
                        showSearchSheet = false
                        showDetailSheet = false
                        showColorPicker = false
                        showCategoryColorSheet = false
                    }
            )
        }

        if (showDeleteConfirmDialog) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.width(300.dp).clip(RoundedCornerShape(24.dp)),
                    color = MiuixTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("确认删除这笔账单吗？", style = MaterialTheme.typography.titleMedium, color = MiuixTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 20.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(
                                onClick = {
                                    vibrate(context)
                                    showDeleteConfirmDialog = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MiuixTheme.colorScheme.surfaceVariant)
                            ) {
                                Text("取消", color = MiuixTheme.colorScheme.onSurface)
                            }
                            val deleteBtnBg = if (isAppDark) Color(0xFFE0E0E0) else Color(0xFFB71C1C)
                            val deleteBtnText = if (isAppDark) Color(0xFFB71C1C) else Color.White
                            Button(
                                onClick = {
                                    vibrate(context)
                                    billToDelete?.let { billViewModel.deleteBill(it) }
                                    showDeleteConfirmDialog = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = deleteBtnBg)
                            ) {
                                Text("删除", color = deleteBtnText)
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteSelectedDialog) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.width(300.dp).clip(RoundedCornerShape(24.dp)),
                    color = MiuixTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "删除选中的 ${selectedIds.size} 条账单？",
                            style = MaterialTheme.typography.titleMedium,
                            color = MiuixTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            "此操作不可撤销",
                            style = MaterialTheme.typography.bodySmall,
                            color = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Button(
                                onClick = {
                                    vibrate(context)
                                    showDeleteSelectedDialog = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MiuixTheme.colorScheme.surfaceVariant)
                            ) {
                                Text("取消", color = MiuixTheme.colorScheme.onSurface)
                            }
                            val deleteBtnBg = if (isAppDark) Color(0xFFE0E0E0) else Color(0xFFB71C1C)
                            val deleteBtnText = if (isAppDark) Color(0xFFB71C1C) else Color.White
                            Button(
                                onClick = {
                                    vibrate(context)
                                    val toDelete = allBills.filter { it.id in selectedIds }
                                    toDelete.forEach { billViewModel.deleteBill(it) }
                                    selectionMode = false
                                    selectedIds.clear()
                                    showDeleteSelectedDialog = false
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = deleteBtnBg)
                            ) {
                                Text("删除", color = deleteBtnText)
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showThemeDialog,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingEnter)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationShort, easing = MotionScheme.EasingExit)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
                color = MiuixTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = "选择主题模式",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 20.dp, bottom = 16.dp),
                        color = MiuixTheme.colorScheme.onSurface
                    )
                    ThemeOption(
                        title = "跟随系统",
                        isSelected = themeViewModel.themeController.colorSchemeMode == ColorSchemeMode.MonetSystem,
                        onClick = {
                            showThemeDialog = false
                            scope.launch {
                                delay(MotionScheme.DurationShort.toLong())
                                themeViewModel.setThemeMode(ColorSchemeMode.MonetSystem)
                            }
                        }
                    )
                    ThemeOption(
                        title = "浅色",
                        isSelected = themeViewModel.themeController.colorSchemeMode == ColorSchemeMode.MonetLight,
                        onClick = {
                            showThemeDialog = false
                            scope.launch {
                                delay(MotionScheme.DurationShort.toLong())
                                themeViewModel.setThemeMode(ColorSchemeMode.MonetLight)
                            }
                        }
                    )
                    ThemeOption(
                        title = "深色",
                        isSelected = themeViewModel.themeController.colorSchemeMode == ColorSchemeMode.MonetDark,
                        onClick = {
                            showThemeDialog = false
                            scope.launch {
                                delay(MotionScheme.DurationShort.toLong())
                                themeViewModel.setThemeMode(ColorSchemeMode.MonetDark)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        AnimatedVisibility(
            visible = showColorPicker,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingEnter)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationShort, easing = MotionScheme.EasingExit)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            ColorPickerSheet(
                currentArgb = themeViewModel.customKeyColorArgb,
                isDark = isAppDark,
                onSelect = { argb ->
                    themeViewModel.setCustomKeyColor(argb)
                    showColorPicker = false
                },
                onDismiss = { showColorPicker = false }
            )
        }

        AnimatedVisibility(
            visible = showCategoryColorSheet,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingEnter)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationShort, easing = MotionScheme.EasingExit)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            CategoryColorSheet(
                isDark = isAppDark,
                onDismiss = { showCategoryColorSheet = false }
            )
        }

        AnimatedVisibility(
            visible = showMonthPicker,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingEnter)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationShort, easing = MotionScheme.EasingExit)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            MonthYearPickerSheet(
                initialYear = billViewModel.selectedYear.collectAsState().value,
                initialMonth = billViewModel.selectedMonth.collectAsState().value,
                primaryColor = MiuixTheme.colorScheme.primary,
                textPrimary = if (isAppDark) Color(0xFFEEEEEE) else Color(0xFF212121),
                textSecondary = if (isAppDark) Color(0xFFAAAAAA) else Color(0xFF666666),
                surfaceColor = MiuixTheme.colorScheme.surface,
                onDismiss = { showMonthPicker = false },
                onConfirm = { year, month ->
                    billViewModel.jumpTo(year, month)
                    showMonthPicker = false
                }
            )
        }

        AnimatedVisibility(
            visible = showSearchSheet,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingEnter)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationShort, easing = MotionScheme.EasingExit)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            SearchSheet(
                onDismiss = { showSearchSheet = false },
                onEditBill = { bill ->
                    editingBill = bill
                    showEditPage = true
                },
                viewModel = billViewModel
            )
        }

        AnimatedVisibility(
            visible = showDetailSheet && detailBill != null,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingEnter)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(MotionScheme.DurationShort, easing = MotionScheme.EasingExit)
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            detailBill?.let { bill ->
                BillDetailSheet(
                    bill = bill,
                    onClose = { showDetailSheet = false },
                    onEdit = {
                        showDetailSheet = false
                        editingBill = bill
                        showEditPage = true
                    },
                    onDelete = {
                        showDetailSheet = false
                        billToDelete = bill
                        showDeleteConfirmDialog = true
                    }
                )
            }
        }

        //
        pendingUpdate?.let { info ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { pendingUpdate = null },
                contentAlignment = Alignment.Center
            ) {
                UpdateDialog(
                    currentVersion = UpdateChecker.getCurrentVersion(context),
                    updateInfo = info,
                    onGoToDownload = {
                        UpdateChecker.openReleasePage(context, info.htmlUrl)
                        pendingUpdate = null
                    },
                    onDismiss = { pendingUpdate = null }
                )
            }
        }

        //
        celebrationMilestone?.let { milestone ->
            StreakCelebrationOverlay(
                milestone = milestone,
                onDismiss = { celebrationMilestone = null }
            )
        }
    }
}

@Composable
private fun SelectionBottomBar(
    selectedCount: Int,
    totalCount: Int,
    isAllSelected: Boolean,
    onToggleSelectAll: () -> Unit,
    onDelete: () -> Unit
) {
    val surfaceColor = MiuixTheme.colorScheme.surface
    val onSurface = MiuixTheme.colorScheme.onSurface
    val primary = MiuixTheme.colorScheme.primary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(surfaceColor)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onToggleSelectAll) {
            Text(
                if (isAllSelected) "取消全选" else "全选",
                color = primary,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(Modifier.weight(1f))

        Text(
            "$selectedCount / $totalCount",
            fontSize = 13.sp,
            color = onSurface.copy(alpha = 0.6f)
        )

        Spacer(Modifier.width(12.dp))

        Button(
            onClick = onDelete,
            enabled = selectedCount > 0,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE53935),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFE53935).copy(alpha = 0.3f),
                disabledContentColor = Color.White.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("删除", fontWeight = FontWeight.Medium)
        }
    }
}

private suspend fun shareCurrentBills(
    context: Context,
    billViewModel: BillViewModel,
    primaryColor: Int,
    isYearMode: Boolean
) {
    val all = billViewModel.getAllBillsOnce()
    val y = billViewModel.selectedYear.value
    val m = billViewModel.selectedMonth.value

    val bills = if (isYearMode) {
        all.filter { bill ->
            val parts = bill.date.split(" ", "-", ":")
            parts.size >= 3 && parts[0].toIntOrNull() == y
        }
    } else {
        all.filter { bill ->
            val parts = bill.date.split(" ", "-", ":")
            parts.size >= 3 &&
                    parts[0].toIntOrNull() == y &&
                    parts[1].toIntOrNull() == m
        }
    }

    val title = if (isYearMode) "${y}年" else "${y}年${m}月"
    val emptyHint = if (isYearMode) "这一年还没有账单" else "本月还没有账单"

    if (bills.isEmpty()) {
        Toast.makeText(context, emptyHint, Toast.LENGTH_SHORT).show()
    } else {
        val income = bills.filter { it.amount > 0 }.sumOf { it.amount }
        val expense = bills.filter { it.amount < 0 }.sumOf { -it.amount }
        val balance = income - expense
        val file = BillShareHelper.generateShareImage(
            context = context,
            month = title,
            balance = balance,
            income = income,
            expense = expense,
            bills = bills,
            primaryColor = primaryColor
        )
        BillShareHelper.shareImage(context, file)
    }
}

fun calculateStreakForCelebration(bills: List<BillEntity>): Int {
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
fun ThemeOption(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dotColor by animateColorAsState(
        targetValue = if (isSelected) MiuixTheme.colorScheme.primary
        else MiuixTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        label = "dotColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MiuixTheme.colorScheme.primary
        else MiuixTheme.colorScheme.onSurface,
        label = "textColor"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) MiuixTheme.colorScheme.primary.copy(alpha = 0.08f)
        else Color.Transparent,
        label = "bgColor"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            color = textColor,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}