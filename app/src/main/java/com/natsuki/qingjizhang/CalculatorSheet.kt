package com.natsuki.qingjizhang

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.abs

/**
 * 计算器 Overlay。
 *
 * 用全屏 Dialog 实现，避开 HorizontalPager 的尺寸限制。
 */
@Composable
fun CalculatorOverlay(
    visible: Boolean,
    onDismiss: () -> Unit,
    onApply: (Double) -> Unit
) {
    if (!visible) return

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.BottomCenter
        ) {
            CalculatorSheet(
                onApply = onApply,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun CalculatorSheet(
    onApply: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val surface = MiuixTheme.colorScheme.surface
    val onSurface = MiuixTheme.colorScheme.onSurface
    val primary = MiuixTheme.colorScheme.primary
    val onPrimary = MiuixTheme.colorScheme.onPrimary

    var expression by remember { mutableStateOf("") }
    val preview = remember(expression) { ExpressionEvaluator.evaluate(expression) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(surface)
            // 消费整块的点击，防止穿透到遮罩
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { /* 空消费 */ }
            )
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // 顶部把手
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(onSurface.copy(alpha = 0.4f))
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(20.dp))

        // 表达式 + 实时结果
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(onSurface.copy(alpha = 0.05f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = expression.ifEmpty { "0" },
                    fontSize = 18.sp,
                    color = onSurface.copy(alpha = 0.65f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(fontFeatureSettings = "tnum")
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (preview != null) "= ${formatAmount(preview)}" else "=",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (preview != null) primary else onSurface.copy(alpha = 0.3f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(fontFeatureSettings = "tnum")
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 键盘 5 行 4 列（无括号）
        val keyRows = listOf(
            listOf(
                KeySpec("7"), KeySpec("8"), KeySpec("9"),
                KeySpec("÷", operator = true)
            ),
            listOf(
                KeySpec("4"), KeySpec("5"), KeySpec("6"),
                KeySpec("×", operator = true)
            ),
            listOf(
                KeySpec("1"), KeySpec("2"), KeySpec("3"),
                KeySpec("-", operator = true)
            ),
            listOf(
                KeySpec("0"), KeySpec("."),
                KeySpec("⌫", operator = true),
                KeySpec("+", operator = true)
            ),
            listOf(
                KeySpec("AC", operator = true, weight = 2f),
                KeySpec("=", operator = true, weight = 2f, accent = true)
            )
        )

        keyRows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().height(56.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { spec ->
                    CalcKey(
                        label = spec.label,
                        modifier = Modifier.weight(spec.weight).fillMaxHeight(),
                        isOperator = spec.operator,
                        isAccent = spec.accent,
                        onClick = {
                            expression = handleCalcKey(expression, spec.label)
                        }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(4.dp))

        // 应用到金额
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (preview != null) primary else primary.copy(alpha = 0.3f))
                .clickable(
                    enabled = preview != null,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    vibrate(context)
                    preview?.let { onApply(it) }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "应用到金额",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (preview != null) onPrimary else onPrimary.copy(alpha = 0.5f)
            )
        }
    }
}

private data class KeySpec(
    val label: String,
    val operator: Boolean = false,
    val weight: Float = 1f,
    val accent: Boolean = false
)

@Composable
private fun CalcKey(
    label: String,
    modifier: Modifier = Modifier,
    isOperator: Boolean = false,
    isAccent: Boolean = false,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val onSurface = MiuixTheme.colorScheme.onSurface
    val primary = MiuixTheme.colorScheme.primary
    val onPrimary = MiuixTheme.colorScheme.onPrimary

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val baseBg = when {
        isAccent -> primary
        isOperator -> primary.copy(alpha = 0.10f)
        else -> onSurface.copy(alpha = 0.06f)
    }
    val pressedBg = when {
        isAccent -> primary.copy(alpha = 0.85f)
        isOperator -> primary.copy(alpha = 0.20f)
        else -> onSurface.copy(alpha = 0.14f)
    }
    val textColor = when {
        isAccent -> onPrimary
        isOperator -> primary
        else -> onSurface
    }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else 1f,
        animationSpec = tween(120, easing = MotionScheme.EasingStandard),
        label = "calcScale"
    )
    val bg by animateColorAsState(
        targetValue = if (pressed) pressedBg else baseBg,
        animationSpec = tween(120, easing = MotionScheme.EasingStandard),
        label = "calcBg"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                vibrate(context)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

// ============================================================
//  按键处理
// ============================================================

private fun handleCalcKey(current: String, key: String): String {
    return when (key) {
        "AC" -> ""
        "⌫" -> if (current.isEmpty()) "" else current.dropLast(1)
        "=" -> handleEquals(current)
        "+", "-", "×", "÷" -> handleOperator(current, key)
        "." -> handleDecimal(current)
        else -> handleDigit(current, key)
    }
}

private fun handleEquals(current: String): String {
    if (current.isEmpty()) return current
    val trimmed = current.trimEnd('+', '-', '×', '÷', '.')
    if (trimmed.isEmpty()) return current
    val result = ExpressionEvaluator.evaluate(trimmed) ?: return current
    return formatCalcResult(result)
}

private fun formatCalcResult(result: Double): String {
    if (result.isNaN() || result.isInfinite()) return "0"
    return if (abs(result) < 1e15 && result == result.toLong().toDouble()) {
        result.toLong().toString()
    } else {
        "%.6f".format(result).trimEnd('0').trimEnd('.')
    }
}

private fun handleOperator(current: String, op: String): String {
    if (current.isEmpty()) return current
    val last = current.last()
    return when {
        last in "+-×÷" -> current.dropLast(1) + op
        else -> current + op
    }
}

private fun handleDecimal(current: String): String {
    val lastSegment = current.takeLastWhile { it.isDigit() || it == '.' }
    if (lastSegment.contains(".")) return current
    return if (current.isEmpty() || current.last() in "+-×÷") {
        current + "0."
    } else {
        current + "."
    }
}

private fun handleDigit(current: String, digit: String): String {
    if (current.length >= 30) return current
    if (current.isNotEmpty() && current.last() == '0') {
        val before = if (current.length >= 2) current[current.length - 2] else null
        if (before == null || before in "+-×÷") {
            return current.dropLast(1) + digit
        }
    }
    return current + digit
}

// ============================================================
//  算式解析器：Shunting-yard + RPN 求值
// ============================================================

object ExpressionEvaluator {

    fun evaluate(expression: String): Double? {
        if (expression.isBlank()) return null
        return try {
            val tokens = tokenize(expression)
            if (tokens.isEmpty()) return null
            val rpn = toRpn(tokens)
            evalRpn(rpn)
        } catch (e: Exception) {
            null
        }
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i]); i++
                    }
                    tokens.add(sb.toString())
                }
                c == '+' || c == '-' || c == '*' || c == '/' ||
                        c == '(' || c == ')' -> {
                    tokens.add(c.toString()); i++
                }
                c == '×' -> { tokens.add("*"); i++ }
                c == '÷' -> { tokens.add("/"); i++ }
                c == ' ' -> i++
                else -> throw IllegalArgumentException("Invalid char: $c")
            }
        }
        return tokens
    }

    private fun toRpn(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = ArrayDeque<String>()
        val precedence = mapOf("+" to 1, "-" to 1, "*" to 2, "/" to 2)

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> output.add(token)
                token == "(" -> stack.addLast(token)
                token == ")" -> {
                    while (stack.isNotEmpty() && stack.last() != "(") {
                        output.add(stack.removeLast())
                    }
                    if (stack.isEmpty()) throw IllegalArgumentException("Mismatched parens")
                    stack.removeLast()
                }
                token in precedence -> {
                    while (stack.isNotEmpty() && stack.last() != "(" &&
                        (precedence[stack.last()] ?: 0) >= (precedence[token] ?: 0)
                    ) {
                        output.add(stack.removeLast())
                    }
                    stack.addLast(token)
                }
            }
        }
        while (stack.isNotEmpty()) {
            val op = stack.removeLast()
            if (op == "(") throw IllegalArgumentException("Mismatched parens")
            output.add(op)
        }
        return output
    }

    private fun evalRpn(rpn: List<String>): Double {
        val stack = ArrayDeque<Double>()
        for (token in rpn) {
            when (token) {
                "+" -> { val b = stack.removeLast(); val a = stack.removeLast(); stack.addLast(a + b) }
                "-" -> { val b = stack.removeLast(); val a = stack.removeLast(); stack.addLast(a - b) }
                "*" -> { val b = stack.removeLast(); val a = stack.removeLast(); stack.addLast(a * b) }
                "/" -> {
                    val b = stack.removeLast(); val a = stack.removeLast()
                    if (b == 0.0) throw ArithmeticException("Divide by zero")
                    stack.addLast(a / b)
                }
                else -> stack.addLast(token.toDouble())
            }
        }
        if (stack.size != 1) throw IllegalArgumentException("Invalid expression")
        return stack.last()
    }
}