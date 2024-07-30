package me.abhigya.bourbon.domain

import kotlinx.coroutines.flow.Flow
import me.abhigya.bourbon.domain.entities.User

interface UserRepository {

    suspend fun currentUser(): Flow<User>

}