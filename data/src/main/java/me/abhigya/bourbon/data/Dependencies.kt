package me.abhigya.bourbon.data

import android.content.Context
import me.abhigya.bourbon.domain.UserRepository
import org.koin.dsl.module

val dataModules = module {
    single<UserRepository> { (context: Context) ->
        UserRepositoryImpl(context)
    }
}