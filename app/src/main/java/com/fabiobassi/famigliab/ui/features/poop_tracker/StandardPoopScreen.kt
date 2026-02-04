package com.fabiobassi.famigliab.ui.features.poop_tracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.ui.features.poop_tracker.charts.MonthPoopChartCard
import com.fabiobassi.famigliab.ui.features.poop_tracker.dialogs.AddPoopDialog
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun StandardPoopScreen(
    paddingValues: PaddingValues,
    onSwitchToStatistics: () -> Unit,
) {
    val viewModel: PoopTrackerViewModel = viewModel(factory = PoopTrackerViewModel.Factory)
    val poopEntries by viewModel.poopEntries.collectAsState()
    val poopData by viewModel.poopData.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }

    LaunchedEffect(displayedMonth) {
        viewModel.loadPoopEntries(month = displayedMonth)
    }

    val currentMonthEntries = remember(poopEntries, displayedMonth) {
        poopEntries.filter {
            try {
                val entryDate = LocalDate.parse(it.date, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                entryDate.monthValue == displayedMonth.monthValue && entryDate.year == displayedMonth.year
            } catch (_: Exception) {
                false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Month Selector Header
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = { displayedMonth = displayedMonth.minusMonths(1) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous month"
                            )
                        }
                        Text(
                            text = displayedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        IconButton(onClick = { displayedMonth = displayedMonth.plusMonths(1) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next month"
                            )
                        }
                    }
                }
            }

            // Summary Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val fabCount = currentMonthEntries.count { it.person == Person.FAB }
                    val sabCount = currentMonthEntries.count { it.person == Person.SAB }

                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        label = "FAB",
                        count = fabCount,
                        color = poopData?.fabColor ?: MaterialTheme.colorScheme.primary
                    )
                    SummaryCard(
                        modifier = Modifier.weight(1f),
                        label = "SAB",
                        count = sabCount,
                        color = poopData?.sabColor ?: MaterialTheme.colorScheme.secondary
                    )
                }
            }

            item{
                MonthPoopChartCard()
            }

            // Navigation Button
            item {
                OutlinedButton(
                    onClick = onSwitchToStatistics,
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(stringResource(R.string.poop_statistics_button))
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.month_entries),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Entries List
            if (currentMonthEntries.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_entries_month),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                items(currentMonthEntries) { entry ->
                    PoopEntryItem(entry = entry, onDelete = { viewModel.deletePoopEntry(entry) })
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(72.dp)) // Space for FAB
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 16.dp, vertical = 32.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
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

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    label: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            color.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = color
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StandardPoopScreenPreview() {
    StandardPoopScreen(PaddingValues(), onSwitchToStatistics = {})
}
