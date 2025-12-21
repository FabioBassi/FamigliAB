package com.fabiobassi.famigliab.ui.features.poop_tracker.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.ui.features.poop_tracker.PoopChartData
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
fun PoopChartCard(
    poopChartData: PoopChartData?,
) {
    if (poopChartData == null) return
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }

    val poopEntriesByDay = poopChartData.entriesByDay

    LaunchedEffect(poopEntriesByDay) {
        if (poopEntriesByDay.isNotEmpty()) {
            val entries = poopEntriesByDay.map { (_, entries) ->
                entries.values.mapIndexed { index, value ->
                    entryOf(index.toFloat(), value.toFloat())
                }
            }
            chartEntryModelProducer.setEntries(entries)
        }
    }

    // X axis: show only day number
    val bottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            poopEntriesByDay.values
                .firstOrNull()
                ?.keys
                ?.elementAtOrNull(value.toInt())
                ?.substringBefore("/") // dd/MM/yyyy → dd
                ?: ""
        }

    // Y axis: integers only
    val startAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
            value.toInt().toString()
        }

    val lineSpec = listOf(
        LineChart.LineSpec(lineColor = poopChartData.fabColor.hashCode()),
        LineChart.LineSpec(lineColor = poopChartData.sabColor.hashCode()),
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 8.dp)
    ) {
        if (poopEntriesByDay.isNotEmpty()) {
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
            )
        }
    }
}
