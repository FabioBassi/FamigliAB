package com.fabiobassi.famigliab.ui.features.passwords

import android.content.Context
import android.util.Log
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class PasswordRepository(context: Context) {

    private val file: File?

    init {
        val storageDir = context.getExternalFilesDir("FamigliAB")
        if (storageDir == null) {
            Log.e("PasswordRepository", "External storage not available")
            file = null
        } else {
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            file = File(storageDir, "passwords.json")
        }
    }

    fun savePasswords(passwords: List<PasswordItem>) {
        file?.let {
            val json = Json { prettyPrint = true }
            val jsonString = json.encodeToString(passwords)
            it.writeText(jsonString)
        }
    }

    fun loadPasswords(): List<PasswordItem> {
        file?.let {
            return if (it.exists()) {
                val jsonString = it.readText()
                if (jsonString.isNotBlank()) {
                    Json.decodeFromString<List<PasswordItem>>(jsonString)
                } else {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
        return emptyList()
    }
}
