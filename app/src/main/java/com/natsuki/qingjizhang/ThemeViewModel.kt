package com.natsuki.qingjizhang

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.ThemeController

class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val themePreferences = ThemePreferences(application)

    var themeController by mutableStateOf(ThemeController(ColorSchemeMode.MonetSystem))
        private set

    var isLoaded by mutableStateOf(false)
        private set

    var customKeyColorArgb by mutableStateOf(-1)
        private set

    init {
        viewModelScope.launch {
            combine(
                themePreferences.themeModeFlow,
                themePreferences.customKeyColorFlow
            ) { modeInt, argb -> modeInt to argb }
                .collectLatest { (savedMode, argb) ->
                    val mode = when (savedMode) {
                        0 -> ColorSchemeMode.MonetSystem
                        1 -> ColorSchemeMode.MonetLight
                        2 -> ColorSchemeMode.MonetDark
                        else -> ColorSchemeMode.MonetSystem
                    }
                    val keyColor = if (argb == -1) null else Color(argb)
                    themeController = ThemeController(
                        colorSchemeMode = mode,
                        keyColor = keyColor
                    )
                    customKeyColorArgb = argb
                    isLoaded = true
                }
        }
    }

    fun setThemeMode(mode: ColorSchemeMode) {
        val currentKeyColor = themeController.keyColor
        themeController = ThemeController(
            colorSchemeMode = mode,
            keyColor = currentKeyColor
        )
        viewModelScope.launch {
            val modeOrdinal = when (mode) {
                ColorSchemeMode.MonetSystem -> 0
                ColorSchemeMode.MonetLight -> 1
                ColorSchemeMode.MonetDark -> 2
                else -> 0
            }
            themePreferences.saveThemeMode(modeOrdinal)
        }
    }

    fun setCustomKeyColor(argb: Int) {
        viewModelScope.launch {
            themePreferences.saveCustomKeyColor(argb)
        }
    }
}