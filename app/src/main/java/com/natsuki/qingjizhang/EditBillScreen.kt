package com.natsuki.qingjizhang

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBillScreen(
    bill: BillEntity,
    viewModel: BillViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
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

    var amount by remember { mutableStateOf(kotlin.math.abs(bill.amount).toString()) }
    var note by remember { mutableStateOf(bill.title) }
    var category by remember { mutableStateOf(bill.category) }
    var isExpense by remember { mutableStateOf(bill.amount < 0) }
    var selectedDate by remember { mutableStateOf(parseDateToMillis(bill.date)) }
    var showDatePicker by remember { mutableStateOf(false) }

    //
    var recurrenceType by remember { mutableStateOf(bill.recurrenceType) }
    var showRecurrencePicker by remember { mutableStateOf(false) }

    val expenseCategories = listOf(
        "餐饮" to Icons.Filled.Restaurant,
        "交通" to Icons.Filled.DirectionsCar,
        "购物" to Icons.Filled.ShoppingBag,
        "学习" to Icons.Filled.Book,
        "娱乐" to Icons.Filled.SportsEsports,
        "医疗" to Icons.Filled.LocalHospital,
        "住房" to Icons.Filled.Home,
        "其他" to Icons.Filled.Category
    )
    val incomeCategories = listOf(
        "工资" to Icons.Filled.AttachMoney,
        "奖金" to Icons.Filled.Star,
        "投资" to Icons.Filled.TrendingUp,
        "兼职" to Icons.Filled.Work,
        "红包" to Icons.Filled.Redeem,
        "其他" to Icons.Filled.Category
    )

    val currentCats = buildList {
        if (isExpense) {
            addAll(expenseCategories)
            addAll(CategoryStore.customExpenseCats)
        } else {
            addAll(incomeCategories)
            addAll(CategoryStore.customIncomeCats)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { vibrateEdit(context); onBack() }) {
                Icon(Icons.Filled.ArrowBack, "返回", tint = textPrimary)
            }
            Text("编辑账单", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterChip(
                selected = isExpense,
                onClick = { vibrateEdit(context); isExpense = true; category = "餐饮" },
                label = { Text("支出") },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = primaryColor,
                    selectedLabelColor = onPrimaryColor
                )
            )
            FilterChip(
                selected = !isExpense,
                onClick = { vibrateEdit(context); isExpense = false; category = "工资" },
                label = { Text("收入") },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = primaryColor,
                    selectedLabelColor = onPrimaryColor
                )
            )
        }

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
            placeholder = { Text("0.00", fontSize = 28.sp, color = textSecondary.copy(alpha = 0.5f)) },
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

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            placeholder = { Text("备注", color = textSecondary.copy(alpha = 0.5f)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, color = textPrimary),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = textSecondary.copy(alpha = 0.3f),
                cursorColor = primaryColor
            )
        )

        Spacer(Modifier.height(20.dp))

        Text("选择分类", fontSize = 14.sp, color = textSecondary)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(currentCats.size) { idx ->
                val (name, icon) = currentCats[idx]
                val isSelected = category == name
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) primaryColor.copy(alpha = 0.15f) else Color.Transparent)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) primaryColor else textSecondary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { vibrateEdit(context); category = name }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) primaryColor else textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        color = if (isSelected) primaryColor else textPrimary
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        //
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .clickable { vibrateEdit(context); showDatePicker = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.DateRange, null, tint = primaryColor, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Text(
                text = formatDisplayDate(selectedDate),
                fontSize = 16.sp,
                color = textPrimary,
                modifier = Modifier.weight(1f)
            )
            Text("修改", fontSize = 13.sp, color = primaryColor)
        }

        Spacer(Modifier.height(12.dp))

        //
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .clickable {
                    vibrateEdit(context)
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

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                vibrateEdit(context)
                val amt = amount.toDoubleOrNull()
                if (amt != null && amt > 0) {
                    viewModel.updateBill(
                        bill.copy(
                            title = note.ifBlank { category },
                            amount = if (isExpense) -amt else amt,
                            category = category,
                            date = formatDateForStorage(selectedDate),
                            isExpense = isExpense,
                            isRecurring = recurrenceType != "none",
                            recurrenceType = recurrenceType
                        )
                    )
                    onSaved()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = amount.toDoubleOrNull()?.let { it > 0 } == true,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryColor,
                contentColor = onPrimaryColor,
                disabledContainerColor = primaryColor.copy(alpha = 0.3f),
                disabledContentColor = onPrimaryColor.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("保存修改", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { selectedDate = it }
                        showDatePicker = false
                    }) { Text("确定", color = primaryColor) }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("取消", color = textSecondary)
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false
                )
            }
        }
    }

    //
    if (showRecurrencePicker) {
        AlertDialog(
            onDismissRequest = { showRecurrencePicker = false },
            title = { Text("同步频率", color = textPrimary) },
            text = {
                Column {
                    RecurrenceOption("不同步", recurrenceType == "none", textPrimary, primaryColor) {
                        vibrateEdit(context); recurrenceType = "none"; showRecurrencePicker = false
                    }
                    RecurrenceOption("每天同步", recurrenceType == "daily", textPrimary, primaryColor) {
                        vibrateEdit(context); recurrenceType = "daily"; showRecurrencePicker = false
                    }
                    RecurrenceOption("每周同步", recurrenceType == "weekly", textPrimary, primaryColor) {
                        vibrateEdit(context); recurrenceType = "weekly"; showRecurrencePicker = false
                    }
                    RecurrenceOption("每月同步", recurrenceType == "monthly", textPrimary, primaryColor) {
                        vibrateEdit(context); recurrenceType = "monthly"; showRecurrencePicker = false
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showRecurrencePicker = false }) {
                    Text("取消", color = textSecondary)
                }
            }
        )
    }
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

private fun parseDateToMillis(dateStr: String): Long {
    return try {
        java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).parse(dateStr)?.time
            ?: System.currentTimeMillis()
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

private fun formatDateForStorage(millis: Long): String {
    return java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date(millis))
}

private fun vibrateEdit(context: Context) {
    vibrate(context)
}