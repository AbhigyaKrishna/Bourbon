package me.abhigya.bourbon.core.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.abhigya.bourbon.core.ui.components.BmiScale
import me.abhigya.bourbon.core.ui.components.TileCard
import me.abhigya.bourbon.core.ui.components.TileDropDown
import me.abhigya.bourbon.core.ui.components.TileLabel
import me.abhigya.bourbon.core.ui.components.TileTextBox
import me.abhigya.bourbon.core.ui.components.TiledRow
import me.abhigya.bourbon.domain.entities.Kilograms
import me.abhigya.bourbon.domain.entities.calculateBmi

object GoalAndAimStepScreen : StepScreen {

    @Composable
    override fun invoke(viewModel: OnBoardingViewModel, uiState: State<OnBoardingContract.State>) {
        Column {
            BmiScaleCard(uiState = uiState)

            var weight by remember { mutableIntStateOf(40) }
            GoalCard(
                goal = uiState.value.goal,
                onGoalChanged = {
                    viewModel.trySend(OnBoardingContract.Inputs.GoalChanged(it))
                },
                aimWeight = weight,
                onAimWeightChanged = {
                    weight = it
                    viewModel.trySend(OnBoardingContract.Inputs.AimWeightChanged(Kilograms(it)))
                }
            )
        }
    }

    @Composable
    internal fun BmiScaleCard(uiState: State<OnBoardingContract.State>) {
        TileCard {
            TileLabel(text = "BMI Scale")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val bmi = remember {
                    calculateBmi(
                        weight = uiState.value.weight,
                        height = uiState.value.height
                    )
                }

                Text(
                    text = "%.1f".format(bmi),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
                BmiScale(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = bmi,
                    scaleBorder = BorderStroke(0.dp, Color.Black),
                    indicatorColor = Color.Black
                )
            }
        }
    }

    @Composable
    internal fun GoalCard(
        goal: OnBoardingContract.Goals,
        onGoalChanged: (OnBoardingContract.Goals) -> Unit,
        aimWeight: Int,
        onAimWeightChanged: (Int) -> Unit,
    ) {
        TileCard {
            TileLabel(text = "Goal")
            TiledRow(elements = listOf(
                {
                    TileDropDown(
                        selected = goal.ordinal,
                        entries = OnBoardingContract.Goals.entries.map { it.toString() },
                        onEntryClick = { onGoalChanged(OnBoardingContract.Goals.entries[it]) },
                        arrowTint = MaterialTheme.colorScheme.primary,
                        textColor = Color.White,
                    )
                },
                {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TileTextBox(
                            modifier = Modifier
                                .weight(2f),
                            value = if (aimWeight == 0) "" else aimWeight.toString(),
                            onValueChange = {
                                runCatching {
                                    if (it.isEmpty()) return@runCatching 0
                                    it.toInt()
                                }.onSuccess {
                                    onAimWeightChanged(it)
                                }
                            },
                            label = {
                                Text(text = "Aim")
                            }
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Kg", color = MaterialTheme.colorScheme.background)
                        }
                    }
                }
            ))
        }
    }

}