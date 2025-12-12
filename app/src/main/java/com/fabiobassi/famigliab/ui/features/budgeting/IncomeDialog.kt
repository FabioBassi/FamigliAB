package com.fabiobassi.famigliab.ui.features.budgeting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.fabiobassi.famigliab.data.Income
import com.fabiobassi.famigliab.data.Person

@OptIn(ExperimentalFoundationApi::class)
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

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Incomes",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(incomes) { income ->
                        IncomeItem(
                            income = income,
                            onLongClick = { incomeToDelete = it }
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IncomeItem(
    income: Income,
    onLongClick: (Income) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = { onLongClick(income) }
            )
    ) {
        Text(
            text = income.description,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = income.paidTo.name,
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "%.2f €".format(income.amount),
                textAlign = TextAlign.End,
                fontSize = 18.sp,
                modifier = Modifier.weight(3f)
            )
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
        title = { Text("Delete Income") },
        text = { Text("Are you sure you want to delete this income?\n'${income.description}' of ${income.amount}€") },
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

@Preview(showBackground = true)
@Composable
fun IncomeDialogPreview() {
    val mockIncomes = listOf(
        Income("Stipendio", 2000.0, Person.FAB),
        Income("Stipendio", 1500.0, Person.SAB),
        Income("Bonus", 200.0, Person.FAB),
    )
    IncomeDialog(
        incomes = mockIncomes,
        onDismiss = {},
        onDeleteIncome = {}
    )
}