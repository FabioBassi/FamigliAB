package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ANNUAL INCOME",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFF00aa00)
                )
                Text(
                    text = "Fab: ${"%.2f".format(totalIncomes[1])} €\t(${"%.2f".format(totalIncomes[1] / 12.0)} €)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    color = fabColor
                )
                Text(
                    text = "Sab: ${"%.2f".format(totalIncomes[2])} €\t(${"%.2f".format(totalIncomes[2] / 12.0)} €)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    color = sabColor
                )
                Text(
                    text = "Tot: ${"%.2f".format(totalIncomes[0])} €\t(${"%.2f".format(totalIncomes[0] / 12.0)} €)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ANNUAL EXPENSES",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Red
                )
                Text(
                    text = "Fab: ${"%.2f".format(totalExpenses[1])} € (${"%.2f".format(totalExpenses[1] / 12.0)} €)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    color = fabColor
                )
                Text(
                    text = "Sab: ${"%.2f".format(totalExpenses[2])} € (${"%.2f".format(totalExpenses[2] / 12.0)} €)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    color = sabColor
                )
                Text(
                    text = "Tot: ${"%.2f".format(totalExpenses[0])} € (${"%.2f".format(totalExpenses[0] / 12.0)} €)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ANNUAL NET",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Blue
                )
                Text(
                    text = "Fab: ${"%.2f".format(totalIncomes[1]-totalExpenses[1])} € " +
                            "(${"%.2f".format((totalIncomes[1]-totalExpenses[1]) / 12.0)} €)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    color = fabColor
                )
                Text(
                    text = "Sab: ${"%.2f".format(totalIncomes[2]-totalExpenses[2])} € " +
                            "(${"%.2f".format((totalIncomes[2]-totalExpenses[2]) / 12.0)} €)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp,
                    color = sabColor
                )
                Text(
                    text = "Tot: ${"%.2f".format(totalIncomes[0]-totalExpenses[0])} € " +
                            "(${"%.2f".format((totalIncomes[0]-totalExpenses[0]) / 12.0)} €)",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
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
