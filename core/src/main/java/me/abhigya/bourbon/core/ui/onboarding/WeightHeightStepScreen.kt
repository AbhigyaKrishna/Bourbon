package me.abhigya.bourbon.core.ui.onboarding

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

@Deprecated("Use WeighStepScreen and HeightStepScreen instead")
object WeightHeightStepScreen : StepScreen {

    @Composable
    override fun invoke(viewModel: OnBoardingViewModel, uiState: State<OnBoardingContract.State>) {
        var weight by remember { mutableIntStateOf(0) }
        var height by remember { mutableIntStateOf(0) }
        HeightAndWeightCard(weight, {
            weight = it
            viewModel.trySend(OnBoardingContract.Inputs.WeightChanged(uiState.value.weight.copy(value = it)))
        }, uiState.value.weight.unit, {
            viewModel.trySend(OnBoardingContract.Inputs.WeightChanged(uiState.value.weight.copy(unit = it)))
        }, height, {
            height = it
            viewModel.trySend(OnBoardingContract.Inputs.HeightChanged(uiState.value.height.copy(value = it)))
        }, uiState.value.height.unit, {
            viewModel.trySend(OnBoardingContract.Inputs.HeightChanged(uiState.value.height.copy(unit = it)))
        })
    }

    @Composable
    internal fun HeightAndWeightCard(
        weight: Int,
        onWeightChanged: (Int) -> Unit = {},
        weightUnit: OnBoardingContract.WeightUnit,
        onWeightUnitChanged: (OnBoardingContract.WeightUnit) -> Unit = {},
        height: Int,
        onHeightChanged: (Int) -> Unit = {},
        heightUnit: OnBoardingContract.HeightUnit,
        onHeightUnitChanged: (OnBoardingContract.HeightUnit) -> Unit = {}
    ) {
        TileCard {
            TiledRow(elements = listOf(
                {
                    TileTextBox(
                        value = if (weight <= 0f) "" else weight.toString(),
                        onValueChange = {
                            runCatching {
                                if (it.isEmpty()) return@runCatching 0

                                it.toInt()
                            }.onSuccess {
                                onWeightChanged(it)
                            }
                        },
                        label = {
                            Text(text = "Weight", textAlign = TextAlign.Center)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                },
                {
                    TileDropDown(
                        modifier = Modifier
                            .matchParentSize(),
                        selected = weightUnit.ordinal,
                        entries = OnBoardingContract.WeightUnit.entries.map { it.toString() },
                        onEntryClick = {
                            onWeightUnitChanged(OnBoardingContract.WeightUnit.entries[it])
                        }
                    )
                }
            ))

            TileSeparator()

            TiledRow(elements = listOf(
                {
                    TileTextBox(
                        value = if (height <= 0) "" else height.toString(),
                        onValueChange = {
                            runCatching {
                                if (it.isEmpty()) return@runCatching 0

                                it.toInt()
                            }.onSuccess {
                                onHeightChanged(it)
                            }
                        },
                        label = {
                            Text(text = "Height", textAlign = TextAlign.Center)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                },
                {
                    TileDropDown(
                        modifier = Modifier
                            .matchParentSize(),
                        selected = heightUnit.ordinal,
                        entries = OnBoardingContract.HeightUnit.entries.map { it.toString() },
                        onEntryClick = {
                            onHeightUnitChanged(OnBoardingContract.HeightUnit.entries[it])
                        }
                    )
                }
            ))
        }
    }

}