package com.fabiobassi.famigliab.ui.features.budgeting.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.Voucher
import com.fabiobassi.famigliab.R

@Composable
fun VoucherSummaryCard(vouchers: List<Voucher>, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalGroceryStore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.vouchers_used),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (vouchers.isNotEmpty()) {
                val (voucherCounts, voucherValues) = remember(vouchers) {
                    val fabVouchers = vouchers.filter { it.whose == Person.FAB }
                    val sabVouchers = vouchers.filter { it.whose == Person.SAB }

                    val fabVouchersCount = fabVouchers.sumOf { it.numberUsed }
                    val sabVouchersCount = sabVouchers.sumOf { it.numberUsed }
                    val counts = listOf(fabVouchersCount + sabVouchersCount, fabVouchersCount, sabVouchersCount)

                    val fabVouchersValue = fabVouchers.sumOf { it.value * it.numberUsed }
                    val sabVouchersValue = sabVouchers.sumOf { it.value * it.numberUsed }
                    val values = listOf(fabVouchersValue + sabVouchersValue, fabVouchersValue, sabVouchersValue)
                    counts to values
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    VoucherRow(label = "Fab", count = voucherCounts[1], value = voucherValues[1])
                    VoucherRow(label = "Sab", count = voucherCounts[2], value = voucherValues[2])
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.total),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = pluralStringResource(
                            R.plurals.vouchers_count,
                            voucherCounts[0],
                            voucherCounts[0],
                            voucherValues[0]
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.no_vouchers_used),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun VoucherRow(label: String, count: Int, value: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = pluralStringResource(R.plurals.vouchers_count, count, count, value),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
