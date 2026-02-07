package com.fabiobassi.famigliab.ui.features.miscellaneus

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Start
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fabiobassi.famigliab.R

@Composable
fun MiscellaneousSelectionScreen(
    paddingValues: PaddingValues,
    onSelectionChange: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            MiscellaneousSection(
                title = stringResource(R.string.toyota_chr),
                icon = Icons.Default.DirectionsCar
            ) {
                MiscellaneousItem(
                    title = stringResource(R.string.mileage),
                    icon = Icons.Default.Start,
                    onClick = { onSelectionChange(1) }
                )
            }
        }
    }
}
