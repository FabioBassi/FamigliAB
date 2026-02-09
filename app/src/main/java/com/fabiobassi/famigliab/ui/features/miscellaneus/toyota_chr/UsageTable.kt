package com.fabiobassi.famigliab.ui.features.miscellaneus.toyota_chr

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.data.ChrUsage

@Composable
fun UsageTable(
    entries: List<ChrUsage>,
    onEntryClick: (ChrUsage) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableHeaderText(text = "Week", modifier = Modifier.weight(0.6f))
                TableHeaderText(text = stringResource(R.string.date), modifier = Modifier.weight(1.1f))
                TableHeaderText(
                    text = "Exp.",
                    modifier = Modifier.weight(1.1f),
                    color = MaterialTheme.colorScheme.primary
                )
                TableHeaderText(
                    text = "Act.",
                    modifier = Modifier.weight(1.1f),
                    color = MaterialTheme.colorScheme.secondary
                )
                TableHeaderText(text = "Diff", modifier = Modifier.weight(1.1f))
            }

            HorizontalDivider(thickness = 2.dp)

            entries.forEach { entry ->
                val diff = entry.actualKm?.minus(entry.expectedKm)
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onEntryClick(entry) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCellText(
                        text = entry.week.toString(),
                        modifier = Modifier.weight(0.6f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    TableCellText(
                        text = entry.date,
                        modifier = Modifier.weight(1.1f),
                        style = MaterialTheme.typography.bodySmall
                    )
                    TableCellText(
                        text = "${entry.expectedKm}",
                        modifier = Modifier.weight(1.1f),
                        color = MaterialTheme.colorScheme.primary
                    )
                    TableCellText(
                        text = entry.actualKm?.toString() ?: "-",
                        modifier = Modifier.weight(1.1f),
                        fontWeight = FontWeight.Bold,
                        color = if (entry.actualKm != null) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    val diffText = when {
                        diff == null -> "-"
                        diff > 0 -> "+$diff"
                        else -> "$diff"
                    }
                    val diffColor = when {
                        diff == null -> MaterialTheme.colorScheme.onSurfaceVariant
                        diff > 0 -> MaterialTheme.colorScheme.error
                        else -> Color(0xFF2E7D32) // Dark Green
                    }

                    TableCellText(
                        text = diffText,
                        modifier = Modifier.weight(1.1f),
                        fontWeight = FontWeight.Bold,
                        color = diffColor
                    )
                }
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun TableHeaderText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = color
    )
}

@Composable
fun TableCellText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    fontWeight: FontWeight? = null,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        textAlign = TextAlign.Center,
        fontWeight = fontWeight,
        color = color
    )
}
