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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabiobassi.famigliab.R


@Composable
fun AnnualSummaryCard(
    totalIncomes: List<Double>, //0 = total, 1 = Fab, 2 = Sab
    totalExpenses: List<Double>, //0 = total, 1 = Fab, 2 = Sab
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummarySection(
            title = stringResource(R.string.annual_incomes),
            fabValue = totalIncomes[1],
            sabValue = totalIncomes[2],
            totalValue = totalIncomes[0],
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onPrimaryContainer
        )

        SummarySection(
            title = stringResource(R.string.annual_expenses),
            fabValue = totalExpenses[1],
            sabValue = totalExpenses[2],
            totalValue = totalExpenses[0],
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        SummarySection(
            title = stringResource(R.string.annual_net),
            fabValue = totalIncomes[1] - totalExpenses[1],
            sabValue = totalIncomes[2] - totalExpenses[2],
            totalValue = totalIncomes[0] - totalExpenses[0],
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

@Composable
private fun SummarySection(
    title: String,
    fabValue: Double,
    sabValue: Double,
    totalValue: Double,
    containerColor: Color,
    onContainerColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = onContainerColor
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                SummaryRow(label = "Fab", value = fabValue, onContainerColor = onContainerColor)
                SummaryRow(label = "Sab", value = sabValue, onContainerColor = onContainerColor)
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp,
                color = onContainerColor.copy(alpha = 0.12f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = stringResource(R.string.total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = onContainerColor
                )
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${"%.2f".format(totalValue)} €",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = onContainerColor
                    )
                    Text(
                        text = "avg: ${"%.2f".format(totalValue / 12.0)} €/M",
                        style = MaterialTheme.typography.labelSmall,
                        color = onContainerColor
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: Double, onContainerColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp,
            color = onContainerColor.copy(alpha = 0.8f)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${"%.2f".format(value)} €",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                color = onContainerColor.copy(alpha = 0.8f)
            )
            Text(
                text = " (${"%.2f".format(value / 12.0)} €)",
                style = MaterialTheme.typography.bodySmall,
                color = onContainerColor.copy(alpha = 0.6f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnnualSummaryCardPreview() {
    AnnualSummaryCard(
        totalIncomes = listOf(50000.0, 30000.0, 20000.0),
        totalExpenses = listOf(40000.0, 20000.0, 20000.0)
    )
}
