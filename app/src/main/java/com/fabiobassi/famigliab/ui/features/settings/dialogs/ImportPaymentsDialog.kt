package com.fabiobassi.famigliab.ui.features.settings.dialogs

import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.data.Payment
import com.fabiobassi.famigliab.file.CsvFileManager
import com.fabiobassi.famigliab.file.CsvFileType
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportPaymentsDialog(
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val csvFileManager = remember { CsvFileManager(context) }
    var selectedMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var selectedYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var monthExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }
    var separator by remember { mutableStateOf(";") }

    val months = (1..12).toList()
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear - 10..currentYear).toList()

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri ->
        uri?.let {
            val fileName = context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex)
            }

            if (fileName?.endsWith(".csv", ignoreCase = true) == true) {
                try {
                    context.contentResolver.openInputStream(it)?.use { inputStream ->
                        val payments = mutableListOf<Payment>()

                        val rows = csvReader {
                            delimiter = separator.first()
                        }.readAll(inputStream)

                        for (row in rows) {
                            val payment = Payment.fromCsvRow(row)
                            if (payment != null) {
                                payments.add(payment)
                            }
                        }

                        val calendar = Calendar.getInstance().apply {
                            set(Calendar.YEAR, selectedYear)
                            set(Calendar.MONTH, selectedMonth)
                        }
                        csvFileManager.writeData(CsvFileType.PAYMENTS, calendar.time, payments)
                        Toast.makeText(context, "Imported ${payments.size} payments.", Toast.LENGTH_LONG).show()
                    }
                    onDismissRequest()
                } catch (e: Exception) {
                    Toast.makeText(context, "Error importing CSV: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(context, "Invalid file type. Please select a CSV file.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Import Payments CSV") },
        text = {
            Column {
                Text("Select month and year to import the payments for.")
                Text("Dates in the csv file must be in the format dd/mm/yy")
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = monthExpanded,
                        onExpandedChange = { monthExpanded = !monthExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        TextField(
                            value = months[selectedMonth].toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Month") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = monthExpanded,
                            onDismissRequest = { monthExpanded = false }
                        ) {
                            months.forEachIndexed { index, month ->
                                DropdownMenuItem(
                                    text = { Text(month.toString()) },
                                    onClick = {
                                        selectedMonth = index
                                        monthExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    ExposedDropdownMenuBox(
                        expanded = yearExpanded,
                        onExpandedChange = { yearExpanded = !yearExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        TextField(
                            value = selectedYear.toString(),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Year") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = yearExpanded,
                            onDismissRequest = { yearExpanded = false }
                        ) {
                            years.forEach { year ->
                                DropdownMenuItem(
                                    text = { Text(year.toString()) },
                                    onClick = {
                                        selectedYear = year
                                        yearExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Input the separator used in the csv file.")
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = separator,
                    onValueChange = { separator = it },
                    label = { Text("Separator") },
                )
            }
        },
        confirmButton = {
            Button(onClick = { importLauncher.launch("*/*") }) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ImportPaymentsDialogPreview() {
    FamigliABTheme {
        ImportPaymentsDialog(onDismissRequest = {})
    }
}
