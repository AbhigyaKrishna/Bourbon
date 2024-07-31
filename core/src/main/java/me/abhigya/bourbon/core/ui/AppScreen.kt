package me.abhigya.bourbon.core.ui

import androidx.compose.runtime.Composable
import org.koin.core.component.KoinComponent

interface AppScreen : KoinComponent {

    @Composable
    operator fun invoke()

}