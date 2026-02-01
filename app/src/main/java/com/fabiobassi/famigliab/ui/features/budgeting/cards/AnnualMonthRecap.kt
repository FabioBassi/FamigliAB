package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabiobassi.famigliab.data.Income
import com.fabiobassi.famigliab.data.Payment
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

@Composable
fun AnnualMonthRecap(
    payments: List<Payment>,
    incomes: List<Income>
) {
    val monthNames = remember { DateFormatSymbols(Locale.getDefault()).months }
    val monthlyRecap = remember(payments, incomes) {
        val cal = Calendar.getInstance()
        (0..11).map { month ->
            val monthExpenses = payments.filter {
                cal.time = it.date
                cal.get(Calendar.MONTH) == month
            }.sumOf { it.amount }
            
            // Simplified income distribution if not per-month
            val monthIncomes = incomes.sumOf { it.amount } / 12.0 
            
            Triple(monthNames[month], monthIncomes, monthExpenses)
        }.filter { it.second > 0 || it.third > 0 }
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
                text = "MONTHLY RECAP",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Column {
                monthlyRecap.forEachIndexed { index, (month, income, expense) ->
                    MonthRecapRow(
                        month = month,
                        income = income,
                        expense = expense
                    )
                    if (index < monthlyRecap.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthRecapRow(
    month: String,
    income: Double,
    expense: Double
) {
    val net = income - expense
    val netColor = if (net >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = month.replaceFirstChar { it.titlecase() },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${"%.2f".format(net)} €",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = netColor
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "In: ${"%.2f".format(income)} €",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Out: ${"%.2f".format(expense)} €",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
