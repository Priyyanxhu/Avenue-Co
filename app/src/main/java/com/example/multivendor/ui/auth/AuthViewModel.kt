package com.example.multivendor.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multivendor.data.AppContainer
import com.example.multivendor.data.auth.AuthRepository
import com.example.multivendor.data.auth.AuthResult
import com.example.multivendor.data.auth.AuthValidator
import com.example.multivendor.data.auth.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

/** Holds login / registration form state and talks to the [AuthRepository]. */
class AuthViewModel(
    private val auth: AuthRepository? = runCatching { AppContainer.authRepository }.getOrNull()
) : ViewModel() {

    val currentUser: Flow<User?> = auth?.currentUser ?: emptyFlow()
    val isLoggedIn: Flow<Boolean> = auth?.isLoggedIn ?: emptyFlow()
    val sessionReady: Flow<Boolean> = auth?.sessionReady ?: emptyFlow()

    // ---- Login form ----
    var loginEmail by mutableStateOf("")
        private set
    var loginPassword by mutableStateOf("")
        private set
    var loginPasswordVisible by mutableStateOf(false)
        private set

    // ---- Registration form ----
    var regName by mutableStateOf("")
        private set
    var regEmail by mutableStateOf("")
        private set
    var regPhone by mutableStateOf("")
        private set
    var regPassword by mutableStateOf("")
        private set
    var regConfirm by mutableStateOf("")
        private set
    var regPasswordVisible by mutableStateOf(false)
        private set

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun onLoginEmailChange(v: String) { loginEmail = v; _error.value = null }
    fun onLoginPasswordChange(v: String) { loginPassword = v; _error.value = null }
    fun toggleLoginPasswordVisibility() { loginPasswordVisible = !loginPasswordVisible }

    fun onRegNameChange(v: String) { regName = v; _error.value = null }
    fun onRegEmailChange(v: String) { regEmail = v; _error.value = null }
    fun onRegPhoneChange(v: String) { regPhone = v; _error.value = null }
    fun onRegPasswordChange(v: String) { regPassword = v; _error.value = null }
    fun onRegConfirmChange(v: String) { regConfirm = v; _error.value = null }
    fun toggleRegPasswordVisibility() { regPasswordVisible = !regPasswordVisible }

    fun clearError() { _error.value = null }

    fun login(onSuccess: () -> Unit) {
        val validationError = AuthValidator.validateLogin(loginEmail, loginPassword)
        if (validationError != null) {
            _error.value = validationError
            return
        }
        val repo = auth ?: run { _error.value = "Auth is not initialized yet."; return }
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            when (val result = repo.login(loginEmail, loginPassword)) {
                is AuthResult.Success -> {
                    loginPassword = ""
                    onSuccess()
                }
                is AuthResult.Error -> _error.value = result.message
            }
            _isLoading.value = false
        }
    }

    fun register(onSuccess: () -> Unit) {
        val validationError = AuthValidator.validateRegistration(
            regName, regEmail, regPhone, regPassword, regConfirm
        )
        if (validationError != null) {
            _error.value = validationError
            return
        }
        val repo = auth ?: run { _error.value = "Auth is not initialized yet."; return }
        _isLoading.value = true
        _error.value = null
        viewModelScope.launch {
            when (val result = repo.register(regName, regEmail, regPhone, regPassword)) {
                is AuthResult.Success -> {
                    regPassword = ""
                    regConfirm = ""
                    onSuccess()
                }
                is AuthResult.Error -> _error.value = result.message
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch { auth?.logout() }
    }
}
