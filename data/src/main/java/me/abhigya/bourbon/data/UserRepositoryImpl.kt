package me.abhigya.bourbon.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import me.abhigya.bourbon.data.firebase.handle
import me.abhigya.bourbon.data.firebase.handleAsResult
import me.abhigya.bourbon.data.firebase.value
import me.abhigya.bourbon.data.firebase.valueEventOnce
import me.abhigya.bourbon.data.firebase.valueOrThrow
import me.abhigya.bourbon.domain.UserRepository
import me.abhigya.bourbon.domain.entities.User
import me.abhigya.bourbon.domain.entities.UserData
import org.koin.core.component.KoinComponent
import java.util.UUID

class UserRepositoryImpl(applicationContext: Context) : UserRepository, KoinComponent {

    private val database: DatabaseReference = Firebase.database(applicationContext.getString(R.string.database_url))
        .getReference("bourbon")
    private val auth: FirebaseAuth = Firebase.auth
    private var userDataCache: UserData? = null

    override fun isLoggedIn(): Flow<Boolean> {
        return flowOf(auth.currentUser != null)
    }

    override fun currentUser(): Flow<User> {
        return flowOf(auth.currentUser?.into()?.getOrNull() ?: return emptyFlow())
    }

    override fun exists(email: String): Flow<Boolean> {
        return auth.fetchSignInMethodsForEmail(email)
            .handle({ false }) {
                trySendBlocking(it.signInMethods?.isNotEmpty() == true)
            }
    }

    override fun signIn(): UserRepository.SignIn {
        return SignInImpl()
    }

    override fun signUp(): UserRepository.SignUp {
        return SignUpImpl()
    }

    override fun signOut(): Flow<Result<Unit>> = flow {
        emitCatching {
            auth.signOut()
        }
    }

    override fun hasData(user: User): Flow<Boolean> = flow {
        runCatching {
            database.child("userdata/${user.uid}")
                .valueEventOnce()
                .firstOrNull()?.value != null
        }.onFailure {
            emit(false)
        }.onSuccess {
            emit(it)
        }
    }

    override fun loadUserData(user: User): Flow<Result<UserData>> {
        if (userDataCache != null) {
            return flowOf(Result.success(userDataCache!!))
        }
        return database.child("userdata/${user.uid}")
            .valueEventOnce()
            .map { Result.success(it.valueOrThrow<UserData>()) }
            .catch { emit(Result.failure(it)) }
    }

    override fun saveData(user: User): Flow<Result<Unit>> = flow {
        emitCatching {
            database.child("userdata/${user.uid}")
                .value(user.data)
        }
    }

    inner class SignInImpl : UserRepository.SignIn {

        override fun withEmailAndPassword(email: String, password: String): Flow<Result<Unit>> {
            return auth.signInWithEmailAndPassword(email, password)
                .handleAsResult {
                    it.user?.let { trySend(Result.success(Unit)) } ?: trySend(Result.failure(IllegalStateException("User is null")))
                }
        }

        override fun withGoogle(context: Context): Flow<Result<Unit>> {
            return flow {
                emitCatching {
                    requestGoogleSignIn(context).getOrElse {
                        emit(Result.failure(it))
                        return@flow
                    }.idToken
                }
            }
                .map {
                    auth.signInWithCredential(GoogleAuthProvider.getCredential(it.getOrThrow(), null)).await()
                    Result.success(Unit)
//                        .handleAsResult {
//                            Log.d("UserRepositoryImpl", "User: ${it.user}")
//                            if (it.user == null) {
//                                trySendBlocking(Result.failure(IllegalStateException("User is null")))
//                            } else {
//                                trySendBlocking(Result.success(Unit))
//                            }
//                        }
//                        .collect(this)
                }
            .catch {
                emit(Result.failure(it))
            }
        }

    }

    inner class SignUpImpl : UserRepository.SignUp {

        override fun withEmailAndPassword(email: String, password: String): Flow<Result<Unit>> {
            return auth.createUserWithEmailAndPassword(email, password)
                .handleAsResult {
                    it.user?.let { trySend(Result.success(Unit)) } ?: trySend(Result.failure(IllegalStateException("User is null")))
                }
        }

    }

    private suspend inline fun <T> FlowCollector<Result<T>>.emitCatching(block: () -> T) {
        emit(runCatching(block))
    }

    private fun FirebaseUser.into(): Result<User> {
        return Result.success(User(
            uid,
            displayName ?: return Result.failure(IllegalStateException("Name is null")),
            email ?: return Result.failure(IllegalStateException("Email is null")),
            userDataCache ?: UserData()
        ))
    }

    private suspend fun requestGoogleSignIn(context: Context): Result<GoogleIdTokenCredential> {
        return runCatching {
//            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
//                .setServerClientId(context.resources.getString(R.string.web_client_id))
//                .setAutoSelectEnabled(true)
//                .setNonce(UUID.randomUUID().toString())
//                .build()
            val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(context.resources.getString(R.string.web_client_id))
                .setNonce(UUID.randomUUID().toString())
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(signInWithGoogleOption)
                .build()
            val credentialManager = CredentialManager.create(context.applicationContext)
            val credentials = credentialManager.getCredential(context, request).credential as CustomCredential

            GoogleIdTokenCredential.createFrom(credentials.data)
        }
    }
}