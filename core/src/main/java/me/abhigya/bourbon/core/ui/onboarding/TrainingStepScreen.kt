package me.abhigya.bourbon.core.ui.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import me.abhigya.bourbon.core.ui.components.TileCard
import me.abhigya.bourbon.core.ui.components.TileLabel
import me.abhigya.bourbon.core.ui.components.TileOption
import me.abhigya.bourbon.core.ui.components.TiledRow

object TrainingStepScreen : StepScreen {

    @Composable
    override fun invoke(viewModel: OnBoardingViewModel, uiState: State<OnBoardingContract.State>) {
        TileCard {
            TileLabel(text = "Train")
            TiledRow(elements = OnBoardingContract.DefaultTraining.entries.map {
                {
                    val selected = uiState.value.training.contains(it)
                    TileOption(
                        modifier = Modifier
                            .fillMaxSize(),
                        isSelected = selected,
                        onClick = {
                            if (selected) {
                                viewModel.trySend(OnBoardingContract.Inputs.TrainingChanged(OnBoardingContract.Inputs.TrainingChanged.Remove(it)))
                            } else {
                                viewModel.trySend(OnBoardingContract.Inputs.TrainingChanged(OnBoardingContract.Inputs.TrainingChanged.Add(it)))
                            }
                        },
                        outlined = true
                    ) {
                        Text(
                            text = it.name,
                            color = if (selected) MaterialTheme.colorScheme.background else Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            })
        }
    }

}