package com.fabiobassi.famigliab.ui.features.passwords

import kotlinx.serialization.Serializable

@Serializable
data class PasswordItem(
    val title: String,
    val arguments: List<Pair<String, String>>,
)
