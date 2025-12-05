package com.fabiobassi.famigliab.ui.features.budgeting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.Income
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.Payment
import java.util.Date


@Composable
fun BudgetingScreen(
    paddingValues: PaddingValues,
    viewModel: BudgetingViewModel = viewModel(),
) {
    val payments by viewModel.payments.collectAsState()
    val incomes by viewModel.incomes.collectAsState()
    BudgetingScreenContent(
        paddingValues = paddingValues,
        payments = payments,
        incomes = incomes
    )
}

@Composable
fun BudgetingScreenContent(
    paddingValues: PaddingValues,
    payments: List<Payment>,
    incomes: List<Income>
) {
    val totalPaymentsFab = payments.filter { it.paidBy == Person.FAB }.sumOf { it.amount }
    val totalPaymentsSab = payments.filter { it.paidBy == Person.SAB }.sumOf { it.amount }
    val totalPayments = totalPaymentsFab + totalPaymentsSab

    val totalIncomeFab = incomes.filter { it.paidTo == Person.FAB }.sumOf { it.amount }
    val totalIncomeSab = incomes.filter { it.paidTo == Person.SAB }.sumOf { it.amount }
    val totalIncome = totalIncomeFab + totalIncomeSab

    val paymentsByCategory = payments.groupBy { it.category }
    val maxCategoryTotal = paymentsByCategory.values.maxOfOrNull { categoryoutcomes ->
        categoryoutcomes.sumOf { it.amount }
    } ?: 1.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SummarySection(
                totalIncomeFab = totalIncomeFab,
                totalIncomeSab = totalIncomeSab,
                totalIncome = totalIncome,
                totalOutcomeFab = totalPaymentsFab,
                totalOutcomeSab = totalPaymentsSab,
                totalOutcome = totalPayments
            )
        }

        items(paymentsByCategory.entries.toList()) { (category, paymentsInCategory) ->
            val categoryTotalFab = paymentsInCategory.filter { it.paidBy == Person.FAB }.sumOf { it.amount }
            val categoryTotalSab = paymentsInCategory.filter { it.paidBy == Person.SAB }.sumOf { it.amount }
            val barFractionFab = if (maxCategoryTotal > 0) {
                (categoryTotalFab / maxCategoryTotal).toFloat()
            } else {
                0f
            }
            val barFractionSab = if (maxCategoryTotal > 0) {
                (categoryTotalSab / maxCategoryTotal).toFloat()
            } else {
                0f
            }
            CategoryBar(
                category = category,
                totalFab = categoryTotalFab,
                totalSab = categoryTotalSab,
                fractionFab = barFractionFab,
                fractionSab = barFractionSab
            )
        }
    }
}

@Composable
private fun SummarySection(
    totalIncomeFab: Double,
    totalIncomeSab: Double,
    totalIncome: Double,
    totalOutcomeFab: Double,
    totalOutcomeSab: Double,
    totalOutcome: Double
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Totale Entrate Fab: ${"%.2f".format(totalIncomeFab)} €",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Green
        )
        Text(
            text = "Totale Entrate Sab: ${"%.2f".format(totalIncomeSab)} €",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Green
        )
        Text(
            text = "Totale Entrate: ${"%.2f".format(totalIncome)} €",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Green
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Totale Uscite Fab: ${"%.2f".format(totalOutcomeFab)} €",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        Text(
            text = "Totale Uscite Sab: ${"%.2f".format(totalOutcomeSab)} €",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        Text(
            text = "Totale Uscite: ${"%.2f".format(totalOutcome)} €",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
    }
}

@Composable
private fun CategoryBar(
    category: Category,
    totalFab: Double,
    totalSab: Double,
    fractionFab: Float,
    fractionSab: Float
) {
    val barColorFab = MaterialTheme.colorScheme.primary
    val barColorSab = MaterialTheme.colorScheme.secondary

    Column {
        Text(
            text = category.name.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.small,
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fractionFab)
                        .height(24.dp)
                        .background(
                            color = barColorFab,
                            shape = MaterialTheme.shapes.small,
                        )
                )
            }
            Text(
                text = "${"%.2f".format(totalFab)} €",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(24.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.small,
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fractionSab)
                        .height(24.dp)
                        .background(
                            color = barColorSab,
                            shape = MaterialTheme.shapes.small,
                        )
                )
            }
            Text(
                text = "${"%.2f".format(totalSab)} €",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetingScreenPreview() {
    val mockOutcomes = listOf(
        Payment(Date(), "Spesa Esselunga", 150.55, Person.FAB, Category.CIBO),
        Payment(Date(), "Benzina", 70.0, Person.SAB, Category.MACCHINA),
        Payment(Date(), "Cinema", 20.0, Person.FAB, Category.SVAGO),
        Payment(Date(), "Bolletta luce", 80.0, Person.SAB, Category.BOLLETTE),
        Payment(Date(), "Ristorante", 95.0, Person.FAB, Category.CIBO),
    )
    val mockIncomes = listOf(
        Income(Date(), "Stipendio", 2000.0, Person.FAB),
        Income(Date(), "Stipendio", 1500.0, Person.SAB),
    )
    BudgetingScreenContent(
        paddingValues = PaddingValues(),
        payments = mockOutcomes,
        incomes = mockIncomes
    )
}
