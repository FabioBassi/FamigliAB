package com.fabiobassi.famigliab.ui.features.budgeting

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.Income
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.Payment
import com.fabiobassi.famigliab.ui.theme.categoryColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.min


@Composable
fun BudgetingScreen(
    paddingValues: PaddingValues,
    viewModel: BudgetingViewModel = viewModel(),
    onViewAllPaymentsClick: () -> Unit,
) {
    val payments by viewModel.payments.collectAsState()
    val incomes by viewModel.incomes.collectAsState()
    var currentDate by remember { mutableStateOf(Date()) }

    BudgetingScreenContent(
        paddingValues = paddingValues,
        payments = payments,
        incomes = incomes,
        currentDate = currentDate,
        onPreviousMonthClick = {
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.MONTH, -1)
            currentDate = calendar.time
        },
        onNextMonthClick = {
            val calendar = Calendar.getInstance()
            calendar.time = currentDate
            calendar.add(Calendar.MONTH, 1)
            currentDate = calendar.time
        },
        onViewAllPaymentsClick = onViewAllPaymentsClick
    )
}

@Composable
fun BudgetingScreenContent(
    paddingValues: PaddingValues,
    payments: List<Payment>,
    incomes: List<Income>,
    currentDate: Date,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onViewAllPaymentsClick: () -> Unit
) {
    val monthlyPayments = remember(payments, currentDate) {
        val selectedMonthCal = Calendar.getInstance().apply { time = currentDate }
        val selectedMonth = selectedMonthCal.get(Calendar.MONTH)
        val selectedYear = selectedMonthCal.get(Calendar.YEAR)
        val itemCalendar = Calendar.getInstance()
        payments.filter { payment ->
            itemCalendar.time = payment.date
            itemCalendar.get(Calendar.MONTH) == selectedMonth && itemCalendar.get(Calendar.YEAR) == selectedYear
        }
    }

    val monthlyIncomes = remember(incomes, currentDate) {
        val selectedMonthCal = Calendar.getInstance().apply { time = currentDate }
        val selectedMonth = selectedMonthCal.get(Calendar.MONTH)
        val selectedYear = selectedMonthCal.get(Calendar.YEAR)
        val itemCalendar = Calendar.getInstance()
        incomes.filter { income ->
            itemCalendar.time = income.date
            itemCalendar.get(Calendar.MONTH) == selectedMonth && itemCalendar.get(Calendar.YEAR) == selectedYear
        }
    }

    val totalPaymentsFab = monthlyPayments.filter { it.paidBy == Person.FAB }.sumOf { it.amount }
    val totalPaymentsSab = monthlyPayments.filter { it.paidBy == Person.SAB }.sumOf { it.amount }
    val totalPayments = totalPaymentsFab + totalPaymentsSab

    val totalIncomeFab = monthlyIncomes.filter { it.paidTo == Person.FAB }.sumOf { it.amount }
    val totalIncomeSab = monthlyIncomes.filter { it.paidTo == Person.SAB }.sumOf { it.amount }
    val totalIncome = totalIncomeFab + totalIncomeSab

    val paymentsByCategory = monthlyPayments.groupBy { it.category }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            MonthNavigation(
                currentDate = currentDate,
                onPreviousMonthClick = onPreviousMonthClick,
                onNextMonthClick = onNextMonthClick
            )
        }
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

        item {
            LastPaymentsSection(
                payments = monthlyPayments,
                colors = categoryColors,
                onViewAllPaymentsClick = onViewAllPaymentsClick
            )
        }

        item {
            ExpensesSummary(
                paymentsByCategory = paymentsByCategory,
                colors = categoryColors
            )
        }
    }
}

@Composable
private fun MonthNavigation(
    currentDate: Date,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit
) {
    val monthFormat = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonthClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
        }
        Text(
            text = monthFormat.format(currentDate).uppercase(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onNextMonthClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "INCOME",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Fab: ${"%.2f".format(totalIncomeFab)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Sab: ${"%.2f".format(totalIncomeSab)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Total: ${"%.2f".format(totalIncome)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Card(
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "EXPENSES",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Fab: ${"%.2f".format(totalOutcomeFab)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Sab: ${"%.2f".format(totalOutcomeSab)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Total: ${"%.2f".format(totalOutcome)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun LastPaymentsSection(
    payments: List<Payment>,
    colors: List<Color>,
    onViewAllPaymentsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "LAST PAYMENTS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (payments.isNotEmpty()) {
                val dateFormat = remember { SimpleDateFormat("dd/MM", Locale.getDefault()) }
                val sortedCategories = remember { Category.entries.sortedBy { it.name } }

                payments.sortedByDescending { it.date }.take(3).forEach { payment ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dateFormat.format(payment.date),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(0.15f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(modifier = Modifier.weight(0.3f)) {
                            val categoryIndex = sortedCategories.indexOf(payment.category)
                            Text(
                                text = payment.category.name.lowercase().replaceFirstChar { it.titlecase() },
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .background(
                                        color = colors[categoryIndex % colors.size].copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = payment.description,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(0.35f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "%.2f €".format(payment.amount),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(0.5f)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            } else {
                Text(
                    text = "No payments recorded yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Button(
                onClick = onViewAllPaymentsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View All Monthly Payments")
            }
        }
    }
}

@Composable
private fun ExpensesSummary(
    paymentsByCategory: Map<Category, List<Payment>>,
    colors: List<Color>
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Title
            Text(
                text = "EXPENSES BY CATEGORY",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
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
                    modifier = Modifier.weight(1.5f)
                )
                Text(
                    text = "FAB",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.16f),
                    textAlign = TextAlign.End
                )
                Text(
                    text = "SAB",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.16f),
                    textAlign = TextAlign.End
                )
                Text(
                    text = "TOTAL",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.18f),
                    textAlign = TextAlign.End
                )
            }
            // Body
            Category.entries.sortedBy { it.name }.forEachIndexed { index, category ->
                val payments = paymentsByCategory[category] ?: emptyList()
                val totalFab = payments.filter { it.paidBy == Person.FAB }.sumOf { it.amount }
                val totalSab = payments.filter { it.paidBy == Person.SAB }.sumOf { it.amount }
                val total = totalFab + totalSab
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1.5f), // Adjusted weight for the category column
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.name.lowercase().replaceFirstChar { it.titlecase() },
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .background(
                                    color = colors[index % colors.size].copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            softWrap = false
                        )
                    }
                    Text(
                        text = "%.2f €".format(totalFab),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1.16f),
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "%.2f €".format(totalSab),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1.16f),
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "%.2f €".format(total),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1.18f),
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

                    Category.entries.sortedBy { it.name }.forEachIndexed { index, category ->
                        if (drawnAngle >= totalAngleToDraw) return@forEachIndexed

                        val total = categoryTotals[category] ?: 0.0
                        if (total > 0) {
                            val sweepAngleForCategory = (total / totalExpenses).toFloat() * 360f
                            val angleToDraw = min(sweepAngleForCategory, totalAngleToDraw - drawnAngle)

                            drawArc(
                                color = colors[index % colors.size],
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

@Preview(showBackground = true)
@Composable
fun BudgetingScreenPreview() {
    val mockOutcomes = listOf(
        Payment(Date(), "Spesa Esselunga", 150.55, Person.FAB, Category.CIBO),
        Payment(Date(), "Benzina", 70.0, Person.SAB, Category.MACCHINA),
        Payment(Date(), "Cinema", 20.0, Person.FAB, Category.SVAGO),
        Payment(Date(), "Bolletta luce", 80.0, Person.SAB, Category.BOLLETTE),
        Payment(Date(), "Ristorante", 95.0, Person.FAB, Category.CIBO),
        Payment(Date(), "Affitto", 800.0, Person.FAB, Category.CASA),
        Payment(Date(), "Spesa abbigliamento", 120.0, Person.SAB, Category.ABBIGLIAMENTO),
        Payment(Date(), "Piscina", 45.0, Person.FAB, Category.SPORT),
        Payment(Date(), "Visita medica", 60.0, Person.SAB, Category.SALUTE),
        Payment(Date(), "Regalo compleanno", 30.0, Person.FAB, Category.REGALI),
        Payment(Date(), "Viaggio Roma", 300.0, Person.SAB, Category.TURISMO),
        Payment(Date(), "Cena fuori", 75.0, Person.FAB, Category.RISTORANTE),
        Payment(Date(), "Manutenzione auto", 150.0, Person.SAB, Category.MACCHINA),
        Payment(Date(), "Libro", 25.0, Person.FAB, Category.VARIE),
        Payment(Date(), "Netflix", 15.99, Person.SAB, Category.SVAGO),
        Payment(Date(), "Uscita amici", 50.0, Person.FAB, Category.SVAGO),
        Payment(Date(), "Regalo di Natale", 100.0, Person.SAB, Category.REGALI),
        Payment(Date(), "Treno", 40.0, Person.FAB, Category.TURISMO),
        Payment(Date(), "Spesa Carrefour", 85.30, Person.SAB, Category.CIBO),
        Payment(Date(), "Parrucchiere", 40.0, Person.FAB, Category.VARIE)
    )
    val mockIncomes = listOf(
        Income(Date(), "Stipendio", 2000.0, Person.FAB),
        Income(Date(), "Stipendio", 1500.0, Person.SAB),
        Income(Date(), "Bonus", 200.0, Person.FAB),
    )
    BudgetingScreenContent(
        paddingValues = PaddingValues(),
        payments = mockOutcomes,
        incomes = mockIncomes,
        currentDate = Date(),
        onPreviousMonthClick = {},
        onNextMonthClick = {},
        onViewAllPaymentsClick = {}
    )
}
