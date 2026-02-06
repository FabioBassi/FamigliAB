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
import com.fabiobassi.famigliab.ui.features.poop_tracker.PersonColors
import com.fabiobassi.famigliab.ui.features.poop_tracker.TimeDistribution
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.Fill

@Composable
fun TimeDistributionLineChartCard(
    timeDistribution: TimeDistribution,
    personColors: PersonColors? = null
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        TimeDistributionLineChart(
            timeDistribution = timeDistribution,
            personColors = personColors,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        )
    }
}

@Composable
private fun TimeDistributionLineChart(
    timeDistribution: TimeDistribution,
    personColors: PersonColors?,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(timeDistribution) {
        val fabDist = timeDistribution.fabDistribution
        val sabDist = timeDistribution.sabDistribution

        val xValues = 0..23
        val fabYValues = xValues.map { fabDist[it]?.toFloat() ?: 0f }
        val sabYValues = xValues.map { sabDist[it]?.toFloat() ?: 0f }

        modelProducer.runTransaction {
            lineSeries {
                series(xValues.toList(), fabYValues)
                series(xValues.toList(), sabYValues)
            }
        }
    }

    val fabColor = personColors?.fabColor ?: MaterialTheme.colorScheme.primary
    val sabColor = personColors?.sabColor ?: MaterialTheme.colorScheme.secondary

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.Line(
                        fill = LineCartesianLayer.LineFill.single(Fill(fabColor.toArgb()))
                    ),
                    LineCartesianLayer.Line(
                        fill = LineCartesianLayer.LineFill.single(Fill(sabColor.toArgb()))
                    ),
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                valueFormatter = { _, value, _ -> value.toInt().toString() }
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = { _, value, _ -> "%02d:00".format(value.toInt()) }
            ),
        ),
        modelProducer = modelProducer,
        zoomState = rememberVicoZoomState(initialZoom = Zoom.Content),
        modifier = modifier,
    )
}
