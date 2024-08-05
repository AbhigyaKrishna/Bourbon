package me.abhigya.bourbon.core.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import org.koin.core.component.KoinComponent

interface StepScreen : KoinComponent {

    @Composable
    operator fun invoke(viewModel: OnBoardingViewModel, uiState: State<OnBoardingContract.State>)

}