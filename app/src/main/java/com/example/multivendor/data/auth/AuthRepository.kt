package com.example.multivendor.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.multivendor.data.local.MarketplaceDatabase
import com.example.multivendor.data.local.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")
private val LOGGED_IN_USER_ID = longPreferencesKey("logged_in_user_id")
private const val NO_USER = -1L

sealed interface AuthResult {
    data class Success(val user: User) : AuthResult
    data class Error(val message: String) : AuthResult
}

interface AuthRepository {
    val currentUser: Flow<User?>
    val isLoggedIn: Flow<Boolean>
    /** True once the persisted session has been read (avoids flashing login on cold start). */
    val sessionReady: Flow<Boolean>
    suspend fun register(name: String, email: String, phone: String, password: String): AuthResult
    suspend fun login(email: String, password: String): AuthResult
    suspend fun logout()
}

/** Auth backed by the app's own Room database + DataStore session. */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class RoomAuthRepository(context: Context, db: MarketplaceDatabase) : AuthRepository {
    private val appContext = context.applicationContext
    private val userDao = db.userDao()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _sessionReady = kotlinx.coroutines.flow.MutableStateFlow(false)
    override val sessionReady: Flow<Boolean> = _sessionReady

    private val loggedInId: Flow<Long> = appContext.authDataStore.data
        .map { prefs -> prefs[LOGGED_IN_USER_ID] ?: NO_USER }
        .onEach { _sessionReady.value = true }

    override val currentUser: Flow<User?> = loggedInId.flatMapLatest { id ->
        if (id == NO_USER) flowOf(null) else userDao.observeById(id).map { it?.toUser() }
    }.stateIn(scope, SharingStarted.Eagerly, null)

    override val isLoggedIn: Flow<Boolean> = currentUser
        .map { it != null }
        .stateIn(scope, SharingStarted.Eagerly, false)

    override suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): AuthResult {
        val normalizedEmail = AuthValidator.normalizeEmail(email)
        if (userDao.getByEmail(normalizedEmail) != null) {
            return AuthResult.Error("An account with this email already exists. Please log in.")
        }
        val salt = PasswordHasher.generateSalt()
        val id = userDao.insert(
            UserEntity(
                name = name.trim(),
                email = normalizedEmail,
                phone = phone.trim(),
                passwordHash = PasswordHasher.hash(password, salt),
                passwordSalt = salt
            )
        )
        saveSession(id)
        val user = userDao.getById(id)?.toUser()
            ?: return AuthResult.Error("Registration succeeded but loading the profile failed.")
        return AuthResult.Success(user)
    }

    override suspend fun login(email: String, password: String): AuthResult {
        val normalizedEmail = AuthValidator.normalizeEmail(email)
        val entity = userDao.getByEmail(normalizedEmail)
            ?: return AuthResult.Error("No account found with this email. Please register first.")
        if (!PasswordHasher.verify(password, entity.passwordSalt, entity.passwordHash)) {
            return AuthResult.Error("Incorrect password. Please try again.")
        }
        saveSession(entity.id)
        return AuthResult.Success(entity.toUser())
    }

    override suspend fun logout() {
        appContext.authDataStore.edit { prefs -> prefs[LOGGED_IN_USER_ID] = NO_USER }
    }

    private suspend fun saveSession(userId: Long) {
        appContext.authDataStore.edit { prefs -> prefs[LOGGED_IN_USER_ID] = userId }
    }

    private fun UserEntity.toUser() = User(
        id = id,
        name = name,
        email = email,
        phone = phone,
        createdAt = createdAt
    )
}
