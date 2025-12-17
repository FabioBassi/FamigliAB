package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabiobassi.famigliab.data.Payment
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
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
                            Text(
                                text = payment.paidBy.name,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
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
