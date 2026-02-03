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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.ui.features.poop_tracker.MonthlyPoopChartData
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
fun MonthlyPoopChartCard(
    monthlyPoopChartData: MonthlyPoopChartData?,
    onChangeYear: (Int) -> Unit,
) {
    if (monthlyPoopChartData == null) return
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }

    val monthlyPoopEntries = monthlyPoopChartData.entries

    LaunchedEffect(monthlyPoopEntries) {
        if (monthlyPoopEntries.isNotEmpty()) {
            val entries = monthlyPoopEntries.map { (_, entries) ->
                entries.mapIndexed { index, value ->
                    entryOf(index.toFloat(), value.second.toFloat())
                }
            }
            chartEntryModelProducer.setEntries(entries)
        }
    }

    val bottomAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            monthlyPoopEntries.values
                .firstOrNull()
                ?.elementAtOrNull(value.toInt())
                ?.first
                ?.substringBefore(" ") ?: ""
        }

    val startAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
            value.toInt().toString()
        }

    val lineSpec = mutableListOf<LineChart.LineSpec>()
    monthlyPoopChartData.entries.keys.forEach { person ->
        val color = when (person) {
            Person.FAB -> monthlyPoopChartData.fabColor
            Person.SAB -> monthlyPoopChartData.sabColor
        }
        lineSpec.add(LineChart.LineSpec(lineColor = color.toArgb()))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.monthly_frequency),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onChangeYear(monthlyPoopChartData.year - 1) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Previous year", tint = MaterialTheme.colorScheme.primary)
                    }
                    Text(
                        text = monthlyPoopChartData.year.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { onChangeYear(monthlyPoopChartData.year + 1) }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next year", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            if (monthlyPoopEntries.isNotEmpty()) {
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
                    modifier = Modifier.height(180.dp).padding(top = 16.dp)
                )
            }
        }
    }
}
