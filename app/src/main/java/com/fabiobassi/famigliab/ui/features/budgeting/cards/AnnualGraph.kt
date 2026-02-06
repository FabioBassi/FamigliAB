package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.SettingsDataStore
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
fun AnnualGraph(
    fabMonthlyIncomes: List<Double>,
    fabMonthlyPayments: List<Double>,
    sabMonthlyIncomes: List<Double>,
    sabMonthlyPayments: List<Double>
) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val fabColorHex by settingsDataStore.getColorFor(Person.FAB.name)
        .collectAsState(initial = "")
    val fabColor = if (fabColorHex.isNotEmpty()) {
        Color(fabColorHex.toColorInt())
    } else {
        MaterialTheme.colorScheme.primary
    }
    val sabColorHex by settingsDataStore.getColorFor(Person.SAB.name)
        .collectAsState(initial = "")
    val sabColor = if (sabColorHex.isNotEmpty()) {
        Color(sabColorHex.toColorInt())
    } else {
        MaterialTheme.colorScheme.secondary
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    val chartData = remember(fabMonthlyIncomes, fabMonthlyPayments, sabMonthlyIncomes, sabMonthlyPayments) {
        val totalNet = mutableListOf<Double>()
        val fabNet = mutableListOf<Double>()
        val sabNet = mutableListOf<Double>()

        for (i in 0 until 12) {
            val fNet = fabMonthlyIncomes.getOrElse(i) { 0.0 } - fabMonthlyPayments.getOrElse(i) { 0.0 }
            val sNet = sabMonthlyIncomes.getOrElse(i) { 0.0 } - sabMonthlyPayments.getOrElse(i) { 0.0 }
            
            fabNet.add(fNet)
            sabNet.add(sNet)
            totalNet.add(fNet + sNet)
        }
        Triple(totalNet, fabNet, sabNet)
    }

    LaunchedEffect(chartData) {
        modelProducer.runTransaction {
            lineSeries {
                series(chartData.first)
                series(chartData.second)
                series(chartData.third)
            }
        }
    }

    val months = listOf(
        stringResource(R.string.jan),
        stringResource(R.string.feb),
        stringResource(R.string.mar),
        stringResource(R.string.apr),
        stringResource(R.string.may),
        stringResource(R.string.jun),
        stringResource(R.string.jul),
        stringResource(R.string.aug),
        stringResource(R.string.sep),
        stringResource(R.string.oct),
        stringResource(R.string.nov),
        stringResource(R.string.dec)
    )

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.annual_trend),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            CartesianChartHost(
                chart = rememberCartesianChart(
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.Line(
                                fill = LineCartesianLayer.LineFill.single(Fill(MaterialTheme.colorScheme.tertiary.toArgb())) // Total
                            ),
                            LineCartesianLayer.Line(
                                fill = LineCartesianLayer.LineFill.single(Fill(fabColor.toArgb())) // Fab
                            ),
                            LineCartesianLayer.Line(
                                fill = LineCartesianLayer.LineFill.single(Fill(sabColor.toArgb())) // Sab
                            )
                        )
                    ),
                    startAxis = VerticalAxis.rememberStart(),
                    bottomAxis = HorizontalAxis.rememberBottom(
                        valueFormatter = { _, value, _ ->
                            months.getOrNull(value.toInt()) ?: ""
                        },
                        itemPlacer = HorizontalAxis.ItemPlacer.aligned(spacing = { 3 })
                    ),
                ),
                modelProducer = modelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}
