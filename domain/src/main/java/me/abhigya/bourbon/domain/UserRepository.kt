package me.abhigya.bourbon.domain

import kotlinx.coroutines.flow.Flow
import me.abhigya.bourbon.domain.entities.User

interface UserRepository {

    fun isLoggedIn(): Flow<Boolean>

    fun currentUser(): Flow<User>

    fun googleLogin(googleSignInToken: String): Flow<Result<Boolean>>

}