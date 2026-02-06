package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.R
import java.util.*

@Composable
fun AnnualMonthRecap(
    fabMonthlyIncomes: List<Double>,
    fabMonthlyPayments: List<Double>,
    sabMonthlyIncomes: List<Double>,
    sabMonthlyPayments: List<Double>
) {
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.monthly_recap),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.month),
                    modifier = Modifier.weight(1.2f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = stringResource(R.string.income),
                    modifier = Modifier.weight(1.3f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.End
                )
                Text(
                    text = stringResource(R.string.expenses),
                    modifier = Modifier.weight(1.3f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.End
                )
                Text(
                    text = stringResource(R.string.net),
                    modifier = Modifier.weight(1.3f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.End
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            (0 until 12).forEach { index ->
                val income = fabMonthlyIncomes.getOrElse(index) { 0.0 } + sabMonthlyIncomes.getOrElse(index) { 0.0 }
                val expenses = fabMonthlyPayments.getOrElse(index) { 0.0 } + sabMonthlyPayments.getOrElse(index) { 0.0 }
                val net = income - expenses

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = months.getOrElse(index) { "" },
                        modifier = Modifier.weight(1.2f),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = String.format("%.2f €", income),
                        modifier = Modifier.weight(1.3f),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = String.format("%.2f €", expenses),
                        modifier = Modifier.weight(1.3f),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = String.format("%.2f €", net),
                        modifier = Modifier.weight(1.3f),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        color = if (net >= 0) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                    )
                }
                if (index < 11) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
