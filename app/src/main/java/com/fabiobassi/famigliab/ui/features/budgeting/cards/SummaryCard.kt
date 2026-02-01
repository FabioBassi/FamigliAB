package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.fabiobassi.famigliab.data.SettingsDataStore

@Composable
fun SummaryCard(
    totalIncomes: List<Double>,
    totalExpenses: List<Double>,
    onIncomeCardClick: () -> Unit,
) {
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
            icon = Icons.AutoMirrored.Filled.TrendingUp,
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
            icon = Icons.AutoMirrored.Filled.TrendingDown,
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
    icon: ImageVector,
    containerColor: Color,
    onContainerColor: Color,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val fabColorHex by settingsDataStore.getColorFor("FAB")
        .collectAsState(initial = "")
    val sabColorHex by settingsDataStore.getColorFor("SAB")
        .collectAsState(initial = "")

    val fabColor = if (fabColorHex.isNotEmpty()) {
        Color(fabColorHex.toColorInt())
    } else {
        MaterialTheme.colorScheme.primary
    }
    val sabColor = if (sabColorHex.isNotEmpty()) {
        Color(sabColorHex.toColorInt())
    } else {
        MaterialTheme.colorScheme.primary
    }

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
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth(),
            ) {
                Icon(icon, contentDescription = null, tint = onContainerColor)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(fabColor.copy(alpha = 0.7f), MaterialTheme.shapes.extraSmall)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Fab",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = onContainerColor
                    )
                    Text(
                        text = "${"%.2f".format(fabValue)} €",
                        style = MaterialTheme.typography.bodySmall,
                        color = onContainerColor,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(sabColor.copy(alpha = 0.7f), MaterialTheme.shapes.extraSmall)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Sab",
                        style = MaterialTheme.typography.bodySmall,
                        color = onContainerColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${"%.2f".format(sabValue)} €",
                        style = MaterialTheme.typography.bodySmall,
                        color = onContainerColor,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }
            }

            Text(
                text = "${"%.2f".format(totalValue)} €",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = onContainerColor,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
