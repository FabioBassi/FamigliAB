package com.fabiobassi.famigliab.ui.features.poop_tracker.charts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.ui.features.poop_tracker.MonthlyPoopChartData

@Composable
fun PoopMonthlySummaryTable(
    monthlyPoopChartData: MonthlyPoopChartData?,
) {
    if (monthlyPoopChartData == null) return

    val fabEntries = monthlyPoopChartData.entries[Person.FAB] ?: emptyList()
    val sabEntries = monthlyPoopChartData.entries[Person.SAB] ?: emptyList()

    val tableData = fabEntries.mapIndexed { index, fabPair ->
        val month = fabPair.first.substringBefore(" ") // "Jan 2023" -> "Jan"
        val fabCount = fabPair.second
        val sabCount = sabEntries.getOrNull(index)?.second ?: 0
        Triple(month, fabCount, sabCount)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(text = "Month", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text(text = "Fab", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
                Text(text = "Sab", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            }

            // Data rows
            tableData.forEach { (month, fabCount, sabCount) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = month, modifier = Modifier.weight(1f))
                    Text(text = fabCount.toString(), modifier = Modifier.weight(1f))
                    Text(text = sabCount.toString(), modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
