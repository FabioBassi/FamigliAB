package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.Payment
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme
import com.fabiobassi.famigliab.ui.theme.categoryColors
import java.util.Date
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
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Expenses by Category",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (totalExpenses > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier.size(140.dp)) {
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
                }

                Column {
                    Category.entries
                        .filter { (categoryTotals[it] ?: 0.0) > 0 }
                        .sortedByDescending { categoryTotals[it] ?: 0.0 }
                        .forEachIndexed { index, category ->
                            val payments = paymentsByCategory[category] ?: emptyList()
                            val totalFab = payments.filter { it.paidBy == Person.FAB }.sumOf { it.amount }
                            val totalSab = payments.filter { it.paidBy == Person.SAB }.sumOf { it.amount }
                            val total = totalFab + totalSab
                            val percentage = (total * 100.0) / totalExpenses
                            
                            AnnualCategoryItem(category, totalFab, totalSab, total, percentage, colors)
                            
                            if (index < Category.entries.count { (categoryTotals[it] ?: 0.0) > 0 } - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                }
            } else {
                Text(
                    "No expense data to display.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(vertical = 32.dp)
                        .align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview
@Composable
fun AnnualCategoryExpensesCardPreview() {
    val samplePayments = listOf(
        Payment(date = Date(), description = "T-Shirt", amount = 25.0, paidBy = Person.FAB, category = Category.ABBIGLIAMENTO),
        Payment(date = Date(), description = "Jeans", amount = 75.0, paidBy = Person.SAB, category = Category.ABBIGLIAMENTO),
        Payment(date = Date(), description = "Electricity bill", amount = 100.0, paidBy = Person.FAB, category = Category.BOLLETTE),
        Payment(date = Date(), description = "Groceries", amount = 50.0, paidBy = Person.SAB, category = Category.CIBO),
        Payment(date = Date(), description = "Dinner out", amount = 60.0, paidBy = Person.FAB, category = Category.RISTORAZIONE),
        Payment(date = Date(), description = "Gas", amount = 50.0, paidBy = Person.SAB, category = Category.MACCHINA),
        Payment(date = Date(), description = "Cinema", amount = 20.0, paidBy = Person.FAB, category = Category.SVAGO),
    )
    val paymentsByCategory = samplePayments.groupBy { it.category }
    FamigliABTheme {
        AnnualCategoryExpensesCard(
            paymentsByCategory = paymentsByCategory,
            colors = categoryColors
        )
    }
}
