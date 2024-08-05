package me.abhigya.bourbon.core.ui

import androidx.compose.runtime.Composable
import org.koin.core.component.KoinComponent

interface AppScreen : KoinComponent {

    @Composable
    operator fun invoke()

}

sealed interface AddRemove<T> {
    data class Add<T>(val item: T) : AddRemove<T>
    data class Remove<T>(val item: T) : AddRemove<T>
}