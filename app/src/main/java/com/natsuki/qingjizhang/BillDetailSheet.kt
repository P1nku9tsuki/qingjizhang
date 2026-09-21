package com.natsuki.qingjizhang

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun BillDetailSheet(
    bill: BillEntity,
    onClose: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    //
    val themeViewModel: ThemeViewModel = viewModel()
    val currentMode = themeViewModel.themeController.colorSchemeMode
    val isDark = when (currentMode) {
        ColorSchemeMode.Light, ColorSchemeMode.MonetLight -> false
        ColorSchemeMode.Dark, ColorSchemeMode.MonetDark -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    val textPrimary = if (isDark) Color(0xFFF0F0F0) else Color(0xFF1A1A1A)
    val textSecondary = if (isDark) Color(0xFFB0B0B0) else Color(0xFF5F5F5F)
    val primaryColor: Color = MiuixTheme.colorScheme.primary
    val onPrimaryColor = if (primaryColor.luminance() > 0.5f) Color(0xFF1A1A1A) else Color.White
    val sheetBg = if (isDark) Color(0xFF1E1E1E) else Color(0xFFFFFFFF)

    val isExpense = bill.amount < 0
    val amountColor = if (isExpense) {
        if (isDark) Color(0xFFE57373) else Color(0xFFE53935)
    } else {
        if (isDark) Color(0xFF81C784) else Color(0xFF43A047)
    }
    val amountBg = amountColor.copy(alpha = if (isDark) 0.18f else 0.10f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(sheetBg)
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        // 顶部横条
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(textSecondary.copy(alpha = 0.4f))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(20.dp))

        //
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "账单详情",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Filled.Close, "关闭", tint = textSecondary)
            }
        }

        Spacer(Modifier.height(8.dp))

        //
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(amountBg)
                .padding(vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isExpense) "支出" else "收入",
                    fontSize = 14.sp,
                    color = amountColor.copy(alpha = 0.85f)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (isExpense) "-¥${-bill.amount}" else "+¥${bill.amount}",
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        //
        DetailRow("分类", bill.category, textPrimary, textSecondary)
        DetailRow("备注", bill.title, textPrimary, textSecondary)
        DetailRow("日期", bill.date, textPrimary, textSecondary)

        when {
            bill.recurrenceType != "none" -> {
                val freqLabel = when (bill.recurrenceType) {
                    "daily" -> "每天"
                    "weekly" -> "每周"
                    "monthly" -> "每月"
                    else -> ""
                }
                DetailRow(
                    "同步",
                    "已开启 · $freqLabel",
                    primaryColor,
                    textSecondary,
                    trailingIcon = true
                )
            }
            bill.recurringId != 0L -> {
                DetailRow("同步", "自动生成", primaryColor, textSecondary)
            }
        }

        Spacer(Modifier.height(24.dp))

        //
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 删除 weight 1f
            Button(
                onClick = onDelete,
                modifier = Modifier.weight(1f).height(52.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDark) Color(0xFFE0E0E0) else Color(0xFFB71C1C),
                    contentColor = if (isDark) Color(0xFFB71C1C) else Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.Delete, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "删除",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            //
            Button(
                onClick = onEdit,
                modifier = Modifier.weight(2f).height(52.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = onPrimaryColor
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Filled.Edit, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "编辑",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color,
    labelColor: Color,
    trailingIcon: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = labelColor,
            modifier = Modifier.width(60.dp)
        )
        Spacer(Modifier.width(12.dp))
        if (trailingIcon) {
            Icon(
                Icons.Filled.Refresh,
                null,
                tint = valueColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(4.dp))
        }
        Text(
            value,
            fontSize = 15.sp,
            color = valueColor,
            fontWeight = FontWeight.Medium
        )
    }
}