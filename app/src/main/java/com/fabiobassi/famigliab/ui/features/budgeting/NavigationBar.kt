package com.fabiobassi.famigliab.ui.features.budgeting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ManageSearch
import androidx.compose.material.icons.filled.AreaChart
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    currentDate: Date,
    onMonthClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onAnnualReportClick: () -> Unit,
    isAnnualReport: Boolean
) {
    val monthFormat = remember(isAnnualReport) {
        SimpleDateFormat(if (isAnnualReport) "yyyy" else "MMMM", Locale.getDefault())
    }
    val yearFormat = remember { SimpleDateFormat("yyyy", Locale.getDefault()) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onPreviousMonthClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous"
                )
            }

            IconButton(
                onClick = onMonthClick,
                enabled = !isAnnualReport
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ManageSearch,
                    contentDescription = "Select Month"
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(MaterialTheme.shapes.small)
                    .clickable(enabled = !isAnnualReport, onClick = onMonthClick)
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = monthFormat.format(currentDate).uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!isAnnualReport) {
                    Text(
                        text = yearFormat.format(currentDate),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconToggleButton(
                checked = isAnnualReport,
                onCheckedChange = { onAnnualReportClick() }
            ) {
                Icon(
                    imageVector = if (isAnnualReport) Icons.Filled.Assessment else Icons.Filled.AreaChart,
                    contentDescription = "Toggle Annual Report"
                )
            }

            IconButton(onClick = onNextMonthClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next"
                )
            }
        }
    }
}
