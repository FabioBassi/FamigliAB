package com.fabiobassi.famigliab.ui.features.poop_tracker.charts

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
import com.fabiobassi.famigliab.ui.features.poop_tracker.MonthlyStats
import com.fabiobassi.famigliab.ui.features.poop_tracker.PoopData
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.component.LineComponent
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthAveragePoopGraphCard(
    recapData: List<Pair<Month, MonthlyStats>>,
    poopData: PoopData? = null
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        AveragePoopColumnChart(
            recapData = recapData,
            poopData = poopData,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        )
    }
}

@Composable
private fun AveragePoopColumnChart(
    recapData: List<Pair<Month, MonthlyStats>>,
    poopData: PoopData?,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(recapData) {
        if (recapData.isEmpty()) return@LaunchedEffect

        val xValues = recapData.map { it.first.value - 1 }
        val fabYValues = recapData.map { it.second.fabAvg.toFloat() }
        val sabYValues = recapData.map { it.second.sabAvg.toFloat() }

        modelProducer.runTransaction {
            columnSeries {
                series(xValues, fabYValues)
                series(xValues, sabYValues)
            }
        }
    }

    val fabColor = poopData?.fabColor ?: MaterialTheme.colorScheme.primary
    val sabColor = poopData?.sabColor ?: MaterialTheme.colorScheme.secondary
    val months = remember {
        Month.entries.map { it.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }
    }

    if (recapData.isNotEmpty()) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer.ColumnProvider.series(
                        LineComponent(Fill(fabColor.toArgb()), thicknessDp = 8f),
                        LineComponent(Fill(sabColor.toArgb()), thicknessDp = 8f)
                    )
                ),
                startAxis = VerticalAxis.rememberStart(
                    valueFormatter = { _, value, _ -> "%.1f".format(value) }
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { _, value, _ -> months.getOrNull(value.toInt()) ?: "" }
                ),
            ),
            modelProducer = modelProducer,
            modifier = modifier,
        )
    }
}
