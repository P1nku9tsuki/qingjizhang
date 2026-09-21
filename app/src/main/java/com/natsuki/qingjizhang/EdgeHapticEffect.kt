package com.natsuki.qingjizhang

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.platform.LocalContext

@Composable
fun EdgeHapticEffect(listState: LazyListState) {
    val context = LocalContext.current

    LaunchedEffect(listState) {
        var lastAtTop = true
        var lastAtBottom = false

        snapshotFlow {
            Triple(
                !listState.canScrollBackward,
                !listState.canScrollForward,
                listState.isScrollInProgress
            )
        }.collect { (atTop, atBottom, scrolling) ->
            if (scrolling) {
                if (atTop && !lastAtTop) vibrateEdge(context)
                if (atBottom && !lastAtBottom) vibrateEdge(context)
            }
            lastAtTop = atTop
            lastAtBottom = atBottom
        }
    }
}

