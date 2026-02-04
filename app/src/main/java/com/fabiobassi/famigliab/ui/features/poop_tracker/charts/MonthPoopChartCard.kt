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
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.PoopEntry
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun MonthPoopChartCard(
    entries: List<PoopEntry>,
    month: YearMonth,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        PoopLineChart(
            entries = entries,
            month = month,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        )
    }
}

@Composable
private fun PoopLineChart(
    entries: List<PoopEntry>,
    month: YearMonth,
    modifier: Modifier = Modifier,
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(entries, month) {
        val daysInMonth = month.lengthOfMonth()
        val days = (1..daysInMonth).toList()

        val fabCounts = IntArray(daysInMonth)
        val sabCounts = IntArray(daysInMonth)

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        entries.forEach { entry ->
            try {
                val date = LocalDate.parse(entry.date, formatter)
                val dayIdx = date.dayOfMonth - 1
                if (dayIdx in 0 until daysInMonth) {
                    if (entry.person == Person.FAB) {
                        fabCounts[dayIdx]++
                    } else if (entry.person == Person.SAB) {
                        sabCounts[dayIdx]++
                    }
                }
            } catch (e: Exception) {
                // Ignore malformed dates
            }
        }

        modelProducer.runTransaction {
            lineSeries {
                series(days, fabCounts.toList())
                series(days, sabCounts.toList())
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}
