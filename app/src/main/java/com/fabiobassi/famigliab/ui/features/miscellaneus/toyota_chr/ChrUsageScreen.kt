package com.fabiobassi.famigliab.ui.features.miscellaneus.toyota_chr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.data.ChrUsage

@Composable
fun ChrUsageScreen(
    paddingValues: PaddingValues,
    onBackToSelection: () -> Unit,
    viewModel: ChrUsageViewModel = viewModel(factory = ChrUsageViewModel.Factory)
) {
    val entries by viewModel.entries.collectAsState()
    var selectedEntry by remember { mutableStateOf<ChrUsage?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            TextButton(
                onClick = onBackToSelection,
                contentPadding = PaddingValues(0.dp),
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                Text(stringResource(R.string.go_back), modifier = Modifier.padding(start = 8.dp))
            }
        }

        item {
            Text(
                text = stringResource(R.string.mileage),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        if (entries.isNotEmpty()) {
            item {
                UsageChartCard(entries = entries)
            }
        }

        item {
            UsageTable(
                entries = entries,
                onEntryClick = { selectedEntry = it }
            )
        }
    }

    selectedEntry?.let { entry ->
        EditKmDialog(
            entry = entry,
            onDismiss = { selectedEntry = null },
            onConfirm = { updatedEntry ->
                viewModel.updateEntry(updatedEntry)
                selectedEntry = null
            }
        )
    }
}
