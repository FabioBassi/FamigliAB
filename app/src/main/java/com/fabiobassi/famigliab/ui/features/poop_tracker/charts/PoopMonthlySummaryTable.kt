package com.fabiobassi.famigliab.ui.features.poop_tracker.charts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "MONTHLY SUMMARY",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Month",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Fab",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = monthlyPoopChartData.fabColor
                )
                Text(
                    text = "Sab",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = monthlyPoopChartData.sabColor
                )
            }
            
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Data rows
            tableData.forEachIndexed { index, (month, fabCount, sabCount) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = month,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = fabCount.toString(),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = sabCount.toString(),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
