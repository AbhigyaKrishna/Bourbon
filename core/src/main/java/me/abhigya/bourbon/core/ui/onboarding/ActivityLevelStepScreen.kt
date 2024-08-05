package me.abhigya.bourbon.core.ui.onboarding

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.abhigya.bourbon.core.ui.components.TileCard
import me.abhigya.bourbon.core.ui.components.TileLabel
import me.abhigya.bourbon.core.ui.components.TileOption
import me.abhigya.bourbon.core.ui.components.TiledRow
import me.abhigya.bourbon.domain.entities.ActivityLevel

object ActivityLevelStepScreen : StepScreen {

    @Composable
    override fun invoke(viewModel: OnBoardingViewModel, uiState: State<OnBoardingContract.State>) {
        TileCard {
            TileLabel(text = "Activity Level")
            TiledRow(itemsPerRow = 3, elements = ActivityLevel.entries.map {
                {
                    val selected = uiState.value.activityLevel == it
                    TileOption(
                        modifier = Modifier
                            .matchParentSize(),
                        isSelected = selected,
                        onClick = {
                            if (selected) return@TileOption
                            viewModel.trySend(OnBoardingContract.Inputs.ActivityLevelChanged(it))
                        }
                    ) {
                        Text(text = it.name, color = if (selected) MaterialTheme.colorScheme.background else Color.White)
                    }
                }
            })
        }
    }

}