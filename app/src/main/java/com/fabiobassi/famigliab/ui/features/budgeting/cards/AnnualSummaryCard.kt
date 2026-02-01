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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.fabiobassi.famigliab.data.SettingsDataStore

@Composable
fun AnnualSummaryCard(
    totalIncomes: List<Double>, //0 = total, 1 = Fab, 2 = Sab
    totalExpenses: List<Double>, //0 = total, 1 = Fab, 2 = Sab
) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val fabColorHex by settingsDataStore.getColorFor("Fab").collectAsState(initial = "")
    val sabColorHex by settingsDataStore.getColorFor("Sab").collectAsState(initial = "")

    val fabColor = remember(fabColorHex) {
        try {
            if (fabColorHex.isNotEmpty()) {
                Color(fabColorHex.toColorInt())
            } else {
                Color.Unspecified
            }
        } catch (e: Exception) {
            Color.Unspecified
        }
    }
    val sabColor = remember(sabColorHex) {
        try {
            if (sabColorHex.isNotEmpty()) {
                Color(sabColorHex.toColorInt())
            } else {
                Color.Unspecified
            }
        } catch (e: Exception) {
            Color.Unspecified
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummarySection(
            title = "ANNUAL INCOME",
            fabValue = totalIncomes[1],
            sabValue = totalIncomes[2],
            totalValue = totalIncomes[0],
            fabColor = fabColor,
            sabColor = sabColor,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onPrimaryContainer
        )

        SummarySection(
            title = "ANNUAL EXPENSES",
            fabValue = totalExpenses[1],
            sabValue = totalExpenses[2],
            totalValue = totalExpenses[0],
            fabColor = fabColor,
            sabColor = sabColor,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onSecondaryContainer
        )

        SummarySection(
            title = "ANNUAL NET",
            fabValue = totalIncomes[1] - totalExpenses[1],
            sabValue = totalIncomes[2] - totalExpenses[2],
            totalValue = totalIncomes[0] - totalExpenses[0],
            fabColor = fabColor,
            sabColor = sabColor,
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
    fabColor: Color,
    sabColor: Color,
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
                SummaryRow(label = "Fab", value = fabValue, color = fabColor)
                SummaryRow(label = "Sab", value = sabValue, color = sabColor)
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
                    text = "Total",
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
                        text = "avg: ${"%.2f".format(totalValue / 12.0)} €/mo",
                        style = MaterialTheme.typography.labelSmall,
                        color = onContainerColor
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: Double, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 18.sp,
            color = color
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${"%.2f".format(value)} €",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 18.sp,
                color = color
            )
            Text(
                text = " (${"%.2f".format(value / 12.0)} €)",
                style = MaterialTheme.typography.bodySmall,
                color = color.copy(alpha = 0.6f)
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
