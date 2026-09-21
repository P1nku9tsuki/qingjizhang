package com.natsuki.qingjizhang

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import top.yukonga.miuix.kmp.theme.MiuixTheme

data class AppColorScheme(
    val isDark: Boolean,

    //
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,

    //
    val cardBg: Color,
    val cardBgElevated: Color,
    val divider: Color,

    //
    val income: Color,
    val expense: Color,
    val warning: Color,
    val danger: Color,

    //
    val incomeSoft: Color,
    val expenseSoft: Color,
    val warningSoft: Color,
    val dangerSoft: Color
)

object AppColors {
    fun get(isDark: Boolean): AppColorScheme = if (isDark) DarkScheme else LightScheme
    //
    val DarkScheme = AppColorScheme(
        isDark = true,
        textPrimary = Color(0xFFE4E4E4),
        textSecondary = Color(0xFF9E9E9E),
        textTertiary = Color(0xFF6B6B6B),
        cardBg = Color(0xFF1E1E1E),
        cardBgElevated = Color(0xFF262626),
        divider = Color(0x1FFFFFFF),
        income = Color(0xFF81C784),
        expense = Color(0xFFEF9A9A),
        warning = Color(0xFFFFB74D),
        danger = Color(0xFFEF5350),
        incomeSoft = Color(0x2681C784),
        expenseSoft = Color(0x26EF9A9A),
        warningSoft = Color(0x26FFB74D),
        dangerSoft = Color(0x26EF5350)
    )

    val LightScheme = AppColorScheme(
        isDark = false,
        textPrimary = Color(0xFF1A1A1A),
        textSecondary = Color(0xFF6B6B6B),
        textTertiary = Color(0xFF9E9E9E),
        cardBg = Color(0xFFF5F5F5),
        cardBgElevated = Color(0xFFFFFFFF),
        divider = Color(0x14000000),
        income = Color(0xFF43A047),
        expense = Color(0xFFE53935),
        warning = Color(0xFFF57C00),
        danger = Color(0xFFE53935),
        incomeSoft = Color(0x2681C784),
        expenseSoft = Color(0x1FE53935),
        warningSoft = Color(0x1FF57C00),
        dangerSoft = Color(0x1FE53935)
    )

    //
    val Local = staticCompositionLocalOf<AppColorScheme> {
        error("AppColors.Local 未注入。请在 MainActivity 里包一层 CompositionLocalProvider。")
    }

    val current: AppColorScheme
        @Composable
        @ReadOnlyComposable
        get() = Local.current
}

