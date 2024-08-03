package me.abhigya.bourbon.data

import me.abhigya.bourbon.domain.UserRepository
import org.koin.dsl.module

val dataModules = module {
    single<UserRepository> { UserRepositoryImpl() }
}