package com.fabiobassi.famigliab.ui.features.poop_tracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.ui.features.poop_tracker.charts.PoopChartCard

@Composable
fun StandardPoopScreen(
    paddingValues: PaddingValues,
    onSwitchToStatistics: () -> Unit,
) {
    val viewModel: PoopTrackerViewModel = viewModel(factory = PoopTrackerViewModel.Factory)
    val poopEntries by viewModel.poopEntries.collectAsState()
    val poopChartData by viewModel.poopChartData.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var dayOffset by remember { mutableIntStateOf(0) }

    LaunchedEffect(dayOffset) {
        viewModel.loadPoopEntries(dayOffset)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            poopChartData?.let { chartData ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    poopEntries.lastOrNull()?.date?.let {
                        Text(
                            text = "Poops since: $it",
                            textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "Fab: ${poopEntries.count { it.person == Person.FAB }}",
                            color = chartData.fabColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                        )
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "Sab: ${poopEntries.count { it.person == Person.SAB }}",
                            color = chartData.sabColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                        )
                    }
                }
            }
            Text(
                text = "Last seven days",
                textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
            PoopChartCard(poopChartData = poopChartData)
            Slider(
                value = (30f - dayOffset.toFloat()),
                onValueChange = { dayOffset = (30 - it.toInt()) },
                valueRange = 0f..30f,
                steps = 29,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Button(onClick = onSwitchToStatistics) {
                Text("View general statistics and graphs")
            }
            LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                items(poopEntries) { entry ->
                    PoopEntryItem(entry = entry, onDelete = { viewModel.deletePoopEntry(entry) })
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = { showDialog = true }) {
            Icon(Icons.Filled.Add, contentDescription = "Add poop")
        }

        if (showDialog) {
            AddPoopDialog(
                onDismiss = { showDialog = false },
                onSave = { date, hour, quality, person ->
                    viewModel.addPoopEntry(date, hour, quality, person)
                    showDialog = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StandardPoopScreenPreview() {
    StandardPoopScreen(PaddingValues(), onSwitchToStatistics = {})
}
