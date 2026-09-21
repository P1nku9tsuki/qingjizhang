package com.natsuki.qingjizhang

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth

enum class HeatmapMode { MONTH, YEAR }

@Composable
fun HeatmapChart(
    viewModel: BillViewModel,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    mode: HeatmapMode = HeatmapMode.YEAR,
    year: Int,
    month: Int
) {
    val allBills by viewModel.allBillsFlow.collectAsState(initial = emptyList())

    val dailyExpense = remember(allBills) {
        allBills.filter { it.amount < 0 }
            .groupBy { it.date.substringBefore(" ") }
            .mapValues { (_, list) -> list.sumOf { -it.amount } }
    }

    val maxAmount = dailyExpense.values.maxOrNull() ?: 0.0

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(
            if (mode == HeatmapMode.MONTH) "${year}年${month}月热力图" else "${year}年热力图",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary
        )

        Spacer(Modifier.height(4.dp))

        Text(
            if (mode == HeatmapMode.MONTH) "颜色越深当天支出越多" else "颜色越深当月支出越多",
            fontSize = 12.sp,
            color = textSecondary
        )

        Spacer(Modifier.height(12.dp))

        // 图例
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text("少", fontSize = 10.sp, color = textSecondary)
            Spacer(Modifier.width(6.dp))
            listOf(0.10f, 0.30f, 0.55f, 0.80f, 1f).forEach { a ->
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(primaryColor.copy(alpha = a))
                )
                Spacer(Modifier.width(3.dp))
            }
            Text("多", fontSize = 10.sp, color = textSecondary)
        }

        Spacer(Modifier.height(12.dp))

        when (mode) {
            HeatmapMode.MONTH -> MonthDaysGrid(
                year = year,
                month = month,
                dailyExpense = dailyExpense,
                maxAmount = maxAmount,
                primaryColor = primaryColor,
                textSecondary = textSecondary
            )
            HeatmapMode.YEAR -> YearMonthsGrid(
                year = year,
                dailyExpense = dailyExpense,
                primaryColor = primaryColor,
                textSecondary = textSecondary
            )
        }
    }
}

@Composable
private fun MonthDaysGrid(
    year: Int,
    month: Int,
    dailyExpense: Map<String, Double>,
    maxAmount: Double,
    primaryColor: Color,
    textSecondary: Color
) {
    val daysInMonth = YearMonth.of(year, month).lengthOfMonth()
    val cols = 7
    val rows = (daysInMonth + cols - 1) / cols

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height((rows * 32).dp)
    ) {
        val gap = 4f
        val cellW = (size.width - gap * (cols - 1)) / cols
        val cellH = (size.height - gap * (rows - 1)) / rows
        val square = minOf(cellW, cellH)

        for (i in 0 until daysInMonth) {
            val row = i / cols
            val col = i % cols
            val dayNum = i + 1
            val date = LocalDate.of(year, month, dayNum)
            val amount = dailyExpense[date.toString()] ?: 0.0
            val alpha = if (maxAmount > 0) (amount / maxAmount).toFloat() else 0f

            val color = if (amount <= 0.0) {
                textSecondary.copy(alpha = 0.10f)
            } else {
                primaryColor.copy(alpha = 0.25f + 0.75f * alpha)
            }

            drawRoundRect(
                color = color,
                topLeft = Offset(col * (cellW + gap), row * (cellH + gap)),
                size = Size(square, square),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
    }
}

@Composable
private fun YearMonthsGrid(
    year: Int,
    dailyExpense: Map<String, Double>,
    primaryColor: Color,
    textSecondary: Color
) {
    //
    val monthlyExpense = remember(year, dailyExpense) {
        (1..12).map { m ->
            val prefix = String.format("%04d-%02d", year, m)
            m to dailyExpense.filterKeys { it.startsWith(prefix) }.values.sum()
        }
    }

    val maxMonthly = monthlyExpense.maxOfOrNull { it.second }?.takeIf { it > 0 } ?: 1.0

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (row in 0 until 3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0 until 4) {
                    val index = row * 4 + col
                    val monthNum = index + 1
                    val amount = monthlyExpense[index].second
                    val ratio = (amount / maxMonthly).toFloat()

                    val bgColor = if (amount <= 0.0) {
                        textSecondary.copy(alpha = 0.10f)
                    } else {
                        primaryColor.copy(alpha = 0.25f + 0.75f * ratio)
                    }

                    // 文字颜色：格子越深，用白字；越浅，用灰字
                    val textColor = if (ratio > 0.45f) Color.White else textSecondary

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.4f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "${monthNum}月",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            if (amount > 0) {
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    "¥${amount.toInt()}",
                                    fontSize = 10.sp,
                                    color = textColor.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}