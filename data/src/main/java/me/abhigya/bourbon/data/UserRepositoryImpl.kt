package me.abhigya.bourbon.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import me.abhigya.bourbon.domain.UserRepository
import me.abhigya.bourbon.domain.entities.User

class UserRepositoryImpl : UserRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun isLoggedIn(): Flow<Boolean> {
        return flowOf(auth.currentUser != null)
    }

    override fun currentUser(): Flow<User> {
        return auth.currentUser?.let {
            flowOf(User(it.displayName ?: "", it.email ?: ""))
        } ?: emptyFlow()
    }

    override fun googleLogin(googleSignInToken: String): Flow<Result<Boolean>> = flow {
        emit(runCatching {
            auth.signInWithCredential(
                GoogleAuthProvider.getCredential(googleSignInToken, null)
            )
                .addOnFailureListener {
                    throw it
                }
                .await()
            true
        })
    }

}