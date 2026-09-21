package com.natsuki.qingjizhang

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.theme.MiuixTheme

val PRESET_KEY_COLORS: List<Int> = listOf(
    // 粉紫系
    0xFFC8A8A8.toInt(), // 藕粉
    0xFFB88A96.toInt(), // 玫瑰粉
    0xFFA87C96.toInt(), // 玫瑰紫
    0xFF9B8AA6.toInt(), // 雾紫
    0xFFA89AC0.toInt(), // 薰衣草
    0xFF8C7AA6.toInt(), // 深紫灰

    // 蓝青系
    0xFF7C93B0.toInt(), // 雾霾蓝
    0xFF6B8AA8.toInt(), // 灰蓝
    0xFF7C9CB0.toInt(), // 天青灰
    0xFF6BA8A0.toInt(), // 松石绿
    0xFF5E8A90.toInt(), // 青灰
    0xFF5E7C8C.toInt(), // 深海灰

    // 绿系
    0xFF8FAE8B.toInt(), // 鼠尾草
    0xFF7CA47C.toInt(), // 苔藓绿
    0xFF6B8E7C.toInt(), // 灰绿
    0xFF8EAE70.toInt(), // 嫩芽绿
    0xFFA8A56B.toInt(), // 橄榄
    0xFF5E8C70.toInt(), // 墨绿

    // 黄橙 / 中性
    0xFFD4C48A.toInt(), // 奶油黄
    0xFFC0B070.toInt(), // 芥末黄
    0xFFB8906A.toInt(), // 焦糖
    0xFFC08A6A.toInt(), // 陶土橙
    0xFF8C8C8C.toInt(), // 高级灰
    0xFF9C948A.toInt()  // 暖灰
)

@Composable
fun ColorPickerSheet(
    currentArgb: Int,
    isDark: Boolean,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val surface = MiuixTheme.colorScheme.surface
    val onSurface = MiuixTheme.colorScheme.onSurface
    val primary = MiuixTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            //
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(onSurface.copy(alpha = 0.4f))
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "主题色",
                fontSize = 18.sp,
                color = onSurface,
                modifier = Modifier.padding(top = 20.dp, bottom = 4.dp)
            )
            Text(
                text = "选一个低饱和色作为应用主色",
                fontSize = 13.sp,
                color = onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            //
            PRESET_KEY_COLORS.chunked(6).forEach { rowColors ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowColors.forEach { argb ->
                        ColorDot(
                            argb = argb,
                            selected = argb == currentArgb,
                            onSurface = onSurface,
                            primary = primary,
                            onClick = { onSelect(argb) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            //
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(onSurface.copy(alpha = 0.06f))
                    .clickable { onSelect(-1) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (currentArgb == -1) "✓ 已使用系统动态色" else "恢复系统动态色",
                    fontSize = 15.sp,
                    color = if (currentArgb == -1) primary else onSurface
                )
            }
        }
    }
}

@Composable
private fun ColorDot(
    argb: Int,
    selected: Boolean,
    onSurface: Color,
    primary: Color,
    onClick: () -> Unit
) {
    val color = Color(argb)
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(
                if (selected) primary.copy(alpha = 0.15f)
                else Color.Transparent
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (selected) 34.dp else 30.dp)
                .clip(CircleShape)
                .background(color)
                .then(
                    if (selected) Modifier.border(
                        width = 2.dp,
                        color = onSurface.copy(alpha = 0.6f),
                        shape = CircleShape
                    ) else Modifier
                )
        )
    }
}