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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PoopTrackerScreen(paddingValues: PaddingValues) {
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
            Text(text = "Poop Tracker", fontSize = 24.sp)
            PoopChartCard(poopChartData = poopChartData)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Days ago: $dayOffset")
                Slider(
                    value = dayOffset.toFloat(),
                    onValueChange = { dayOffset = it.toInt() },
                    valueRange = 0f..30f,
                    steps = 29,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
            LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                items(poopEntries) { entry ->
                    PoopEntryItem(entry = entry)
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
                onSave = { date, hour, quantity, quality, person ->
                    viewModel.addPoopEntry(date, hour, quantity, quality, person)
                    showDialog = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PoopTrackerScreenPreview() {
    PoopTrackerScreen(PaddingValues())
}
