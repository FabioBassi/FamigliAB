package com.fabiobassi.famigliab.ui.features.poop_tracker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.ui.features.poop_tracker.charts.CumulativePoopChartCard

@Composable
fun StatisticsPoopScreen(
    paddingValues: PaddingValues,
    onSwitchToStandard: () -> Unit,
    viewModel: PoopTrackerViewModel = viewModel(factory = PoopTrackerViewModel.Factory),
) {
    val cumulativePoopChartData by viewModel.cumulativePoopChartData.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.cumulative_poop_chart_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        CumulativePoopChartCard(
            cumulativePoopChartData = cumulativePoopChartData,
            onChangeYear = viewModel::changeYear,
        )
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
