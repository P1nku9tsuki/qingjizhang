package com.natsuki.qingjizhang

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth

data class MonthPoint(
    val year: Int,
    val month: Int,
    val income: Double,
    val expense: Double
)

@Composable
fun MonthlyTrendChart(
    viewModel: BillViewModel,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    val allBills by viewModel.allBillsFlow.collectAsState(initial = emptyList())
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()

    // 以主页选中的年月为终点，往前推 6 个月
    val points: List<MonthPoint> = remember(allBills, selectedYear, selectedMonth) {
        val end = YearMonth.of(selectedYear, selectedMonth)
        (5 downTo 0).map { offset ->
            val ym = end.minusMonths(offset.toLong())
            val year = ym.year
            val month = ym.monthValue

            var income = 0.0
            var expense = 0.0
            allBills.forEach { bill ->
                val parts = bill.date.split(" ", "-", ":")
                if (parts.size >= 3 &&
                    parts[0].toIntOrNull() == year &&
                    parts[1].toIntOrNull() == month
                ) {
                    if (bill.amount > 0) income += bill.amount
                    else expense += -bill.amount
                }
            }
            MonthPoint(year, month, income, expense)
        }
    }

    val incomeColor = Color(0xFF43A047)
    val expenseColor = Color(0xFFE53935)

    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "近 6 个月趋势",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(Modifier.weight(1f))
            Legend(color = incomeColor, label = "收入", textSecondary = textSecondary)
            Spacer(Modifier.width(12.dp))
            Legend(color = expenseColor, label = "支出", textSecondary = textSecondary)
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                val leftPadding = 8f
                val rightPadding = 8f
                val topPadding = 12f
                val bottomPadding = 28f

                val chartWidth = size.width - leftPadding - rightPadding
                val chartHeight = size.height - topPadding - bottomPadding

                val maxValue = points.maxOfOrNull {
                    maxOf(it.income, it.expense)
                }?.takeIf { it > 0 } ?: 1.0

                val stepX = if (points.size > 1) chartWidth / (points.size - 1) else 0f

                fun xOf(index: Int): Float = leftPadding + stepX * index
                fun yOf(value: Double): Float =
                    topPadding + chartHeight * (1f - (value / maxValue).toFloat())

                // 水平网格线
                for (i in 0..3) {
                    val y = topPadding + chartHeight * i / 3f
                    drawLine(
                        color = textSecondary.copy(alpha = 0.12f),
                        start = Offset(leftPadding, y),
                        end = Offset(size.width - rightPadding, y),
                        strokeWidth = 1f
                    )
                }

                // 收入折线
                if (points.size >= 2) {
                    val incomePath = Path().apply {
                        moveTo(xOf(0), yOf(points[0].income))
                        for (i in 1 until points.size) {
                            lineTo(xOf(i), yOf(points[i].income))
                        }
                    }
                    drawPath(path = incomePath, color = incomeColor, style = Stroke(width = 4f))

                    val expensePath = Path().apply {
                        moveTo(xOf(0), yOf(points[0].expense))
                        for (i in 1 until points.size) {
                            lineTo(xOf(i), yOf(points[i].expense))
                        }
                    }
                    drawPath(path = expensePath, color = expenseColor, style = Stroke(width = 4f))
                }

                // 数据点和月份标签
                points.forEachIndexed { index, p ->
                    val x = xOf(index)

                    drawCircle(color = Color.White, radius = 6f, center = Offset(x, yOf(p.income)))
                    drawCircle(color = incomeColor, radius = 4f, center = Offset(x, yOf(p.income)))

                    drawCircle(color = Color.White, radius = 6f, center = Offset(x, yOf(p.expense)))
                    drawCircle(color = expenseColor, radius = 4f, center = Offset(x, yOf(p.expense)))

                    val label = "${p.month}月"
                    val measured = textMeasurer.measure(
                        text = label,
                        style = TextStyle(fontSize = 11.sp, color = textSecondary)
                    )
                    drawText(
                        textLayoutResult = measured,
                        topLeft = Offset(
                            x - measured.size.width / 2f,
                            size.height - bottomPadding + 6f
                        )
                    )
                }
            }
        }
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