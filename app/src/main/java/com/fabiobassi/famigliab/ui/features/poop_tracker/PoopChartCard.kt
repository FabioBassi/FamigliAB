package com.fabiobassi.famigliab.ui.features.poop_tracker

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.Person
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

@Composable
fun PoopChartCard(poopEntriesByDay: Map<Person, Map<String, Int>>) {
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }

    LaunchedEffect(poopEntriesByDay) {
        if (poopEntriesByDay.isNotEmpty()) {
            val entries = poopEntriesByDay.map { (person, entries) ->
                entries.values.mapIndexed { index, value ->
                    entryOf(index.toFloat(), value.toFloat())
                }
            }
            chartEntryModelProducer.setEntries(entries)
        }
    }

    val bottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            poopEntriesByDay.values.firstOrNull()?.keys?.elementAtOrNull(value.toInt()) ?: ""
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 8.dp)
    ) {
        if (poopEntriesByDay.isNotEmpty()) {
            Chart(
                chart = lineChart(),
                chartModelProducer = chartEntryModelProducer,
                startAxis = startAxis(),
                bottomAxis = bottomAxis(
                    valueFormatter = bottomAxisValueFormatter,
                ),
            )
        }
    }
}
