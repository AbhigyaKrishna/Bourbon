package me.abhigya.bourbon.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import me.abhigya.bourbon.domain.UserRepository
import me.abhigya.bourbon.domain.entities.User
import me.abhigya.bourbon.domain.entities.UserData
import org.koin.core.component.KoinComponent
import java.util.UUID

class UserRepositoryImpl(
    private val context: Context
) : UserRepository, KoinComponent {

    private val database: FirebaseDatabase = Firebase.database
    private val auth: FirebaseAuth = Firebase.auth
    private val credentialManager: CredentialManager = CredentialManager.create(context)

    override fun isLoggedIn(): Flow<Boolean> {
        return flowOf(auth.currentUser != null)
    }

    override fun currentUser(): Flow<User> {
        return flowOf(auth.currentUser?.into()?.getOrNull() ?: return emptyFlow())
    }

    override fun exists(email: String): Flow<Boolean> = flow {
        runCatching {
            auth.fetchSignInMethodsForEmail(email)
        }.onFailure {
            emit(false)
        }.onSuccess {
            emit(it.isNotEmpty())
        }
    }

    override fun signIn(): UserRepository.SignIn {
        return SignInImpl()
    }

    override fun signUp(): UserRepository.SignUp {
        return SignUpImpl()
    }

    override fun signOut(): Flow<Result<Unit>> = flow {
        emitCaching {
            auth.signOut()
        }
    }

    override fun loadUserData(user: User): Flow<Result<UserData>> = flow {
        emitCaching {
            database.reference("userdata/${user.uid}")
                .valueEvents
                .first()
                .value<UserData>()
        }
    }

    override fun saveData(user: User): Flow<Result<Unit>> = flow {
        emitCaching {
            database.reference("userdata/${user.uid}")
                .setValue(user.data) {
                    encodeDefaults = true
                }
        }
    }

    inner class SignInImpl : UserRepository.SignIn {

        override fun withEmailAndPassword(email: String, password: String): Flow<Result<Unit>> = flow {
            emitCaching {
                auth.signInWithEmailAndPassword(email, password)
            }
        }

        override fun withGoogle(): Flow<Result<Unit>> = flow {
            emitCaching {
                val token = requestGoogleSignIn()
                    .getOrElse {
                        emit(Result.failure(it))
                        return@flow
                    }
                    .idToken
                auth.signInWithCredential(GoogleAuthProvider.credential(token, null))
            }
        }

    }

    inner class SignUpImpl : UserRepository.SignUp {

        override fun withEmailAndPassword(email: String, password: String): Flow<Result<Unit>> = flow {
            emitCaching {
                auth.createUserWithEmailAndPassword(email, password)
            }
        }

        override fun withGoogle(): Flow<Result<Unit>> = flow {
            emitCaching {
                val token = requestGoogleSignIn()
                    .getOrElse {
                        emit(Result.failure(it))
                        return@flow
                    }
                    .idToken
                auth.signInWithCredential(GoogleAuthProvider.credential(token, null))
            }
        }

    }

    private suspend inline fun <T> FlowCollector<Result<T>>.emitCaching(block: () -> T) {
        emit(runCatching(block))
    }

    private fun FirebaseUser.into(): Result<User> {
        return Result.success(User(
            uid,
            displayName ?: return Result.failure(IllegalStateException("Name is null")),
            email ?: return Result.failure(IllegalStateException("Email is null"))
        ))
    }

    private suspend fun requestGoogleSignIn(): Result<GoogleIdTokenCredential> {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(context.resources.getString(R.string.web_client_id))
            .setAutoSelectEnabled(true)
            .setNonce(UUID.randomUUID().toString())
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        return runCatching {
            credentialManager.getCredential(context, request).credential as GoogleIdTokenCredential
        }
    }
}