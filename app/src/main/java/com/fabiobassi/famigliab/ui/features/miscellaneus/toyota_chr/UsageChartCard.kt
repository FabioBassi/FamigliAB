package com.fabiobassi.famigliab.ui.features.miscellaneus.toyota_chr

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.ChrUsage
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill

@Composable
fun UsageChartCard(entries: List<ChrUsage>) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        UsageLineChart(
            entries = entries,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp)
        )
    }
}

@Composable
private fun UsageLineChart(
    entries: List<ChrUsage>,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(entries) {
        // Find the pivot week (the last one with a value, or the first one)
        val lastActualEntry = entries.lastOrNull { it.actualKm != null }
        val pivotWeek = lastActualEntry?.week ?: entries.firstOrNull()?.week ?: 1

        // Filter for previous 3 and next 5 weeks
        val windowEntries = entries.filter { it.week in (pivotWeek - 3)..(pivotWeek + 5) }

        val weeks = windowEntries.map { it.week }
        val expected = windowEntries.map { it.expectedKm }
        
        // Filter out null entries for the actual series to avoid NaN issues
        val actualEntries = windowEntries.filter { it.actualKm != null }
        val actualWeeks = actualEntries.map { it.week }
        val actualValues = actualEntries.map { it.actualKm!!.toDouble() }

        modelProducer.runTransaction {
            lineSeries {
                series(weeks, expected)
                if (actualValues.isNotEmpty()) {
                    series(actualWeeks, actualValues)
                }
            }
        }
    }

    val expectedColor = MaterialTheme.colorScheme.primary
    val actualColor = MaterialTheme.colorScheme.secondary

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.Line(fill = LineCartesianLayer.LineFill.single(Fill(expectedColor.toArgb()))),
                    LineCartesianLayer.Line(fill = LineCartesianLayer.LineFill.single(Fill(actualColor.toArgb())))
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = { _, value, _ -> "${value.toInt()} km" }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                title = "Week"
            ),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}
