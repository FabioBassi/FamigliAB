package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.ui.theme.categoryColors

@Composable
fun AnnualCategoryItem(
    category: Category,
    totalFab: Double,
    totalSab: Double,
    total: Double,
    percentage: Double,
    colors: Map<String, Color>
) {
    val categoryColor = colors[category.name] ?: MaterialTheme.colorScheme.secondary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Main Row: Category Tag and Total Amount
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = categoryColor.copy(alpha = 0.12f),
                contentColor = categoryColor,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = category.name.lowercase().replaceFirstChar { it.titlecase() },
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "${"%.2f".format(total)} €",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.End,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Detail Row: Percentage and Personal Breakdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${"%.1f".format(percentage)}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Fab: ${"%.2f".format(totalFab)} €",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = " • ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Text(
                    text = "Sab: ${"%.2f".format(totalSab)} €",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnnualCategoryItemPreview() {
    AnnualCategoryItem(
        category = Category.ABBIGLIAMENTO,
        totalFab = 100.0,
        totalSab = 200.0,
        total = 300.0,
        percentage = 23.7,
        colors = categoryColors
    )
}
