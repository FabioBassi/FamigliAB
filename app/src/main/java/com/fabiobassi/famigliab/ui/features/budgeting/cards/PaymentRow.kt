package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.Payment
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.SettingsDataStore
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.fabiobassi.famigliab.R


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PaymentRow(
    payment: Payment,
    dateFormat: SimpleDateFormat,
    colors: Map<String, Color>,
    onPaymentClick: (Payment) -> Unit,
    onPaymentLongClick: (Payment) -> Unit,
) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val personColorHex by settingsDataStore.getColorFor(payment.paidBy.name)
        .collectAsState(initial = "")

    val defaultPersonColor = MaterialTheme.colorScheme.primary
    val personColor = remember(personColorHex, defaultPersonColor) {
        try {
            if (personColorHex.isNotEmpty()) {
                Color(personColorHex.toColorInt())
            } else {
                defaultPersonColor
            }
        } catch (e: Exception) {
            defaultPersonColor
        }
    }

    val categoryColor = colors[payment.category.name] ?: MaterialTheme.colorScheme.secondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onPaymentClick(payment) },
                onLongClick = { onPaymentLongClick(payment) },
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Date and Category Icon/Badge
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .weight(0.25f)
        ) {
            Text(
                text = dateFormat.format(payment.date),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = payment.category.name.take(3).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = categoryColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(categoryColor.copy(alpha = 0.15f), MaterialTheme.shapes.extraSmall)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        // Description and Payer
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = payment.description,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.payed_by, payment.paidBy.name),
                style = MaterialTheme.typography.labelSmall,
                color = personColor
            )
        }

        // Amount
        Text(
            text = "%.2f €".format(payment.amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(0.5f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentRowPreview() {
    FamigliABTheme {
        PaymentRow(
            payment = Payment(
                date = Date(),
                description = "Groceries at the supermarket",
                amount = 45.67,
                paidBy = Person.FAB,
                category = Category.CIBO
            ),
            dateFormat = SimpleDateFormat("dd/MM", Locale.US),
            colors = mapOf(Category.CIBO.name to Color(0xFF4CAF50)),
            onPaymentClick = {},
            onPaymentLongClick = {}
        )
    }
}
