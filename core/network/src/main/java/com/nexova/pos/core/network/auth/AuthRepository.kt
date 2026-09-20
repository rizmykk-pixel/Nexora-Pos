package com.nexova.pos.core.network.auth

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: Auth
) {
    suspend fun signInWithGoogle(idToken: String) {
        auth.signInWith(IDToken) {
            this.idToken = idToken
            this.provider = Google
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }

    fun observeAuthState() = auth.sessionStatus

    val currentUser = auth.currentUserOrNull()
}
