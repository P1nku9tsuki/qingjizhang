package com.natsuki.qingjizhang

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OpenSourceScreen(
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val libraries = listOf(
        LibInfo(
            name = "MIUIX-UI",
            author = "compose-miuix-ui",
            license = "Apache-2.0",
            url = "https://github.com/compose-miuix-ui/miuix"
        ),
        LibInfo(
            name = "Haze",
            author = "Chris Banes",
            license = "Apache-2.0",
            url = "https://github.com/chrisbanes/haze"
        ),
        LibInfo(
            name = "Jetpack Compose",
            author = "AndroidX",
            license = "Apache-2.0",
            url = "https://developer.android.com/jetpack/compose"
        ),
        LibInfo(
            name = "Room",
            author = "AndroidX",
            license = "Apache-2.0",
            url = "https://developer.android.com/training/data-storage/room"
        ),
        LibInfo(
            name = "Kotlin Coroutines",
            author = "JetBrains",
            license = "Apache-2.0",
            url = "https://github.com/Kotlin/kotlinx.coroutines"
        ),
        LibInfo(
            name = "DataStore",
            author = "AndroidX",
            license = "Apache-2.0",
            url = "https://developer.android.com/topic/libraries/architecture/datastore"
        ),
        LibInfo(
            name = "WorkManager",
            author = "AndroidX",
            license = "Apache-2.0",
            url = "https://developer.android.com/topic/libraries/architecture/workmanager"
        ),
        LibInfo(
            name = "Lifecycle",
            author = "AndroidX",
            license = "Apache-2.0",
            url = "https://developer.android.com/jetpack/androidx/releases/lifecycle"
        ),
        LibInfo(
            name = "Material Icons Extended",
            author = "AndroidX",
            license = "Apache-2.0",
            url = "https://developer.android.com/jetpack/androidx/releases/compose-material"
        ),
        LibInfo(
            name = "Kotlin",
            author = "JetBrains",
            license = "Apache-2.0",
            url = "https://github.com/JetBrains/kotlin"
        )
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部栏
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, "返回", tint = textPrimary)
            }
            Text("开源致谢", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = textPrimary)
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 感谢语
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 8.dp)
                ) {
                    Text(
                        "感谢每一位开源贡献者",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "轻记账能够顺利诞生，离不开以下开源项目的支持。" +
                                "在此，向所有开源作者致以最诚挚的敬意。",
                        fontSize = 13.sp,
                        color = textSecondary,
                        lineHeight = 20.sp
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            items(libraries.size) { idx ->
                val lib = libraries[idx]
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lib.url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                    shape = RoundedCornerShape(16.dp),
                    color = cardBg,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(primaryColor.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                lib.name.take(1).uppercase(),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                lib.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                            Spacer(Modifier.height(3.dp))
                            Text(
                                "${lib.author} · ${lib.license}",
                                fontSize = 11.sp,
                                color = textSecondary.copy(alpha = 0.75f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Filled.OpenInNew,
                            contentDescription = "打开链接",
                            tint = textSecondary.copy(alpha = 0.5f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // 版权声明
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(1.dp)
                            .background(textSecondary.copy(alpha = 0.2f))
                    )
                    Spacer(Modifier.height(20.dp))

                    Text(
                        "轻记账",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Copyright © 2026 Natsuki",
                        fontSize = 12.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

private data class LibInfo(
    val name: String,
    val author: String,
    val license: String,
    val url: String
)