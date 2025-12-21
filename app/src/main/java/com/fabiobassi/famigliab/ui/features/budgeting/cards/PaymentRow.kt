package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabiobassi.famigliab.data.Category
import com.fabiobassi.famigliab.data.Payment
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.SettingsDataStore
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PaymentRow(
    payment: Payment,
    dateFormat: SimpleDateFormat,
    colors: Map<String, Color>,
    onPaymentLongClick: (Payment) -> Unit,
) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val personColorHex by settingsDataStore.getColorFor(payment.paidBy.name)
        .collectAsState(initial = "")

    val personColor = remember(personColorHex) {
        try {
            if (personColorHex.isNotEmpty()) {
                Color(personColorHex.toColorInt())
            } else {
                Color.Unspecified
            }
        } catch (e: Exception) {
            Color.Unspecified
        }
    }
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
                    text = payment.category.name.lowercase()
                        .replaceFirstChar { it.titlecase() },
                    style = MaterialTheme.typography.bodySmall.copy(
                        shadow = Shadow(
                            color = if (isSystemInDarkTheme()) Color.Black else Color.LightGray,
                            offset = Offset(2f, 2f),
                            blurRadius = 5f
                        )
                    ),
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
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                val strokeTextStyle = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    drawStyle = Stroke(width = 2f, join = StrokeJoin.Round)
                )
                val fillTextStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)

                Text(
                    text = payment.paidBy.name,
                    style = strokeTextStyle,
                    color = Color.Black,
                )
                Text(
                    text = payment.paidBy.name,
                    style = fillTextStyle,
                    color = personColor,
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
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
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
}

@Preview
@Composable
fun PaymentRowPreview() {
    FamigliABTheme {
        PaymentRow(
            payment = Payment(
                date = Date(),
                description = "description",
                amount = 12.34,
                paidBy = Person.FAB,
                category = Category.CIBO
            ),
            dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault()),
            colors = mapOf(Category.CIBO.name to Color.Red),
            onPaymentLongClick = {}
        )
    }
}
