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
import com.fabiobassi.famigliab.ui.features.poop_tracker.AverageMonthlyPoopChartData
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf
import java.text.DecimalFormat

@Composable
fun AverageMonthlyPoopChartCard(
    averageMonthlyPoopChartData: AverageMonthlyPoopChartData?,
    onChangeYear: (Int) -> Unit,
) {
    if (averageMonthlyPoopChartData == null) return
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }

    val monthlyPoopEntries = averageMonthlyPoopChartData.entries

    LaunchedEffect(monthlyPoopEntries) {
        if (monthlyPoopEntries.isNotEmpty()) {
            val entries = monthlyPoopEntries.map { (_, entries) ->
                entries.mapIndexed { index, value ->
                    entryOf(index.toFloat(), value.second)
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

    val decimalFormat = remember { DecimalFormat("#.##") }

    val startAxisValueFormatter =
        AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
            decimalFormat.format(value)
        }

    val lineSpec = mutableListOf<LineChart.LineSpec>()
    averageMonthlyPoopChartData.entries.keys.forEach { person ->
        val color = when (person) {
            Person.FAB -> averageMonthlyPoopChartData.fabColor
            Person.SAB -> averageMonthlyPoopChartData.sabColor
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
                    text = stringResource(R.string.daily_average_month),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onChangeYear(averageMonthlyPoopChartData.year - 1) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Previous year", tint = MaterialTheme.colorScheme.primary)
                    }
                    Text(
                        text = averageMonthlyPoopChartData.year.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { onChangeYear(averageMonthlyPoopChartData.year + 1) }) {
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
