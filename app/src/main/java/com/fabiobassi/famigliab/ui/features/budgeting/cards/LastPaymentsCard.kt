package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.Payment
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun LastPaymentsCard(
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
                    PaymentRow(
                        payment = payment,
                        dateFormat = dateFormat,
                        colors = colors,
                        onPaymentLongClick = onPaymentLongClick
                    )
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
