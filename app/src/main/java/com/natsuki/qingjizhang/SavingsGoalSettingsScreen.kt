package com.natsuki.qingjizhang

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun SavingsGoalSettingsScreen(
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

    val currentGoal by budgetViewModel.savingsGoal.collectAsState()

    var name by remember {
        mutableStateOf(currentGoal?.name ?: "")
    }
    var amount by remember {
        mutableStateOf(
            currentGoal?.let { if (it.amount > 0) it.amount.toInt().toString() else "" } ?: ""
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "返回", tint = textPrimary)
            }
            Text("储蓄目标", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
        }

        Spacer(Modifier.height(32.dp))

        Text(
            "设定一个攒钱目标，主页会显示进度",
            fontSize = 14.sp,
            color = textSecondary,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(24.dp))

        // 目标名称
        OutlinedTextField(
            value = name,
            onValueChange = { if (it.length <= 20) name = it },
            placeholder = { Text("目标名称，例如：买 iPad", color = textSecondary.copy(alpha = 0.5f)) },
            leadingIcon = {
                Icon(Icons.Filled.Edit, null, tint = primaryColor, modifier = Modifier.size(20.dp))
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = TextStyle(fontSize = 18.sp, color = textPrimary),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = textSecondary.copy(alpha = 0.3f),
                cursorColor = primaryColor
            )
        )

        Spacer(Modifier.height(16.dp))

        // 目标金额
        OutlinedTextField(
            value = amount,
            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
            placeholder = { Text("例如 8000", fontSize = 24.sp, color = textSecondary.copy(alpha = 0.5f)) },
            leadingIcon = {
                Icon(Icons.Filled.AttachMoney, null, tint = primaryColor, modifier = Modifier.size(26.dp))
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textPrimary),
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
            "已攒金额 = 从设定目标起的累计结余",
            fontSize = 12.sp,
            color = textSecondary
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (currentGoal != null) {
                Button(
                    onClick = {
                        vibrate(context)
                        budgetViewModel.clearSavingsGoal()
                        onBack()
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF3A3A3A) else Color(0xFFEEEEEE),
                        contentColor = textPrimary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("清除", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = {
                    vibrate(context)
                    val value = amount.toDoubleOrNull() ?: 0.0
                    if (value > 0) {
                        budgetViewModel.setSavingsGoal(name.ifBlank { "储蓄目标" }, value)
                    } else {
                        budgetViewModel.clearSavingsGoal()
                    }
                    onBack()
                },
                modifier = Modifier.weight(if (currentGoal != null) 2f else 1f).height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = onPrimaryColor
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("保存", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

