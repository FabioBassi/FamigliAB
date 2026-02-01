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
import androidx.compose.ui.text.style.TextAlign
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
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "INCOME",
            fabValue = totalIncomes[1],
            sabValue = totalIncomes[2],
            totalValue = totalIncomes[0],
            fabColor = fabColor,
            sabColor = sabColor,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            onClick = onIncomeCardClick
        )
        SummaryCard(
            modifier = Modifier.weight(1f),
            title = "EXPENSES",
            fabValue = totalExpenses[1],
            sabValue = totalExpenses[2],
            totalValue = totalExpenses[0],
            fabColor = fabColor,
            sabColor = sabColor,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            onContainerColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    fabValue: Double,
    sabValue: Double,
    totalValue: Double,
    fabColor: Color,
    sabColor: Color,
    containerColor: Color,
    onContainerColor: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
        ),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Fab", style = MaterialTheme.typography.bodySmall, color = fabColor)
                    Text(text = "${"%.2f".format(fabValue)} €", style = MaterialTheme.typography.bodySmall, color = fabColor)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Sab", style = MaterialTheme.typography.bodySmall, color = sabColor)
                    Text(text = "${"%.2f".format(sabValue)} €", style = MaterialTheme.typography.bodySmall, color = sabColor)
                }
            }

            Text(
                text = "${"%.2f".format(totalValue)} €",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
