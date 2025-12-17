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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            Category.entries.sortedBy { it.name }.forEach { category ->
                val payments = paymentsByCategory[category] ?: emptyList()
                val totalFab = payments.filter { it.paidBy == Person.FAB }.sumOf { it.amount }
                val totalSab = payments.filter { it.paidBy == Person.SAB }.sumOf { it.amount }
                val total = totalFab + totalSab
                val percentage = (total * 100.0) / totalExpenses
                AnnualCategoryItem(category, totalFab, totalSab, total, percentage, colors)
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