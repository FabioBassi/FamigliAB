package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.Payment
import com.fabiobassi.famigliab.data.Person
import kotlin.math.min
import com.fabiobassi.famigliab.R


@Composable
fun CategoryExpensesCard(
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.expenses_by_category),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (totalExpenses > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Canvas(modifier = Modifier.size(120.dp)) {
                        val totalAngleToDraw = 360f * animationProgress.value
                        var startAngle = -90f
                        var drawnAngle = 0f

                        Category.entries.sortedByDescending { categoryTotals[it] ?: 0.0 }.forEach { category ->
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

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Category.entries
                            .filter { (categoryTotals[it] ?: 0.0) > 0 }
                            .sortedByDescending { categoryTotals[it] ?: 0.0 }
                            .take(5)
                            .forEach { category ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Spacer(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(colors[category.name] ?: Color.Gray, MaterialTheme.shapes.extraSmall)
                                    )
                                    Text(
                                        text = category.name.lowercase().replaceFirstChar { it.titlecase() },
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Category.entries
                        .filter { (categoryTotals[it] ?: 0.0) > 0 }
                        .sortedByDescending { categoryTotals[it] ?: 0.0 }
                        .forEach { category ->
                            val payments = paymentsByCategory[category] ?: emptyList()
                            val totalFab = payments.filter { it.paidBy == Person.FAB }.sumOf { it.amount }
                            val totalSab = payments.filter { it.paidBy == Person.SAB }.sumOf { it.amount }
                            val total = totalFab + totalSab
                            val percentage = (total * 100.0) / totalExpenses
                            
                            CategoryItemRow(
                                category = category,
                                total = total,
                                percentage = percentage,
                                color = colors[category.name] ?: Color.Gray
                            )
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                }
            } else {
                Text(
                    stringResource(R.string.no_expense_data),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 32.dp).align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
