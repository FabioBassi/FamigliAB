package com.fabiobassi.famigliab.ui.features.budgeting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.PaidBy
import com.fabiobassi.famigliab.data.Payment
import java.util.Date

@Composable
fun BudgetingScreen(
    paddingValues: PaddingValues,
    viewModel: BudgetingViewModel = viewModel(),
) {
    val payments by viewModel.payments.collectAsState()
    BudgetingScreenContent(
        paddingValues = paddingValues,
        payments = payments
    )
}

@Composable
fun BudgetingScreenContent(
    paddingValues: PaddingValues,
    payments: List<Payment>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(payments) { payment ->
            PaymentItem(payment = payment)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetingScreenPreview() {
    val mockPayments = listOf(
        Payment(Date(), "Spesa Esselunga", 150.55, PaidBy.FAB, Category.CIBO),
        Payment(Date(), "Benzina", 70.0, PaidBy.SAB, Category.MACCHINA),
        Payment(Date(), "Cinema", 20.0, PaidBy.FAB, Category.SVAGO),
        Payment(Date(), "Bolletta luce", 80.0, PaidBy.SAB, Category.BOLLETTE),
    )
    BudgetingScreenContent(
        paddingValues = PaddingValues(),
        payments = mockPayments
    )
}