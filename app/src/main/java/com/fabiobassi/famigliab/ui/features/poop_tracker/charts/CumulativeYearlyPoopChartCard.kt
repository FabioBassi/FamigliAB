package com.fabiobassi.famigliab.ui.features.poop_tracker.charts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.ui.features.poop_tracker.CumulativePoopChartData
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

@Composable
fun CumulativeYearlyPoopChartCard(
    cumulativePoopChartData: CumulativePoopChartData?,
    onChangeYear: (Int) -> Unit,
) {
    if (cumulativePoopChartData == null) return
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }

    val cumulativePoopEntries = cumulativePoopChartData.entries

    LaunchedEffect(cumulativePoopEntries) {
        if (cumulativePoopEntries.isNotEmpty()) {
            val entries = cumulativePoopEntries.map { (_, entries) ->
                entries.mapIndexed { index, value ->
                    entryOf(index.toFloat(), value.second.toFloat())
                }
            }
            chartEntryModelProducer.setEntries(entries)
        }
    }

    val bottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            cumulativePoopEntries.values
                .firstOrNull()
                ?.elementAtOrNull(value.toInt())
                ?.first
                ?.substringBefore(" ") // "MMM yyyy" -> "MMM"
                ?: ""
        }

    val startAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
            value.toInt().toString()
        }

    val lineSpec = mutableListOf<LineChart.LineSpec>()
    cumulativePoopChartData.entries.keys.forEach { person ->
        val color = when (person) {
            Person.FAB -> cumulativePoopChartData.fabColor.hashCode()
            Person.SAB -> cumulativePoopChartData.sabColor.hashCode()
        }
        lineSpec.add(LineChart.LineSpec(lineColor = color))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (cumulativePoopEntries.isNotEmpty()) {
                Chart(
                    chart = lineChart(
                        lines = lineSpec,
                    ),
                    chartModelProducer = chartEntryModelProducer,
                    startAxis = startAxis(
                        valueFormatter = startAxisValueFormatter,
                    ),
                    bottomAxis = bottomAxis(
                        valueFormatter = bottomAxisValueFormatter,
                    ),
                    modifier = Modifier.height(200.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onChangeYear(cumulativePoopChartData.year - 1) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous year")
                }
                Text(
                    text = cumulativePoopChartData.year.toString(),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = { onChangeYear(cumulativePoopChartData.year + 1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next year")
                }
            }
        }
    }
}