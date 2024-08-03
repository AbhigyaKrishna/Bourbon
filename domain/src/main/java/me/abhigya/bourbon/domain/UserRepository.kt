package me.abhigya.bourbon.domain

import kotlinx.coroutines.flow.Flow
import me.abhigya.bourbon.domain.entities.User
import me.abhigya.bourbon.domain.entities.UserData

interface UserRepository {

    fun isLoggedIn(): Flow<Boolean>

    fun currentUser(): Flow<User>

    fun exists(email: String): Flow<Boolean>

    fun signIn(): SignIn

    fun signUp(): SignUp

    fun signOut(): Flow<Result<Unit>>

    fun hasData(user: User): Flow<Boolean>

    fun loadUserData(user: User): Flow<Result<UserData>>

    fun saveData(user: User): Flow<Result<Unit>>

    interface SignIn {

        fun withEmailAndPassword(email: String, password: String): Flow<Result<Unit>>

        fun withGoogle(): Flow<Result<Unit>>

    }

    interface SignUp {

        fun withEmailAndPassword(email: String, password: String): Flow<Result<Unit>>

    }

}