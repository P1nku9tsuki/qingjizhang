package com.natsuki.qingjizhang.ui.theme

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

data class BillItem(
    val id: Int,
    val title: String,      // 账单名称（如：早饭）
    val amount: Double,     // 金额（负数支出，正数收入）
    val category: String,   // 分类（如：餐饮）
    val date: String,       // 日期（如：今天 08:30）
    val icon: ImageVector   // 图标
)