package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = category.name.lowercase().replaceFirstChar { it.titlecase() },
                    style = MaterialTheme.typography.bodySmall.copy(
                        shadow = Shadow(
                            color = if (isSystemInDarkTheme()) Color.Black else Color.LightGray,
                            offset = Offset(2f, 2f),
                            blurRadius = 5f
                        )
                    ),
                    modifier = Modifier
                        .weight(2.5f)
                        .background(
                            color = (colors[category.name] ?: Color.DarkGray).copy(),
                            shape = RoundedCornerShape(25)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    text = "${"%.2f".format(percentage)} %",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    fontSize = 18.sp,
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Fab: ${"%.2f".format(totalFab)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    fontSize = 16.sp
                )
                Text(
                    text = "Sab: ${"%.2f".format(totalSab)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End,
                    fontSize = 16.sp
                )
                Text(
                    text = "Tot: ${"%.2f".format(total)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    fontSize = 18.sp
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