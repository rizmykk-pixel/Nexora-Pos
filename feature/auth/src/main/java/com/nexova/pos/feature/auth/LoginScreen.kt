package com.nexova.pos.feature.auth

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.nexova.pos.core.designsystem.components.NexovaButton
import com.nexova.pos.core.designsystem.components.NexovaCard
import com.nexova.pos.core.domain.WindowSizeClass
import kotlinx.coroutines.launch

@Composable
fun LoginRoute(
    viewModel: LoginViewModel,
    windowSizeClass: WindowSizeClass,
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess()
        }
    }

    LoginScreen(
        uiState = uiState,
        windowSizeClass = windowSizeClass,
        onGoogleSignInClick = {
            scope.launch {
                handleGoogleSignIn(context, viewModel)
            }
        }
    )
}

@Composable
private fun LoginScreen(
    uiState: LoginUiState,
    windowSizeClass: WindowSizeClass,
    onGoogleSignInClick: () -> Unit
) {
    val isTablet = windowSizeClass != WindowSizeClass.COMPACT

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val content = @Composable {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "NEXOVA POS",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "JUALAN LEBIH MUDAH. BISNIS MAKIN MAJU.",
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(48.dp))

                if (uiState is LoginUiState.Loading) {
                    CircularProgressIndicator()
                } else {
                    NexovaButton(
                        onClick = onGoogleSignInClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Masuk dengan Google")
                    }
                }

                if (uiState is LoginUiState.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        if (isTablet) {
            NexovaCard(modifier = Modifier.size(width = 400.dp, height = 500.dp)) {
                content()
            }
        } else {
            content()
        }
    }
}

private suspend fun handleGoogleSignIn(context: Context, viewModel: LoginViewModel) {
    val credentialManager = CredentialManager.create(context)
    
    // Supabase Auth Google Provider Web Client ID
    val googleIdTokenOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId("758618382869-3655199ppa5v3jb1faa5j9702gpka96t.apps.googleusercontent.com")
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdTokenOption)
        .build()

    try {
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential
        
        if (credential is GoogleIdTokenCredential) {
            viewModel.onGoogleSignInSuccess(credential.idToken)
        }
    } catch (e: Exception) {
        // Handle login failure
    }
}
