package com.fabiobassi.famigliab.ui.features.passwords

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.fabiobassi.famigliab.R
import com.fabiobassi.famigliab.ui.features.passwords.dialogs.AddPasswordDialog
import com.fabiobassi.famigliab.ui.features.passwords.dialogs.EditPasswordDialog
import com.fabiobassi.famigliab.ui.theme.FamigliABTheme

@Composable
fun PasswordsScreen(paddingValues: PaddingValues, isVisible: Boolean = true) {
    var isUnlocked by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var editingPassword by remember { mutableStateOf<PasswordItem?>(null) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val repository = remember { PasswordRepository(context) }
    
    val allPasswords = remember { mutableStateListOf<PasswordItem>() }

    fun authenticate() {
        val activity = context as? FragmentActivity ?: return
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isUnlocked = true
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED && errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        Toast.makeText(context, context.getString(R.string.biometric_error, errString), Toast.LENGTH_SHORT).show()
                    }
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.biometric_prompt_title))
            .setSubtitle(context.getString(R.string.biometric_prompt_subtitle))
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    LaunchedEffect(isVisible) {
        if (isVisible && !isUnlocked) {
            authenticate()
        }
    }

    // Reload passwords whenever the screen is resumed (e.g., coming back from Settings)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                allPasswords.clear()
                allPasswords.addAll(repository.loadPasswords().sortedBy { it.title })
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Save passwords only when the list content actually changes
    LaunchedEffect(allPasswords.toList()) {
        // Only save if the list is not empty or if it was intentionally cleared
        // This prevents overwriting the file with an empty list during initial load
        if (allPasswords.isNotEmpty()) {
            repository.savePasswords(allPasswords)
        }
    }

    if (!isUnlocked) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .clickable { authenticate() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.biometric_prompt_subtitle),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        return
    }

    if (showDialog) {
        AddPasswordDialog(
            onDismiss = { showDialog = false },
            onSave = {
                allPasswords.add(it)
                allPasswords.sortBy { password -> password.title }
                showDialog = false
            }
        )
    }

    editingPassword?.let { item ->
        EditPasswordDialog(
            item = item,
            onDismiss = { editingPassword = null },
            onSave = { updatedItem ->
                val index = allPasswords.indexOf(item)
                if (index != -1) {
                    allPasswords[index] = updatedItem
                    allPasswords.sortBy { password -> password.title }
                }
                editingPassword = null
            },
            onDelete = {
                allPasswords.remove(item)
                editingPassword = null
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(allPasswords) { password ->
                PasswordCard(
                    item = password,
                    onClick = { editingPassword = password }
                )
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(horizontal = 16.dp, vertical = 32.dp),
        ) {
            Icon(Icons.Filled.Add, stringResource(id = R.string.add_new_password))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordsScreenPreview() {
    FamigliABTheme {
        PasswordsScreen(paddingValues = PaddingValues(0.dp))
    }
}
