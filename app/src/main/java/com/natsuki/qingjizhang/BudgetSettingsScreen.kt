package com.natsuki.qingjizhang

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun BudgetSettingsScreen(
    onBack: () -> Unit
) {
    val budgetViewModel: BudgetViewModel = viewModel()
    val themeViewModel: ThemeViewModel = viewModel()
    val context = LocalContext.current

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

    var amount by remember {
        mutableStateOf(
            if (budgetViewModel.budget.value <= 0) ""
            else budgetViewModel.budget.value.toInt().toString()
        )
    }

    val categoryBudgets by budgetViewModel.categoryBudgets.collectAsState()
    var editingCategory by remember { mutableStateOf<String?>(null) }

    val builtIn = listOf("餐饮", "交通", "购物", "学习", "娱乐", "医疗", "住房", "其他")
    val expenseCategories = remember(CategoryStore.customExpenseCats.size) {
        builtIn + CategoryStore.customExpenseCats.map { it.first }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "返回", tint = textPrimary)
            }
            Text("月度预算", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
        }

        Spacer(Modifier.height(32.dp))

        Text(
            "设置每月的消费上限，超过会提醒你",
            fontSize = 14.sp,
            color = textSecondary,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
            placeholder = { Text("例如 3000", fontSize = 28.sp, color = textSecondary.copy(alpha = 0.5f)) },
            leadingIcon = {
                Icon(Icons.Filled.AttachMoney, null, tint = primaryColor, modifier = Modifier.size(28.dp))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textPrimary),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = textSecondary.copy(alpha = 0.3f),
                cursorColor = primaryColor
            )
        )

        Spacer(Modifier.height(8.dp))
        Text(
            "设为 0 或留空表示不设置总预算",
            fontSize = 12.sp,
            color = textSecondary
        )

        // 分类预算
        Spacer(Modifier.height(28.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "分类预算",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
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

        Text(
            "给单个分类设置上限，主页会显示进度",
            fontSize = 12.sp,
            color = textSecondary
        )

        Spacer(Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(cardBg)
        ) {
            expenseCategories.forEachIndexed { index, cat ->
                val budget = categoryBudgets[cat]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            vibrate(context)
                            editingCategory = cat
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CategoryStore.getColor(cat).copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            getCategoryIcon(cat),
                            contentDescription = null,
                            tint = CategoryStore.getColor(cat),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(
                        cat,
                        fontSize = 15.sp,
                        color = textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (budget != null && budget > 0) "¥${formatAmount(budget)}" else "未设置",
                        fontSize = 14.sp,
                        fontWeight = if (budget != null && budget > 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (budget != null && budget > 0) primaryColor else textSecondary.copy(alpha = 0.6f)
                    )
                }
                if (index < expenseCategories.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(textSecondary.copy(alpha = 0.08f))
                            .padding(start = 64.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    budgetViewModel.setBudget(0.0)
                    onBack()
                },
                modifier = Modifier.weight(1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color(0xFF3A3A3A) else Color(0xFFEEEEEE),
                    contentColor = textPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("清除", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    val value = amount.toDoubleOrNull() ?: 0.0
                    budgetViewModel.setBudget(value)
                    onBack()
                },
                modifier = Modifier.weight(2f).height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = onPrimaryColor
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("保存", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    // 分类预算编辑对话框
    editingCategory?.let { cat ->
        CategoryBudgetEditDialog(
            category = cat,
            currentBudget = categoryBudgets[cat] ?: 0.0,
            primaryColor = primaryColor,
            onPrimaryColor = onPrimaryColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            isDark = isDark,
            dialogBg = dialogBg,
            onConfirm = { value ->
                budgetViewModel.setCategoryBudget(cat, value)
                editingCategory = null
            },
            onClear = {
                budgetViewModel.resetCategoryBudget(cat)
                editingCategory = null
            },
            onDismiss = { editingCategory = null }
        )
    }
}

@Composable
private fun CategoryBudgetEditDialog(
    category: String,
    currentBudget: Double,
    primaryColor: Color,
    onPrimaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    isDark: Boolean,
    dialogBg: Color,
    onConfirm: (Double) -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var text by remember {
        mutableStateOf(
            if (currentBudget <= 0) ""
            else currentBudget.toInt().toString()
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(CategoryStore.getColor(category).copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        getCategoryIcon(category),
                        contentDescription = null,
                        tint = CategoryStore.getColor(category),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text("$category 预算", color = textPrimary)
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) text = it },
                    placeholder = { Text("例如 800", color = textSecondary.copy(alpha = 0.5f)) },
                    leadingIcon = {
                        Icon(Icons.Filled.AttachMoney, null, tint = primaryColor, modifier = Modifier.size(20.dp))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textPrimary),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = textSecondary.copy(alpha = 0.3f),
                        cursorColor = primaryColor
                    )
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "留空或设 0 = 不设置此分类预算",
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                vibrate(context)
                val v = text.toDoubleOrNull() ?: 0.0
                onConfirm(v)
            }) {
                Text("确定", color = primaryColor, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row {
                if (currentBudget > 0) {
                    TextButton(onClick = {
                        vibrate(context)
                        onClear()
                    }) {
                        Text("清除", color = Color(0xFFE53935))
                    }
                }
                TextButton(onClick = {
                    vibrate(context)
                    onDismiss()
                }) {
                    Text("取消", color = textSecondary)
                }
            }
        },
        containerColor = dialogBg,
        titleContentColor = textPrimary,
        textContentColor = textPrimary
    )
}