package com.natsuki.qingjizhang

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun CategoryColorSheet(
    isDark: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferences = remember { ThemePreferences(context.applicationContext) }

    val surface = MiuixTheme.colorScheme.surface
    val onSurface = MiuixTheme.colorScheme.onSurface
    val primary = MiuixTheme.colorScheme.primary
    val textSecondary = if (isDark) Color(0xFFAAAAAA) else Color(0xFF666666)

    var selectedTab by remember { mutableIntStateOf(0) }   // 0 支出 / 1 收入
    var colorPickerFor by remember { mutableStateOf<String?>(null) }

    //
    val builtInExpense = listOf("餐饮", "交通", "购物", "学习", "娱乐", "医疗", "住房", "其他")
    val builtInIncome = listOf("工资", "奖金", "投资", "兼职", "红包", "其他")

    val expenseList = remember(CategoryStore.customExpenseCats.size) {
        builtInExpense + CategoryStore.customExpenseCats.map { it.first }
    }
    val incomeList = remember(CategoryStore.customIncomeCats.size) {
        builtInIncome + CategoryStore.customIncomeCats.map { it.first }
    }

    val currentList = if (selectedTab == 0) expenseList else incomeList

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(surface)
            .padding(horizontal = 20.dp, vertical = 20.dp)
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

        Spacer(Modifier.height(20.dp))

        Text(
            "分类颜色",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = onSurface
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "点一下分类，选一个新颜色",
            fontSize = 13.sp,
            color = textSecondary
        )

        Spacer(Modifier.height(16.dp))

        //
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(onSurface.copy(alpha = 0.06f))
                .padding(4.dp)
        ) {
            TabButton(
                label = "支出",
                selected = selectedTab == 0,
                primary = primary,
                onSurface = onSurface,
                modifier = Modifier.weight(1f),
                onClick = { selectedTab = 0 }
            )
            TabButton(
                label = "收入",
                selected = selectedTab == 1,
                primary = primary,
                onSurface = onSurface,
                modifier = Modifier.weight(1f),
                onClick = { selectedTab = 1 }
            )
        }

        Spacer(Modifier.height(12.dp))

        //
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                if (targetState > initialState) {
                    //
                    (slideInHorizontally(
                        initialOffsetX = { it / 6 },
                        animationSpec = tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingEnter)
                    ) + fadeIn(tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingEnter)))
                        .togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { -it / 6 },
                                animationSpec = tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingExit)
                            ) + fadeOut(tween(MotionScheme.DurationShort, easing = MotionScheme.EasingExit))
                        )
                } else {
                    //
                    (slideInHorizontally(
                        initialOffsetX = { -it / 6 },
                        animationSpec = tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingEnter)
                    ) + fadeIn(tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingEnter)))
                        .togetherWith(
                            slideOutHorizontally(
                                targetOffsetX = { it / 6 },
                                animationSpec = tween(MotionScheme.DurationMedium, easing = MotionScheme.EasingExit)
                            ) + fadeOut(tween(MotionScheme.DurationShort, easing = MotionScheme.EasingExit))
                        )
                }
            },
            label = "CategoryColorTab"
        ) { tab ->
            val listForTab = if (tab == 0) expenseList else incomeList

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp),
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(listForTab.size) { idx ->
                    val name = listForTab[idx]
                    val color = CategoryStore.getColor(name)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                vibrate(context)
                                colorPickerFor = name
                            }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.20f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                getCategoryIcon(name),
                                contentDescription = null,
                                tint = color,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            name,
                            fontSize = 15.sp,
                            color = onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = {
                vibrate(context)
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("完成", color = primary, fontWeight = FontWeight.Bold)
        }
    }

    //
    if (colorPickerFor != null) {
        ColorPaletteDialog(
            categoryName = colorPickerFor!!,
            currentArgb = CategoryStore.getColor(colorPickerFor!!).toArgb(),
            primary = primary,
            onSurface = onSurface,
            textSecondary = textSecondary,
            onSelect = { argb ->
                val cat = colorPickerFor!!
                scope.launch {
                    if (argb == -1) preferences.resetCategoryColor(cat)
                    else preferences.saveCategoryColor(cat, argb)
                }
                colorPickerFor = null
            },
            onDismiss = { colorPickerFor = null }
        )
    }
}

@Composable
private fun TabButton(
    label: String,
    selected: Boolean,
    primary: Color,
    onSurface: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) primary else Color.Transparent)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ColorPaletteDialog(
    categoryName: String,
    currentArgb: Int,
    primary: Color,
    onSurface: Color,
    textSecondary: Color,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MiuixTheme.colorScheme.surface)
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "为「$categoryName」选颜色",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = onSurface
            )
            Spacer(Modifier.height(16.dp))

            //
            CategoryStore.COLOR_PALETTE.chunked(5).forEach { rowColors ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    rowColors.forEach { argb ->
                        val color = Color(argb)
                        val isSelected = argb == currentArgb
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) primary.copy(alpha = 0.18f)
                                    else Color.Transparent
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    vibrate(context)
                                    onSelect(argb)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 34.dp else 30.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .then(
                                        if (isSelected) Modifier.border(
                                            2.dp,
                                            onSurface.copy(alpha = 0.7f),
                                            CircleShape
                                        ) else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        Icons.Filled.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {
                    vibrate(context)
                    onSelect(-1)
                }) {
                    Text("恢复默认", color = textSecondary)
                }
                TextButton(onClick = {
                    vibrate(context)
                    onDismiss()
                }) {
                    Text("取消", color = primary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

