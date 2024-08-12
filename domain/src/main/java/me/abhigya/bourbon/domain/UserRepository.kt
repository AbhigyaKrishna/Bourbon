package me.abhigya.bourbon.domain

import android.content.Context
import kotlinx.coroutines.flow.Flow
import me.abhigya.bourbon.domain.entities.Diet
import me.abhigya.bourbon.domain.entities.Exercise
import me.abhigya.bourbon.domain.entities.User
import me.abhigya.bourbon.domain.entities.UserData
import java.time.DayOfWeek

interface UserRepository {

    val isLoaded: Boolean

    fun isLoggedIn(): Flow<Boolean>

    fun currentUser(): Flow<User>

    fun exists(email: String): Flow<Boolean>

    fun signIn(): SignIn

    fun signUp(): SignUp

    fun signOut(): Flow<Result<Unit>>

    suspend fun loadUserFully()

    fun hasData(user: User): Flow<Boolean>

    fun loadUserData(user: User): Flow<Result<UserData>>

    fun saveData(user: User): Flow<Result<Unit>>

    fun saveExercises(user: User): Flow<Result<Unit>>

    fun loadExercises(user: User): Flow<Result<Map<DayOfWeek, List<Exercise>>>>

    fun saveDiet(user: User): Flow<Result<Unit>>

    fun loadDiet(user: User): Flow<Result<Map<DayOfWeek, Diet>>>

    interface SignIn {

        fun withEmailAndPassword(email: String, password: String): Flow<Result<Unit>>

        fun withGoogle(context: Context): Flow<Result<Unit>>

    }

    interface SignUp {

        fun withEmailAndPassword(email: String, password: String): Flow<Result<Unit>>

    }

}