package com.natsuki.qingjizhang

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing

object MotionScheme {

    val EasingEnter: Easing = CubicBezierEasing(0.1f, 0.8f, 0.2f, 1f)

    val EasingExit: Easing = CubicBezierEasing(0.4f, 0f, 0.7f, 0.2f)

    val EasingStandard: Easing = CubicBezierEasing(0.25f, 0.1f, 0.25f, 1f)

    val EasingLinear: Easing = CubicBezierEasing(0f, 0f, 1f, 1f)

    const val DurationShort  = 180   // 150 → 180
    const val DurationMedium = 320   // 250 → 320
    const val DurationLong   = 500   // 400 → 500


    const val SpringDampingRatio = 0.82f
    const val SpringStiffness = 480f


    const val SpringSoftDampingRatio = 0.9f
    const val SpringSoftStiffness = 300f
}

