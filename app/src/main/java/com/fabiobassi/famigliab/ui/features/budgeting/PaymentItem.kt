package com.fabiobassi.famigliab.ui.features.budgeting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.PaidBy
import com.fabiobassi.famigliab.data.Payment
import java.util.Date

@Composable
fun PaymentItem(payment: Payment) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = payment.description, fontSize = 18.sp)
                Text(text = payment.category.name, fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "€${payment.amount}", fontSize = 18.sp)
                Text(text = payment.paidBy.name, fontSize = 12.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentItemPreview() {
    PaymentItem(
        payment = Payment(
            Date(),
            "Spesa Esselunga",
            150.55,
            PaidBy.FAB,
            Category.CIBO
        )
    )
}