package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.Payment
import kotlin.math.min

@Composable
fun AnnualCategoryExpensesCard(
    paymentsByCategory: Map<Category, List<Payment>>,
    colors: Map<String, Color>
) {
    val categoryTotals = remember(paymentsByCategory) {
        Category.entries.associateWith { category ->
            paymentsByCategory[category]?.sumOf { it.amount } ?: 0.0
        }
    }
    val totalExpenses = remember(categoryTotals) { categoryTotals.values.sum() }

    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(key1 = totalExpenses) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500)
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Title
            Text(
                text = "EXPENSES BY CATEGORY",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CATEGORY",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.4f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
                Text(
                    text = "TOTAL",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.28f),
                    textAlign = TextAlign.End
                )
            }
            // Body
            Category.entries.sortedBy { it.name }.forEach { category ->
                val payments = paymentsByCategory[category] ?: emptyList()
                val total = payments.sumOf { it.amount }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1.4f), // Adjusted weight for the category column
                        verticalAlignment = Alignment.CenterVertically
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
                                .fillMaxWidth()
                                .background(
                                    color = (colors[category.name] ?: Color.DarkGray).copy(),
                                    shape = RoundedCornerShape(25)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = false
                        )
                    }
                    Text(
                        text = "%.2f€".format(total),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1.28f),
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (totalExpenses > 0) {
                Canvas(modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)) {
                    val totalAngleToDraw = 360f * animationProgress.value
                    var startAngle = -90f
                    var drawnAngle = 0f

                    Category.entries.sortedBy { it.name }.forEach { category ->
                        if (drawnAngle >= totalAngleToDraw) return@forEach

                        val total = categoryTotals[category] ?: 0.0
                        if (total > 0) {
                            val sweepAngleForCategory = (total / totalExpenses).toFloat() * 360f
                            val angleToDraw = min(sweepAngleForCategory, totalAngleToDraw - drawnAngle)

                            drawArc(
                                color = colors[category.name] ?: Color.LightGray,
                                startAngle = startAngle,
                                sweepAngle = angleToDraw,
                                useCenter = true
                            )
                            startAngle += sweepAngleForCategory
                            drawnAngle += sweepAngleForCategory
                        }
                    }
                }
            } else {
                Text("No expense data to display.", modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}
