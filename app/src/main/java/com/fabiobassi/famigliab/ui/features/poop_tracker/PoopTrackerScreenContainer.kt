package com.fabiobassi.famigliab.ui.features.poop_tracker

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PoopTrackerScreenContainer(paddingValues: PaddingValues) {
    var showStatistics by remember { mutableStateOf(false) }

    if (showStatistics) {
        StatisticsPoopScreen(
            paddingValues = paddingValues,
            onSwitchToStandard = { showStatistics = false }
        )
    } else {
        StandardPoopScreen(
            paddingValues = paddingValues,
            onSwitchToStatistics = { showStatistics = true }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PoopTrackerScreenContainerPreview() {
    PoopTrackerScreenContainer(PaddingValues())
}
