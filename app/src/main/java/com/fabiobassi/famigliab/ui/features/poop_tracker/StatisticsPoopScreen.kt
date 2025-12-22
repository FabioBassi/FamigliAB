package com.fabiobassi.famigliab.ui.features.poop_tracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun StatisticsPoopScreen(
    paddingValues: PaddingValues,
    onSwitchToStandard: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onSwitchToStandard) {
            Text("Back to Standard View")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsPoopScreenPreview() {
    StatisticsPoopScreen(paddingValues = PaddingValues(), onSwitchToStandard = {})
}