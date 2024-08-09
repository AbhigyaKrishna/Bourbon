package me.abhigya.bourbon.data

import me.abhigya.bourbon.domain.ExerciseRepository
import me.abhigya.bourbon.domain.UserRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModules = module {
    single<UserRepository> { UserRepositoryImpl(androidContext()) }

    single<ExerciseRepository> { ExerciseRepositoryImpl(androidContext()) }

}