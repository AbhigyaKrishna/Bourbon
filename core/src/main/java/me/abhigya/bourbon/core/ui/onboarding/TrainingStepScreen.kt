package me.abhigya.bourbon.core.ui.onboarding

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.abhigya.bourbon.core.ui.AddRemove
import me.abhigya.bourbon.core.ui.components.TileCard
import me.abhigya.bourbon.core.ui.components.TileLabel
import me.abhigya.bourbon.core.ui.components.TileOption
import me.abhigya.bourbon.core.ui.components.TileSeparator
import me.abhigya.bourbon.core.ui.components.TiledRow
import me.abhigya.bourbon.domain.entities.DefaultTraining
import me.abhigya.bourbon.domain.entities.abbreviation
import java.time.DayOfWeek

object TrainingStepScreen : StepScreen {

    @Composable
    override fun invoke(viewModel: OnBoardingViewModel, uiState: State<OnBoardingContract.State>) {
        TileCard {
            TileLabel(text = "Train")
            TiledRow(elements = DefaultTraining.entries.map {
                {
                    val selected = uiState.value.training.contains(it)
                    TileOption(
                        modifier = Modifier
                            .fillMaxSize(),
                        isSelected = selected,
                        onClick = {
                            if (selected) {
                                viewModel.trySend(OnBoardingContract.Inputs.TrainingChanged(AddRemove.Remove(it)))
                            } else {
                                viewModel.trySend(OnBoardingContract.Inputs.TrainingChanged(AddRemove.Add(it)))
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
            
            TileSeparator()
            
            TileLabel(text = "Workout Days")
            TiledRow(
                itemsPerRow = 7,
                height = 40.dp,
                elements = DayOfWeek.entries.map {
                    {
                        val selected = uiState.value.workoutDays.contains(it)
                        TileOption(
                            modifier = Modifier
                                .fillMaxSize(),
                            isSelected = selected,
                            onClick = {
                                if (selected) {
                                    viewModel.trySend(OnBoardingContract.Inputs.WorkoutDaysChanged(AddRemove.Remove(it)))
                                } else {
                                    viewModel.trySend(OnBoardingContract.Inputs.WorkoutDaysChanged(AddRemove.Add(it)))
                                }
                            },
                            outlined = true
                        ) {
                            Text(
                                text = it.abbreviation,
                                color = if (selected) MaterialTheme.colorScheme.background else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            )
        }
    }

}