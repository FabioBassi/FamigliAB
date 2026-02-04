package com.fabiobassi.famigliab.ui.features.passwords

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKey
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class PasswordRepository(private val context: Context) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    // The new, private, encrypted file location
    private val encryptedFile = File(context.filesDir, "passwords.json")

    // The old, public, plain-text location (for migration and export)
    private val legacyFile = File(context.getExternalFilesDir("FamigliAB"), "passwords.json")

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    init {
        migrateIfNeeded()
    }

    private fun migrateIfNeeded() {
        try {
            if (legacyFile.exists() && !encryptedFile.exists()) {
                Log.d("PasswordRepository", "Migrating legacy plain-text passwords to encrypted storage...")
                val legacyText = legacyFile.readText()
                if (legacyText.isNotBlank()) {
                    val passwords = json.decodeFromString<List<PasswordItem>>(legacyText)
                    savePasswords(passwords)
                    legacyFile.delete()
                    Log.d("PasswordRepository", "Migration successful. Legacy file deleted.")
                }
            }
        } catch (e: Exception) {
            Log.e("PasswordRepository", "Migration failed", e)
        }
    }

    private fun getEncryptedFileInstance(): EncryptedFile {
        return EncryptedFile.Builder(
            context,
            encryptedFile,
            masterKey,
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
    }

    fun savePasswords(passwords: List<PasswordItem>) {
        try {
            // EncryptedFile requires the file to NOT exist before writing
            if (encryptedFile.exists()) {
                encryptedFile.delete()
            }

            val jsonString = json.encodeToString(passwords)
            getEncryptedFileInstance().openFileOutput().use { outputStream ->
                outputStream.write(jsonString.toByteArray(Charsets.UTF_8))
            }
        } catch (e: Exception) {
            Log.e("PasswordRepository", "Error saving encrypted passwords", e)
        }
    }

    fun loadPasswords(): List<PasswordItem> {
        if (!encryptedFile.exists()) return emptyList()

        return try {
            getEncryptedFileInstance().openFileInput().use { inputStream ->
                val jsonString = inputStream.readBytes().decodeToString()
                if (jsonString.isNotBlank()) {
                    json.decodeFromString<List<PasswordItem>>(jsonString)
                } else {
                    emptyList()
                }
            }
        } catch (e: Exception) {
            Log.e("PasswordRepository", "Error loading encrypted passwords", e)
            emptyList()
        }
    }

    /**
     * Exports the encrypted passwords to a plain-text JSON file in external storage.
     * This file can then be shared or saved by the user.
     */
    fun exportToPlainText(): File? {
        return try {
            val passwords = loadPasswords()
            val jsonString = json.encodeToString(passwords)
            
            if (!legacyFile.parentFile!!.exists()) {
                legacyFile.parentFile!!.mkdirs()
            }
            legacyFile.writeText(jsonString)
            legacyFile
        } catch (e: Exception) {
            Log.e("PasswordRepository", "Error exporting passwords to plain text", e)
            null
        }
    }
}
