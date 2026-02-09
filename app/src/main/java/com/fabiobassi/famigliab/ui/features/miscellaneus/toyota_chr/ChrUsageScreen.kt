package com.fabiobassi.famigliab.ui.features.miscellaneus.toyota_chr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

        item {
            UsageTable(entries = entries)
        }
    }
}

@Composable
fun UsageTable(
    entries: List<ChrUsage>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableHeaderText(text = "Week", modifier = Modifier.weight(1f))
                TableHeaderText(text = stringResource(R.string.date), modifier = Modifier.weight(1.5f))
                TableHeaderText(text = "Expected", modifier = Modifier.weight(1.2f))
                TableHeaderText(text = "Actual", modifier = Modifier.weight(1.2f))
            }

            HorizontalDivider(thickness = 2.dp)

            entries.forEach { entry ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCellText(text = entry.week.toString(), modifier = Modifier.weight(1f))
                    TableCellText(text = entry.date, modifier = Modifier.weight(1.5f))
                    TableCellText(text = "${entry.expectedKm} km", modifier = Modifier.weight(1.2f))
                    TableCellText(
                        text = entry.actualKm?.let { "$it km" } ?: "-",
                        modifier = Modifier.weight(1.2f),
                        fontWeight = FontWeight.Bold
                    )
                }
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun TableHeaderText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
}

@Composable
fun TableCellText(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null
) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        fontWeight = fontWeight
    )
}
