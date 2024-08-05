package me.abhigya.bourbon.core.ui.onboarding

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.abhigya.bourbon.core.ui.components.CircularScale
import me.abhigya.bourbon.core.utils.systemBarsPadding
import me.abhigya.bourbon.domain.entities.Kilograms
import kotlin.math.roundToInt

object WeightStepScreen : StepScreen {

    @Composable
    override fun invoke(viewModel: OnBoardingViewModel, uiState: State<OnBoardingContract.State>) {
        WeightScreenContent(
            currentWeight = uiState.value.weight.value,
            onWeightChanged = {
                viewModel.trySend(OnBoardingContract.Inputs.WeightChanged(Kilograms(it)))
            }
        )
    }

    @Composable
    internal fun WeightScreenContent(
        currentWeight: Int,
        onWeightChanged: (Int) -> Unit,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(bottom = 36.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(36.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "How much do you weight?",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            space = 8.dp,
                            alignment = Alignment.CenterVertically
                        ),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 4.dp,
                                alignment = Alignment.CenterHorizontally
                            ),
                        ) {
                            Text(
                                modifier = Modifier.alignByBaseline(),
                                text = currentWeight.toString(),
                                style = MaterialTheme.typography.displayMedium
                            )
                            Text(
                                modifier = Modifier.alignByBaseline(),
                                color = MaterialTheme.colorScheme.outline,
                                text = "kg",
                            )
                        }
                        CompositionLocalProvider(
                            LocalContentColor provides MaterialTheme.colorScheme.outline
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = 4.dp,
                                    alignment = Alignment.CenterHorizontally
                                ),
                            ) {
                                val weightInPounds = currentWeight * 2.2
                                Text(
                                    modifier = Modifier.alignByBaseline(),
                                    text = weightInPounds.roundToInt().toString()
                                )
                                Text(
                                    modifier = Modifier.alignByBaseline(),
                                    text = "lbs"
                                )
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                CircularScale(
                    currentValue = currentWeight,
                    onValueChange = onWeightChanged,
                    orientation = Orientation.Horizontal
                )
            }
        }
    }

}