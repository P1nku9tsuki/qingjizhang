package com.natsuki.qingjizhang

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun YearlyStatsView(
    viewModel: BillViewModel,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    val allBills by viewModel.allBillsFlow.collectAsState(initial = emptyList())
    val selectedYear by viewModel.selectedYear.collectAsState()

    val monthlyData = remember(allBills, selectedYear) {
        (1..12).map { month ->
            var income = 0.0
            var expense = 0.0
            allBills.forEach { bill ->
                val parts = bill.date.split(" ", "-", ":")
                if (parts.size >= 3 &&
                    parts[0].toIntOrNull() == selectedYear &&
                    parts[1].toIntOrNull() == month
                ) {
                    if (bill.amount > 0) income += bill.amount
                    else expense += -bill.amount
                }
            }
            month to (income to expense)
        }
    }

    val maxValue = monthlyData.maxOfOrNull { maxOf(it.second.first, it.second.second) }?.takeIf { it > 0 } ?: 1.0
    val totalIncome = monthlyData.sumOf { it.second.first }
    val totalExpense = monthlyData.sumOf { it.second.second }
    val balance = totalIncome - totalExpense

    val incomeColor = Color(0xFF43A047)
    val expenseColor = Color(0xFFE53935)

    val textMeasurer = rememberTextMeasurer()

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            "${selectedYear}年总览",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("结余 ", fontSize = 13.sp, color = textSecondary)
            Text(
                "¥${formatAmount(balance)}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                style = TextStyle(fontFeatureSettings = "tnum")
            )
        }

        Spacer(Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Legend(incomeColor, "收入 ¥${formatAmount(totalIncome)}", textSecondary)
            Spacer(Modifier.width(16.dp))
            Legend(expenseColor, "支出 ¥${formatAmount(totalExpense)}", textSecondary)
        }

        Spacer(Modifier.height(20.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val barGroupWidth = size.width / 12f
            val barWidth = barGroupWidth * 0.35f
            val gap = barGroupWidth * 0.15f
            val chartHeight = size.height - 30f
            val maxBarHeight = chartHeight

            for (i in 0 until 12) {
                val (_, values) = monthlyData[i]
                val (income, expense) = values

                val groupX = i * barGroupWidth

                val incomeH = (income / maxValue * maxBarHeight).toFloat()
                drawRoundRect(
                    color = incomeColor,
                    topLeft = Offset(groupX + gap, chartHeight - incomeH),
                    size = Size(barWidth, incomeH),
                    cornerRadius = CornerRadius(3f, 3f)
                )

                val expenseH = (expense / maxValue * maxBarHeight).toFloat()
                drawRoundRect(
                    color = expenseColor,
                    topLeft = Offset(groupX + gap + barWidth + 2f, chartHeight - expenseH),
                    size = Size(barWidth, expenseH),
                    cornerRadius = CornerRadius(3f, 3f)
                )

                val label = "${i + 1}"
                val measured = textMeasurer.measure(
                    text = label,
                    style = TextStyle(fontSize = 10.sp, color = textSecondary)
                )
                drawText(
                    textLayoutResult = measured,
                    topLeft = Offset(
                        groupX + barGroupWidth / 2f - measured.size.width / 2f,
                        chartHeight + 6f
                    )
                )
            }
        }

        Spacer(Modifier.height(6.dp))
        Text(
            "单位：元",
            fontSize = 10.sp,
            color = textSecondary
        )
    }
}

@Composable
private fun Legend(color: Color, label: String, textSecondary: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(4.dp))
        Text(label, fontSize = 12.sp, color = textSecondary)
    }
}