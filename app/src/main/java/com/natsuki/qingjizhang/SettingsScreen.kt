package com.natsuki.qingjizhang

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.theme.ColorSchemeMode

@Composable
fun SettingsScreen(
    isThemePage: Boolean = false,
    onBack: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onColorClick: () -> Unit = {},
    onCategoryColorClick: () -> Unit = {},
    onSavingsGoalClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onExport: () -> Unit = {},
    onImport: () -> Unit = {},
    onOpenSource: () -> Unit = {},
    onBudgetClick: () -> Unit = {},
    onExportCsv: () -> Unit = {},
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel = viewModel(),
    budgetViewModel: BudgetViewModel = viewModel()
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())   // 👈 可滚动
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        if (isThemePage) {
            Card {
                val currentModeText = when (themeViewModel.themeController.colorSchemeMode) {
                    ColorSchemeMode.MonetLight -> "浅色"
                    ColorSchemeMode.MonetDark -> "深色"
                    else -> "跟随系统"
                }
                BasicComponent(
                    title = "主题模式",
                    summary = currentModeText,
                    onClick = onThemeClick
                )
                BasicComponent(
                    title = "主题色",
                    summary = if (themeViewModel.customKeyColorArgb == -1) "系统动态色" else "自定义",
                    onClick = onColorClick
                )
                BasicComponent(
                    title = "分类颜色",
                    summary = "自定义每个分类的颜色",
                    onClick = onCategoryColorClick
                )
            }
        } else {
            Card {
                BasicComponent(
                    title = "主题设置",
                    summary = "选择应用的颜色主题",
                    onClick = { onBack() }
                )
            }

            Column(modifier = Modifier.padding(top = 16.dp)) {
                Card {
                    val budgetText = if (budgetViewModel.budget.value <= 0) {
                        "未设置"
                    } else {
                        "¥${budgetViewModel.budget.value.toInt()}"
                    }
                    BasicComponent(
                        title = "月度预算",
                        summary = budgetText,
                        onClick = onBudgetClick
                    )
                }
            }

            Column(modifier = Modifier.padding(top = 16.dp)) {
                Card {
                    val goal = budgetViewModel.savingsGoal.value
                    val goalText = if (goal == null) {
                        "未设置"
                    } else {
                        "${goal.name} · ¥${goal.amount.toInt()}"
                    }
                    BasicComponent(
                        title = "储蓄目标",
                        summary = goalText,
                        onClick = onSavingsGoalClick
                    )
                }
            }

            Column(modifier = Modifier.padding(top = 16.dp)) {
                Card {
                    BasicComponent(
                        title = "导出 CSV",
                        summary = "导出为 Excel 可读的 CSV 文件",
                        onClick = onExportCsv
                    )
                }
            }

            Column(modifier = Modifier.padding(top = 16.dp)) {
                Card {
                    BasicComponent(
                        title = "导出 JSON",
                        summary = "备份全部账单（.json 文件）",
                        onClick = onExport
                    )
                    BasicComponent(
                        title = "导入 JSON",
                        summary = "从 JSON 文件恢复账单数据",
                        onClick = onImport
                    )
                }
            }

            Column(modifier = Modifier.padding(top = 16.dp)) {
                Card {
                    BasicComponent(
                        title = "开源致谢",
                        summary = "感谢这些优秀的开源项目",
                        onClick = onOpenSource

                    )
                }
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Card {
                        BasicComponent(
                            title = "关于",
                            summary = "版本信息、检查更新、项目主页",
                            onClick = onAboutClick
                        )
                    }
                }
            }

            Spacer(Modifier.height(120.dp))
        }
    }
}
