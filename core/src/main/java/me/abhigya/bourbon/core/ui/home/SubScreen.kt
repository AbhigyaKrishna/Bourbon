package me.abhigya.bourbon.core.ui.home

import androidx.compose.runtime.Composable
import me.abhigya.bourbon.domain.entities.User
import org.koin.core.component.KoinComponent

interface SubScreen : KoinComponent {

    @Composable
    operator fun invoke(uiState: HomeContract.State, user: User)

}