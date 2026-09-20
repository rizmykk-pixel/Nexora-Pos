package com.nexova.pos.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nexova.pos.core.data.session.SessionManager
import com.nexova.pos.core.network.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val sessionStatus: StateFlow<SessionStatus> = authRepository.observeAuthState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SessionStatus.Initializing)

    val isLoggedIn: StateFlow<Boolean> = sessionStatus.map { it is SessionStatus.Authenticated }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun onGoogleSignInSuccess(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                authRepository.signInWithGoogle(idToken)
                val user = authRepository.currentUser
                if (user != null) {
                    // Note: Supabase handles session persistence, but we can also store basic info in SessionManager
                    // sessionManager.saveSession(...)
                    _uiState.value = LoginUiState.Success
                } else {
                    _uiState.value = LoginUiState.Error("Gagal mengambil data pengguna")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Terjadi kesalahan saat login")
            }
        }
    }
}

sealed interface LoginUiState {
    object Initial : LoginUiState
    object Loading : LoginUiState
    object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}
