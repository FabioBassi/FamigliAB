package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabiobassi.famigliab.data.Income
import com.fabiobassi.famigliab.data.Payment
import java.util.Calendar

@Composable
fun AnnualGraph(
    payments: List<Payment>,
    incomes: List<Income>
) {
    val monthlyData = remember(payments, incomes) {
        val cal = Calendar.getInstance()
        (0..11).map { month ->
            val monthExpenses = payments.filter {
                cal.time = it.date
                cal.get(Calendar.MONTH) == month
            }.sumOf { it.amount }
            
            // Note: If incomes are not per-month in your data model, 
            // you might need to adjust how they are filtered here.
            // Assuming incomes here are also filtered by month if they have dates, 
            // otherwise using a simplified logic.
            val monthIncomes = incomes.sumOf { it.amount } / 12.0 // Simplified for annual view
            
            monthIncomes - monthExpenses
        }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "ANNUAL TREND",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            val maxVal = (monthlyData.maxOrNull() ?: 1.0).coerceAtLeast(1.0).toFloat()
            val minVal = (monthlyData.minOrNull() ?: 0.0).toFloat()
            val range = maxVal - minVal

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                val width = size.width
                val height = size.height
                val spacing = width / 11f

                val path = Path()
                monthlyData.forEachIndexed { index, value ->
                    val x = index * spacing
                    val y = height - ((value.toFloat() - minVal) / range * height)
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }

                drawPath(
                    path = path,
                    color = Color(0xFF4CAF50), // Green for trend
                    style = Stroke(width = 3.dp.toPx())
                )
                
                // Baseline at zero if in range
                if (minVal < 0 && maxVal > 0) {
                    val zeroY = height - ((0f - minVal) / range * height)
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.3f),
                        start = Offset(0f, zeroY),
                        end = Offset(width, zeroY),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Jan", style = MaterialTheme.typography.labelSmall)
                Text("Jun", style = MaterialTheme.typography.labelSmall)
                Text("Dec", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
