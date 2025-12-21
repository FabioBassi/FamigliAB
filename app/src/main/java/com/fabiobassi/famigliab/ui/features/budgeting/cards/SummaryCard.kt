package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabiobassi.famigliab.data.SettingsDataStore
import androidx.core.graphics.toColorInt

@Composable
fun SummaryCard(
    totalIncomes: List<Double>,
    totalExpenses: List<Double>,
    onIncomeCardClick: () -> Unit,
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

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .clickable { onIncomeCardClick() },
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),

            ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "INCOME",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color(0xFF00aa00)
                )
                Text(
                    text = "Fab: ${"%.2f".format(totalIncomes[1])} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp,
                    color = fabColor
                )
                Text(
                    text = "Sab: ${"%.2f".format(totalIncomes[2])} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp,
                    color = sabColor
                )
                Text(
                    text = "${"%.2f".format(totalIncomes[0])} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        Card(
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "EXPENSES",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.Red
                )
                Text(
                    text = "Fab: ${"%.2f".format(totalExpenses[1])} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp,
                    color = fabColor
                )
                Text(
                    text = "Sab: ${"%.2f".format(totalExpenses[2])} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp,
                    color = sabColor
                )
                Text(
                    text = "${"%.2f".format(totalExpenses[0])} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
