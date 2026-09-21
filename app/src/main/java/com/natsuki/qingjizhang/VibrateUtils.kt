package com.natsuki.qingjizhang

import android.content.Context

fun vibrate(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE)
            as? android.os.Vibrator ?: return

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        vibrator.vibrate(
            android.os.VibrationEffect.createOneShot(
                40,
                android.os.VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(40)
    }
}

fun vibrateEdge(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE)
            as? android.os.Vibrator ?: return

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        vibrator.vibrate(
            android.os.VibrationEffect.createOneShot(
                55,
                android.os.VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(55)
    }
}