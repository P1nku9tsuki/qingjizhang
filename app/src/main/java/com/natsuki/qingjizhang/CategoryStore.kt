package com.natsuki.qingjizhang

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryStore {
    val customExpenseCats = mutableStateListOf<Pair<String, ImageVector>>()
    val customIncomeCats = mutableStateListOf<Pair<String, ImageVector>>()

    val categoryColors = mutableStateMapOf<String, Int>()

    val DEFAULT_COLORS: Map<String, Int> = mapOf(
        "餐饮" to 0xFFE57373.toInt(),
        "交通" to 0xFF64B5F6.toInt(),
        "购物" to 0xFFFFB74D.toInt(),
        "学习" to 0xFF9575CD.toInt(),
        "娱乐" to 0xFF4DB6AC.toInt(),
        "医疗" to 0xFFF06292.toInt(),
        "住房" to 0xFF7986CB.toInt(),
        "工资" to 0xFF81C784.toInt(),
        "奖金" to 0xFFFFD54F.toInt(),
        "投资" to 0xFF4FC3F7.toInt(),
        "兼职" to 0xFFA1887F.toInt(),
        "红包" to 0xFFEF9A9A.toInt(),
        "其他" to 0xFF90A4AE.toInt()
    )


    fun getColor(category: String): Color {
        val argb = categoryColors[category] ?: DEFAULT_COLORS[category] ?: 0xFF90A4AE.toInt()
        return Color(argb)
    }

    val COLOR_PALETTE: List<Int> = listOf(
        0xFFE57373.toInt(),
        0xFFF06292.toInt(),
        0xFFEF9A9A.toInt(),
        0xFFBA68C8.toInt(),
        0xFF9575CD.toInt(),
        0xFF7986CB.toInt(),
        0xFF64B5F6.toInt(),
        0xFF4FC3F7.toInt(),
        0xFF4DB6AC.toInt(),
        0xFF81C784.toInt(),
        0xFFAED581.toInt(),
        0xFFFFD54F.toInt(),
        0xFFFFB74D.toInt(),
        0xFFA1887F.toInt(),
        0xFF90A4AE.toInt()
    )
}