package com.natsuki.qingjizhang

import kotlin.math.abs
import kotlin.math.roundToLong
fun formatAmount(value: Double): String {
    val cents = (value * 100).roundToLong()
    val negative = cents < 0
    val absCents = abs(cents)
    val yuan = absCents / 100
    val fraction = absCents % 100
    val sign = if (negative) "-" else ""
    return if (fraction == 0L) {
        "$sign$yuan"
    } else {
        "$sign$yuan.${fraction.toString().padStart(2, '0')}"
    }
}

fun formatAmountSigned(value: Double): String {
    return if (value > 0) "+${formatAmount(value)}" else formatAmount(value)
}

