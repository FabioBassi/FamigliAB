package com.fabiobassi.famigliab.ui.features.poop_tracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.ui.features.poop_tracker.charts.MonthAveragePoopGraphCard
import com.fabiobassi.famigliab.ui.features.poop_tracker.charts.MonthRecapTable

@Composable
fun StatisticsPoopScreen(
    paddingValues: PaddingValues,
    onSwitchToStandard: () -> Unit,
    viewModel: PoopTrackerViewModel = viewModel(factory = PoopTrackerViewModel.Factory),
) {
    val recapData by viewModel.recapData.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val poopData by viewModel.poopData.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            TextButton(
                onClick = onSwitchToStandard,
                contentPadding = PaddingValues(0.dp),
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                Text(stringResource(R.string.go_back), modifier = Modifier.padding(start = 8.dp))
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = stringResource(R.string.monthly_summary_table_title).uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = { viewModel.updateSelectedYear(selectedYear - 1) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowLeft, contentDescription = "previous year")
                    }
                    Text(
                        text = selectedYear.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                    )
                    IconButton(onClick = { viewModel.updateSelectedYear(selectedYear + 1) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowRight, contentDescription = "next year")
                    }
                }
                MonthAveragePoopGraphCard(
                    recapData = recapData,
                    poopData = poopData,
                )
                MonthRecapTable(
                    recapData = recapData
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsPoopScreenPreview() {
    StatisticsPoopScreen(paddingValues = PaddingValues(), onSwitchToStandard = {})
}
