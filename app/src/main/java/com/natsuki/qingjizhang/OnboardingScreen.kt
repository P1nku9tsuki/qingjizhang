package com.natsuki.qingjizhang

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.runtime.snapshotFlow

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val primaryColor: Color = MiuixTheme.colorScheme.primary
    val textPrimary: Color = MiuixTheme.colorScheme.onSurface
    val textSecondary: Color = MiuixTheme.colorScheme.onSurface

    val pages = remember {
        listOf(
            OnboardingPage(
                icon = Icons.Filled.AttachMoney,
                title = "轻记账",
                description = "克制而专注\n让记账回归轻盈本身"
            ),
            OnboardingPage(
                icon = Icons.Filled.Calculate,
                title = "记录",
                description = "自定义数字键盘，响应利落\n算式与备注识别，一步到位"
            ),
            OnboardingPage(
                icon = Icons.Filled.BarChart,
                title = "分析",
                description = "环形图勾勒消费构成\n月与年，两种时间尺度\n亦可一键生成可分享的账单图"
            ),
            OnboardingPage(
                icon = Icons.Filled.TrendingUp,
                title = "规划",
                description = "月度与分类预算并行\n临界或超支，主动提醒\n储蓄目标，让坚持可见"
            ),
            OnboardingPage(
                icon = Icons.Filled.Settings,
                title = "定制",
                description = "动态取色，风格随心\n桌面快捷入口，一触即达\n记录成习惯，里程碑有回响"
            )
        )
    }

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        var lastPage = pagerState.currentPage
        snapshotFlow { pagerState.currentPage }
            .collect { page ->
                if (page != lastPage) {
                    vibrate(context)
                    lastPage = page
                }
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                beyondViewportPageCount = 1,
                userScrollEnabled = true
            ) { pageIndex ->
                val page = pages[pageIndex]

                val pageOffset = ((pagerState.currentPage - pageIndex) +
                        pagerState.currentPageOffsetFraction).coerceIn(-1f, 1f)

                OnboardingPageContent(
                    page = page,
                    pageOffset = pageOffset,
                    primaryColor = primaryColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 24.dp)
            ) {
                repeat(pages.size) { idx ->
                    val isSelected = pagerState.currentPage == idx
                    val dotWidth by animateFloatAsState(
                        targetValue = if (isSelected) 24f else 8f,
                        animationSpec = tween(280),
                        label = "dotWidth"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(dotWidth.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isSelected) primaryColor
                                else textSecondary.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            val isLastPage = pagerState.currentPage == pages.size - 1
            Button(
                onClick = {
                    vibrate(context)   // 👈 点击震动
                    if (isLastPage) {
                        onFinish()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = if (primaryColor.luminance() > 0.5f) Color(0xFF1A1A1A) else Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isLastPage) "开始使用" else "下一步",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!isLastPage) {
                TextButton(
                    onClick = {
                        vibrate(context)
                        onFinish()
                    },
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Text("跳过", color = textSecondary, fontSize = 14.sp)
                }
            } else {
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    pageOffset: Float,
    primaryColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    val scale = 1f - kotlin.math.abs(pageOffset) * 0.15f
    val alpha = 1f - kotlin.math.abs(pageOffset) * 0.6f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(64.dp)
            )
        }
        Spacer(Modifier.height(48.dp))
        Text(
            text = page.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = textPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Text(
            text = page.description,
            fontSize = 15.sp,
            color = textSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

private data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private fun Color.luminance(): Float {
    return 0.299f * red + 0.587f * green + 0.114f * blue
}