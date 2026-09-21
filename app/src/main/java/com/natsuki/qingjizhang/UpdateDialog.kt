package com.natsuki.qingjizhang

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun UpdateDialog(
    currentVersion: String,
    updateInfo: UpdateInfo,
    onGoToDownload: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .clip(RoundedCornerShape(24.dp)),
        color = MiuixTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "发现新版本",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MiuixTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(8.dp))

            Row {
                Text(
                    "v$currentVersion",
                    fontSize = 12.sp,
                    color = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    "  →  ",
                    fontSize = 12.sp,
                    color = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    updateInfo.tagName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MiuixTheme.colorScheme.primary
                )
            }

            if (updateInfo.releaseNotes.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MiuixTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp)
                ) {
                    Text(
                        updateInfo.releaseNotes,
                        fontSize = 12.sp,
                        color = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "稍后",
                        color = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = onGoToDownload,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MiuixTheme.colorScheme.primary,
                        contentColor = MiuixTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("前往下载", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
