package com.fabiobassi.famigliab.ui.features.budgeting.dialogs

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.core.graphics.toColorInt
import com.fabiobassi.famigliab.data.Income
import com.fabiobassi.famigliab.data.Person
import com.fabiobassi.famigliab.data.SettingsDataStore

@Composable
fun IncomeDialog(
    incomes: List<Income>,
    onDismiss: () -> Unit,
    onDeleteIncome: (Income) -> Unit,
) {
    var incomeToDelete by remember { mutableStateOf<Income?>(null) }

    incomeToDelete?.let { income ->
        DeleteIncomeConfirmationDialog(
            income = income,
            onConfirm = {
                onDeleteIncome(income)
                incomeToDelete = null
            },
            onDismiss = { incomeToDelete = null }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Payments,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Text(
                text = "Income Details",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (incomes.isEmpty()) {
                    Text(
                        text = "No income records found.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(incomes) { income ->
                            IncomeItem(
                                income = income,
                                onLongClick = { incomeToDelete = it }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IncomeItem(
    income: Income,
    onLongClick: (Income) -> Unit
) {
    val context = LocalContext.current
    val settingsDataStore = remember { SettingsDataStore(context) }
    val personColorHex by settingsDataStore.getColorFor(income.paidTo.name)
        .collectAsState(initial = "")

    val personColor = if (personColorHex.isNotEmpty()) {
        Color(personColorHex.toColorInt())
    } else {
        MaterialTheme.colorScheme.primary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = { onLongClick(income) }
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = income.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    Text(
                        text = income.paidTo.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = personColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(personColor.copy(alpha = 0.12f), MaterialTheme.shapes.extraSmall)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                    Text(
                        text = "%.2f €".format(income.amount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteIncomeConfirmationDialog(
    income: Income,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Income Record") },
        text = {
            Text(
                "Are you sure you want to permanently delete the income record for \"${income.description}\" (%.2f €)?".format(income.amount)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun IncomeDialogPreview() {
    val mockIncomes = listOf(
        Income("Monthly Salary", 2450.0, Person.FAB),
        Income("Freelance Project", 350.0, Person.SAB),
        Income("Bonus", 100.0, Person.FAB),
    )
    IncomeDialog(
        incomes = mockIncomes,
        onDismiss = {},
        onDeleteIncome = {}
    )
}
