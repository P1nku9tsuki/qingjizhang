package com.natsuki.qingjizhang

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun AboutScreen(
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    onBack: () -> Unit,
    onCheckUpdate: () -> Unit,
    checking: Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember { ThemePreferences(context.applicationContext) }

    val updateCheckEnabled by preferences.updateCheckEnabledFlow.collectAsState(initial = true)
    val githubRepo by preferences.githubRepoFlow.collectAsState(initial = "")

    val version = remember { UpdateChecker.getCurrentVersion(context) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "返回", tint = textPrimary)
            }
            Text("关于", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "应用图标",
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(24.dp))
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "轻记账",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    "版本 $version",
                    fontSize = 13.sp,
                    color = textSecondary
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "简洁优雅的记账应用",
                    fontSize = 13.sp,
                    color = textSecondary
                )
            }

            Spacer(Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(cardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("自动检查更新", fontSize = 15.sp, color = textPrimary)
                        Spacer(Modifier.height(3.dp))
                        Text(
                            "启动时静默检查 GitHub Release",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                    Switch(
                        checked = updateCheckEnabled,
                        onCheckedChange = { enabled ->
                            scope.launch { preferences.setUpdateCheckEnabled(enabled) }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = primaryColor
                        )
                    )
                }

                Divider(textSecondary)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (!checking) onCheckUpdate()
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            if (checking) "正在检查..." else "立即检查更新",
                            fontSize = 15.sp,
                            color = textPrimary
                        )
                        if (githubRepo.isBlank()) {
                            Spacer(Modifier.height(3.dp))
                            Text(
                                "尚未配置仓库地址",
                                fontSize = 12.sp,
                                color = Color(0xFFE53935)
                            )
                        }
                    }
                    Text(
                        "→",
                        fontSize = 16.sp,
                        color = textSecondary.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(cardBg)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            if (githubRepo.isNotBlank()) {
                                UpdateChecker.openReleasePage(
                                    context,
                                    "https://github.com/$githubRepo"
                                )
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("项目主页", fontSize = 15.sp, color = textPrimary)
                        Spacer(Modifier.height(3.dp))
                        Text(
                            if (githubRepo.isBlank()) "未配置" else "github.com/$githubRepo",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                    Icon(
                        Icons.Filled.OpenInNew,
                        contentDescription = null,
                        tint = textSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Divider(textSecondary)


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "开源协议",
                        fontSize = 15.sp,
                        color = textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text("GPL-3.0", fontSize = 13.sp, color = textSecondary)
                }
            }

            Spacer(Modifier.height(32.dp))


            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Copyright © 2026 Natsuki",
                    fontSize = 11.sp,
                    color = textSecondary.copy(alpha = 0.6f)
                )

            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun Divider(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(start = 16.dp)
            .background(color.copy(alpha = 0.08f))
    )
}