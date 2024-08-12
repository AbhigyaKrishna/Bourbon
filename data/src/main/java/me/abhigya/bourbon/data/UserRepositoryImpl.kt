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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.tasks.await
import me.abhigya.bourbon.data.firebase.get
import me.abhigya.bourbon.data.firebase.getAsFlow
import me.abhigya.bourbon.data.firebase.handle
import me.abhigya.bourbon.data.firebase.handleAsResult
import me.abhigya.bourbon.data.firebase.value
import me.abhigya.bourbon.domain.UserRepository
import me.abhigya.bourbon.domain.entities.Diet
import me.abhigya.bourbon.domain.entities.Exercise
import me.abhigya.bourbon.domain.entities.User
import me.abhigya.bourbon.domain.entities.UserData
import org.koin.core.component.KoinComponent
import java.time.DayOfWeek
import java.util.UUID

class UserRepositoryImpl(applicationContext: Context) : UserRepository, KoinComponent {

    private val database: FirebaseDatabase = Firebase.database(applicationContext.getString(R.string.database_url))
    private val auth: FirebaseAuth = Firebase.auth
    private var userCache: User? = null

    override val isLoaded: Boolean get() = userCache != null

    override fun isLoggedIn(): Flow<Boolean> {
        return flowOf(auth.currentUser != null)
    }

    override fun currentUser(): Flow<User> {
        return flowOf( userCache ?: auth.currentUser?.into()?.getOrNull() ?: return emptyFlow())
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

    override suspend fun loadUserFully() {
        val user = currentUser().single()
        val data = loadUserData(user).flowOn(Dispatchers.IO).single()
        val exercises = loadExercises(user).flowOn(Dispatchers.IO).single()
        val diet = loadDiet(user).flowOn(Dispatchers.IO).single()

        userCache = user.copy(
            data = data.getOrThrow(),
            exercises = exercises.getOrThrow(),
            diet = diet.getOrThrow()
        )
    }

    override fun hasData(user: User): Flow<Boolean> {
        return database
            .getReference("userdata")
            .child(user.uid)
            .getAsFlow()
            .map { it.value != null }
            .catch { emit(false) }
    }

    override fun loadUserData(user: User): Flow<Result<UserData>> {
        return database
            .getReference("userdata")
            .child(user.uid)
            .get<UserData>()
            .onEach { data -> userCache?.let { userCache = it.copy(data = data) } }
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    override fun saveData(user: User): Flow<Result<Unit>> = flow {
        emitCatching {
            database
                .getReference("userdata")
                .child(user.uid)
                .value(user.data)
            userCache?.let { userCache = it.copy(data = user.data) }
        }
    }

    override fun saveExercises(user: User): Flow<Result<Unit>> = flow {
        emitCatching {
            database
                .getReference("exercises")
                .child(user.uid)
                .value(user.exercises)
            userCache?.let { userCache = it.copy(exercises = user.exercises) }
        }
    }

    override fun loadExercises(user: User): Flow<Result<Map<DayOfWeek, List<Exercise>>>> {
        return database
            .getReference("exercises")
            .child(user.uid)
            .get<Map<DayOfWeek, List<Exercise>>>()
            .onEach { exercise -> userCache?.let { userCache = it.copy(exercises = exercise) } }
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    override fun saveDiet(user: User): Flow<Result<Unit>> = flow {
        emitCatching {
            database
                .getReference("diets")
                .child(user.uid)
                .value(user.diet)
            userCache?.let { userCache = it.copy(diet = user.diet) }
        }
    }

    override fun loadDiet(user: User): Flow<Result<Map<DayOfWeek, Diet>>> {
        return database
            .getReference("diets")
            .child(user.uid)
            .get<Map<DayOfWeek, Diet>>()
            .onEach { diet -> userCache?.let { userCache = it.copy(diet = diet) } }
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
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
        ))
    }

    private suspend fun requestGoogleSignIn(context: Context): Result<GoogleIdTokenCredential> {
        return runCatching {
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