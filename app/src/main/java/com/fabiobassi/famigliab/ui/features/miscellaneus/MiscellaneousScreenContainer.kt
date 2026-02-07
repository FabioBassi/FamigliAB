package com.fabiobassi.famigliab.ui.features.miscellaneus

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.fabiobassi.famigliab.ui.features.miscellaneus.toyota_chr.ChrUsageScreen

@Composable
fun MiscellaneousScreenContainer(paddingValues: PaddingValues,) {
    var screenToDisplay by remember { mutableIntStateOf(0) }

    when (screenToDisplay) {
        0 -> MiscellaneousSelectionScreen(
            paddingValues = paddingValues,
            onSelectionChange = { value -> screenToDisplay = value }
        )
        1 -> ChrUsageScreen(
            paddingValues = paddingValues,
            onBackToSelection = {screenToDisplay = 0}
        )
    }

}