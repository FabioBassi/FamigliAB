package com.fabiobassi.famigliab.ui.features.budgeting

import android.content.Context
import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.Income
import com.fabiobassi.famigliab.data.Payment
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.Voucher
import com.fabiobassi.famigliab.file.CsvFileManager
import com.fabiobassi.famigliab.file.CsvFileType
import com.fabiobassi.famigliab.ui.theme.categoryColors
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.min


@Composable
fun BudgetingScreen(
    paddingValues: PaddingValues,
    viewModel: BudgetingViewModel = viewModel(),
) {
    var showAddPaymentDialog by remember { mutableStateOf(false) }
    var showAddIncomeDialog by remember { mutableStateOf(false) }
    var showAddVoucherDialog by remember { mutableStateOf(false) }
    val payments by viewModel.payments.collectAsState()
    val incomes by viewModel.incomes.collectAsState()
    val vouchers by viewModel.vouchers.collectAsState()
    val currentDate by viewModel.currentDate.collectAsState()
    val context = LocalContext.current
    var showAllPayments by remember { mutableStateOf(false) }
    val csvFileManager = remember { CsvFileManager(context) }
    var paymentToDelete by remember { mutableStateOf<Payment?>(null) }


    if (showAddPaymentDialog) {
        AddPaymentDialog(
            onDismiss = { showAddPaymentDialog = false },
            onConfirm = {
                date, description, amount, category, person ->
                viewModel.addPayment(date, description, amount, category, person)
                showAddPaymentDialog = false
            }
        )
    }

    paymentToDelete?.let {
        DeleteConfirmationDialog(
            payment = it,
            onConfirm = {
                viewModel.deletePayment(it.id)
                paymentToDelete = null
            },
            onDismiss = { paymentToDelete = null }
        )
    }

    if (showAddIncomeDialog) {
        AddIncomeDialog(
            onDismiss = { showAddIncomeDialog = false },
            onConfirm = { description, amount, person ->
                viewModel.addIncome(description, amount, person)
                showAddIncomeDialog = false
            }
        )
    }

    if (showAddVoucherDialog) {
        AddVoucherDialog(
            onDismiss = { showAddVoucherDialog = false },
            onConfirm = { value, numberUsed, person ->
                viewModel.addVoucher(value, numberUsed, person)
                showAddVoucherDialog = false
            }
        )
    }

    BudgetingScreenContent(
        paddingValues = paddingValues,
        payments = payments,
        incomes = incomes,
        vouchers = vouchers,
        currentDate = currentDate,
        showAllPayments = showAllPayments,
        onShowAllPaymentsClick = { showAllPayments = !showAllPayments },
        onPreviousMonthClick = viewModel::previousMonth,
        onNextMonthClick = viewModel::nextMonth,
        onAddPaymentClick = { showAddPaymentDialog = true },
        onAddIncomeClick = { showAddIncomeDialog = true },
        onAddVoucherClick = { showAddVoucherDialog = true },
        onSharePaymentsClick = {
            val file = csvFileManager.getFileForMonth(CsvFileType.PAYMENTS, currentDate)
            shareFile(context, file)
        },
        onShareIncomesClick = {
            val file = csvFileManager.getFileForMonth(CsvFileType.INCOMES, currentDate)
            shareFile(context, file)
        },
        onShareVouchersClick = {
            val file = csvFileManager.getFileForMonth(CsvFileType.VOUCHERS, currentDate)
            shareFile(context, file)
        },
        onPaymentLongClick = { paymentToDelete = it },
    )
}

@Composable
fun BudgetingScreenContent(
    paddingValues: PaddingValues,
    payments: List<Payment>,
    incomes: List<Income>,
    vouchers: List<Voucher>,
    currentDate: Date,
    showAllPayments: Boolean,
    onShowAllPaymentsClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onAddPaymentClick: () -> Unit,
    onAddIncomeClick: () -> Unit,
    onAddVoucherClick: () -> Unit,
    onSharePaymentsClick: () -> Unit,
    onShareIncomesClick: () -> Unit,
    onShareVouchersClick: () -> Unit,
    onPaymentLongClick: (Payment) -> Unit,
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

    val totalPaymentsFab = monthlyPayments.filter { it.paidBy == Person.FAB }.sumOf { it.amount }
    val totalPaymentsSab = monthlyPayments.filter { it.paidBy == Person.SAB }.sumOf { it.amount }
    val totalPayments = totalPaymentsFab + totalPaymentsSab

    val totalIncomeFab = incomes.filter { it.paidTo == Person.FAB }.sumOf { it.amount }
    val totalIncomeSab = incomes.filter { it.paidTo == Person.SAB }.sumOf { it.amount }
    val totalIncome = totalIncomeFab + totalIncomeSab

    val paymentsByCategory = monthlyPayments.groupBy { it.category }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
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
                    showAllPayments = showAllPayments,
                    onShowAllPaymentsClick = onShowAllPaymentsClick,
                    onPaymentLongClick = onPaymentLongClick,
                )
            }

            if (!showAllPayments) {
                item {
                    ExpensesSummary(
                        paymentsByCategory = paymentsByCategory,
                        colors = categoryColors
                    )
                }

                item {
                    VoucherSummarySection(vouchers = vouchers)
                }
                item {
                    ShareSection(
                        onSharePaymentsClick = onSharePaymentsClick,
                        onShareIncomesClick = onShareIncomesClick,
                        onShareVouchersClick = onShareVouchersClick
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    onAddPaymentClick()
                 },
            ) {
                Icon(Icons.Filled.ShoppingCart, "Add new payment")
            }
            FloatingActionButton(
                onClick = {
                    onAddIncomeClick()
                 },
            ) {
                Icon(Icons.Filled.AttachMoney, "Add new income")
            }
            FloatingActionButton(
                onClick = {
                    onAddVoucherClick()
                 },
            ) {
                Icon(Icons.Filled.CreditCard, "Edit vouchers")
            }
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
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
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
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Fab: ${"%.2f".format(totalIncomeFab)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp
                )
                Text(
                    text = "Sab: ${"%.2f".format(totalIncomeSab)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp
                )
                Text(
                    text = "${"%.2f".format(totalIncome)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
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
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Fab: ${"%.2f".format(totalOutcomeFab)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp
                )
                Text(
                    text = "Sab: ${"%.2f".format(totalOutcomeSab)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp
                )
                Text(
                    text = "${"%.2f".format(totalOutcome)} €",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LastPaymentsSection(
    payments: List<Payment>,
    colors: Map<String, Color>,
    showAllPayments: Boolean,
    onShowAllPaymentsClick: () -> Unit,
    onPaymentLongClick: (Payment) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = if (showAllPayments) "ALL PAYMENTS" else "LAST PAYMENTS",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (payments.isNotEmpty()) {
                val dateFormat = remember { SimpleDateFormat("dd/MM", Locale.getDefault()) }

                val n = if (showAllPayments) payments.size else 3
                payments.sortedByDescending { it.date }.take(n).forEach { payment ->
                    // single payment single row
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                            .combinedClickable(
                                onClick = { },
                                onLongClick = { onPaymentLongClick(payment) },
                            )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dateFormat.format(payment.date),
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box {
                                val color = colors[payment.category.name] ?: Color.DarkGray
                                Text(
                                    text = payment.category.name.lowercase().replaceFirstChar { it.titlecase() },
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier
                                        .background(
                                            color = color.copy(),
                                            shape = RoundedCornerShape(25)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = payment.description,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(0.7f),
                                maxLines = 1,
                                fontSize = 16.sp,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "%.2f €".format(payment.amount),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(0.3f)
                            )
                        }
                    }
                    //Spacer(modifier = Modifier.height(2.dp))
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
                onClick = onShowAllPaymentsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (showAllPayments) "Hide All Payments" else "View All Monthly Payments")
            }
        }
    }
}

@Composable
private fun ExpensesSummary(
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
                    modifier = Modifier.weight(1.4f)
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
                    modifier = Modifier.weight(1.28f),
                    textAlign = TextAlign.End
                )
            }
            // Body
            Category.entries.sortedBy { it.name }.forEach { category ->
                val payments = paymentsByCategory[category] ?: emptyList()
                val totalFab = payments.filter { it.paidBy == Person.FAB }.sumOf { it.amount }
                val totalSab = payments.filter { it.paidBy == Person.SAB }.sumOf { it.amount }
                val total = totalFab + totalSab
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
                            style = MaterialTheme.typography.bodyLarge,
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
                        text = "%.2f€".format(totalFab),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1.16f),
                        textAlign = TextAlign.End,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "%.2f€".format(totalSab),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1.16f),
                        textAlign = TextAlign.End,
                        fontSize = 10.sp
                    )
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

@Composable
private fun VoucherSummarySection(vouchers: List<Voucher>) {
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
                text = "VOUCHERS USED",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (vouchers.isNotEmpty()) {
                val fabVouchers = vouchers.filter { it.whose == Person.FAB }
                val sabVouchers = vouchers.filter { it.whose == Person.SAB }

                val totalFabVouchersCount = fabVouchers.sumOf { it.numberUsed }
                val totalFabVouchersValue = fabVouchers.sumOf { it.value * it.numberUsed }
                val totalSabVouchersCount = sabVouchers.sumOf { it.numberUsed }
                val totalSabVouchersValue = sabVouchers.sumOf { it.value * it.numberUsed }
                val totalVouchersCount = totalFabVouchersCount + totalSabVouchersCount
                val totalVouchersValue = totalFabVouchersValue + totalSabVouchersValue

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Fab:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$totalFabVouchersCount vouchers (%.2f €)".format(totalFabVouchersValue),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.End
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sab:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$totalSabVouchersCount vouchers (%.2f €)".format(totalSabVouchersValue),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.End
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Total:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$totalVouchersCount vouchers (%.2f €)".format(totalVouchersValue),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End
                    )
                }
            } else {
                Text(
                    text = "No vouchers used yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun ShareSection(
    onSharePaymentsClick: () -> Unit,
    onShareIncomesClick: () -> Unit,
    onShareVouchersClick: () -> Unit
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
                text = "SHARE CSV FILES",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSharePaymentsClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Payments")
                }
                Button(
                    onClick = onShareIncomesClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Incomes")
                }
                Button(
                    onClick = onShareVouchersClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Vouchers")
                }
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    payment: Payment,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Payment") },
        text = { Text("Are you sure you want to delete this payment?\n\'${payment.description}' of ${payment.amount}€") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun shareFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share CSV"))
}

@Preview(showBackground = true)
@Composable
fun BudgetingScreenPreview() {
    val mockOutcomes = listOf(
        Payment(UUID.randomUUID().toString(), Date(), "Spesa Esselunga", 150.55, Person.FAB, Category.CIBO),
        Payment(UUID.randomUUID().toString(), Date(), "Benzina", 70.0, Person.SAB, Category.MACCHINA),
        Payment(UUID.randomUUID().toString(), Date(), "Cinema", 20.0, Person.FAB, Category.SVAGO),
        Payment(UUID.randomUUID().toString(), Date(), "Bolletta luce", 80.0, Person.SAB, Category.BOLLETTE),
        Payment(UUID.randomUUID().toString(), Date(), "Ristorante", 95.0, Person.FAB, Category.CIBO),
        Payment(UUID.randomUUID().toString(), Date(), "Affitto", 800.0, Person.FAB, Category.CASA),
        Payment(UUID.randomUUID().toString(), Date(), "Spesa abbigliamento", 120.0, Person.SAB, Category.ABBIGLIAMENTO),
        Payment(UUID.randomUUID().toString(), Date(), "Piscina", 45.0, Person.FAB, Category.SPORT),
        Payment(UUID.randomUUID().toString(), Date(), "Visita medica", 60.0, Person.SAB, Category.SALUTE),
        Payment(UUID.randomUUID().toString(), Date(), "Regalo compleanno", 30.0, Person.FAB, Category.REGALI),
        Payment(UUID.randomUUID().toString(), Date(), "Viaggio Roma", 300.0, Person.SAB, Category.TURISMO),
        Payment(UUID.randomUUID().toString(), Date(), "Cena fuori", 75.0, Person.FAB, Category.RISTORAZIONE),
        Payment(UUID.randomUUID().toString(), Date(), "Manutenzione auto", 150.0, Person.SAB, Category.MACCHINA),
        Payment(UUID.randomUUID().toString(), Date(), "Libro", 25.0, Person.FAB, Category.VARIE),
        Payment(UUID.randomUUID().toString(), Date(), "Netflix", 15.99, Person.SAB, Category.SVAGO),
        Payment(UUID.randomUUID().toString(), Date(), "Uscita amici", 50.0, Person.FAB, Category.SVAGO),
        Payment(UUID.randomUUID().toString(), Date(), "Regalo di Natale", 100.0, Person.SAB, Category.REGALI),
        Payment(UUID.randomUUID().toString(), Date(), "Treno", 40.0, Person.FAB, Category.TURISMO),
        Payment(UUID.randomUUID().toString(), Date(), "Spesa Carrefour", 85.30, Person.SAB, Category.CIBO),
        Payment(UUID.randomUUID().toString(), Date(), "Parrucchiere", 40.0, Person.FAB, Category.VARIE)
    )
    val mockIncomes = listOf(
        Income("Stipendio", 2000.0, Person.FAB),
        Income("Stipendio", 1500.0, Person.SAB),
        Income("Bonus", 200.0, Person.FAB),
    )
    val mockVouchers = listOf(
        Voucher(value = 50.0, numberUsed = 2, whose = Person.FAB),
        Voucher(value = 25.0, numberUsed = 1, whose = Person.SAB),
        Voucher(value = 100.0, numberUsed = 1, whose = Person.FAB)
    )
    BudgetingScreenContent(
        paddingValues = PaddingValues(),
        payments = mockOutcomes,
        incomes = mockIncomes,
        vouchers = mockVouchers,
        currentDate = Date(),
        showAllPayments = false,
        onShowAllPaymentsClick = {},
        onPreviousMonthClick = {},
        onNextMonthClick = {},
        onAddPaymentClick = {},
        onAddIncomeClick = {},
        onAddVoucherClick = {},
        onSharePaymentsClick = {},
        onShareIncomesClick = {},
        onShareVouchersClick = {},
        onPaymentLongClick = {},
    )
}
