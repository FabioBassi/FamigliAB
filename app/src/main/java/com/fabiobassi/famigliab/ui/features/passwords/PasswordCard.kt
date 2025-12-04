package com.fabiobassi.famigliab.ui.features.passwords

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme

@Composable
fun PasswordCard(
    item: PasswordItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            item.arguments.forEach { (key, value) ->
                Text(
                    buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("$key: ")
                        }
                        append(value)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordCardPreview() {
    FamigliABTheme {
        PasswordCard(
            item = PasswordItem(
                title = "Sample Title",
                arguments = listOf("Key1" to "Value1", "Key2" to "Value2")
            ),
            onClick = {}
        )
    }
}
