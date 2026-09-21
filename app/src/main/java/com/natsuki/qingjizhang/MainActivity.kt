package com.natsuki.qingjizhang

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme

class MainActivity : ComponentActivity() {

    private var pendingAction by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pendingAction = intent?.action

        requestHighRefreshRate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val scope = rememberCoroutineScope()
            val prefs = ThemePreferences(applicationContext)

            val hasSeenOnboarding by prefs.hasSeenOnboardingFlow.collectAsState(initial = null)
            val hasAgreedToTerms by prefs.hasAgreedToTermsFlow.collectAsState(initial = null)

            MiuixTheme(controller = themeViewModel.themeController) {
                val currentMode = themeViewModel.themeController.colorSchemeMode
                val isDark = when (currentMode) {
                    ColorSchemeMode.Light, ColorSchemeMode.MonetLight -> false
                    ColorSchemeMode.Dark, ColorSchemeMode.MonetDark -> true
                    else -> androidx.compose.foundation.isSystemInDarkTheme()
                }
                val appColors = AppColors.get(isDark)

                CompositionLocalProvider(AppColors.Local provides appColors) {
                    val seenOnboarding = hasSeenOnboarding
                    val agreedTerms = hasAgreedToTerms

                    if (seenOnboarding == null || agreedTerms == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MiuixTheme.colorScheme.surface)
                        )
                        return@CompositionLocalProvider
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        HomeScreen(
                            initialAction = pendingAction,
                            onActionConsumed = { pendingAction = null }
                        )

                        AnimatedVisibility(
                            visible = !seenOnboarding,
                            enter = EnterTransition.None,
                            exit = slideOutVertically(
                                targetOffsetY = { -it / 3 },
                                animationSpec = tween(450, easing = FastOutSlowInEasing)
                            ) + fadeOut(tween(400, easing = FastOutSlowInEasing))
                        ) {
                            OnboardingScreen(
                                onFinish = {
                                    scope.launch {
                                        prefs.setHasSeenOnboarding(true)
                                    }
                                }
                            )
                        }

                        AnimatedVisibility(
                            visible = seenOnboarding && !agreedTerms,
                            enter = EnterTransition.None,
                            exit = slideOutVertically(
                                targetOffsetY = { -it / 3 },
                                animationSpec = tween(450, easing = FastOutSlowInEasing)
                            ) + fadeOut(tween(400, easing = FastOutSlowInEasing))
                        ) {
                            AgreementScreen(
                                onAgree = {
                                    scope.launch {
                                        prefs.setHasAgreedToTerms(true)
                                    }
                                },
                                onReject = {
                                    finish()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingAction = intent.action
    }

    private fun requestHighRefreshRate() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    display
                } else {
                    @Suppress("DEPRECATION")
                    windowManager.defaultDisplay
                }

                if (display != null) {
                    val modes = display.supportedModes
                    val highestRefreshMode = modes.maxByOrNull { it.refreshRate }
                    if (highestRefreshMode != null) {
                        val attrs = window.attributes
                        attrs.preferredDisplayModeId = highestRefreshMode.modeId
                        window.attributes = attrs
                    }
                }
            }
        } catch (e: Exception) {

        }
    }
}