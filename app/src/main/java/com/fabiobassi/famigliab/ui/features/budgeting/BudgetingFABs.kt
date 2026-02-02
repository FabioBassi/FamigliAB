package com.fabiobassi.famigliab.ui.features.budgeting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BudgetingFABs(
    onAddPaymentClick: () -> Unit,
    onAddIncomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
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
    }
}
