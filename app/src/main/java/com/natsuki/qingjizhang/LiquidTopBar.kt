package com.natsuki.qingjizhang

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LiquidTopBar(
    title: String,
    isAppDark: Boolean,
    actions: @Composable RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    val textPrimary = if (isAppDark) Color(0xFFEEEEEE) else Color(0xFF212121)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 50.dp)
            .height(64.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
            Spacer(Modifier.weight(1f))
            actions()
        }
    }
}
