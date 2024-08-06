package me.abhigya.bourbon.core.ui.onboarding

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.abhigya.bourbon.core.ui.components.TileCard
import me.abhigya.bourbon.core.ui.components.TileLabel
import kotlin.math.roundToInt

object MealFrequencyStepScreen : StepScreen {

    @Composable
    override fun invoke(viewModel: OnBoardingViewModel, uiState: State<OnBoardingContract.State>) {
        TileCard {
            TileLabel(text = "Meal Frequency")

            Text(
                text = uiState.value.mealFrequency.toString(),
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
                    .padding(bottom = 8.dp),
                text = "meal${if (uiState.value.mealFrequency > 1) "s" else ""} per day",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                val interactionSource = remember { MutableInteractionSource() }
                val colors = SliderDefaults.colors()
                    .copy(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.background
                    )
                Slider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = uiState.value.mealFrequency.toFloat(),
                    onValueChange = {
                        viewModel.trySend(OnBoardingContract.Inputs.MealFrequencyChanged(it.roundToInt()))
                    },
                    interactionSource = interactionSource,
                    steps = 6,
                    valueRange = 1f..8f,
                    colors = colors
                )
            }
        }
    }

//    @OptIn(ExperimentalMaterial3Api::class)
//    @Composable
//    private fun Thumb(
//        modifier: Modifier = Modifier,
//        interactionSource: MutableInteractionSource,
//        colors: SliderColors,
//        thumbSize: DpSize,
//        state: SliderState
//    ) {
//        val interactions = remember { mutableStateListOf<Interaction>() }
//        LaunchedEffect(interactionSource) {
//            interactionSource.interactions.collect { interaction ->
//                when (interaction) {
//                    is PressInteraction.Press -> interactions.add(interaction)
//                    is PressInteraction.Release -> interactions.remove(interaction.press)
//                    is PressInteraction.Cancel -> interactions.remove(interaction.press)
//                    is DragInteraction.Start -> interactions.add(interaction)
//                    is DragInteraction.Stop -> interactions.remove(interaction.start)
//                    is DragInteraction.Cancel -> interactions.remove(interaction.start)
//                }
//            }
//        }
//
//        val elevation = if (interactions.isNotEmpty()) {
//            6.dp
//        } else {
//            1.dp
//        }
//        val shape = RoundedCornerShape(1.dp)
//
//        Box {
//            Box(
//                modifier
//                    .size(thumbSize)
//                    .offset(y = -thumbSize.height)
//                    .shadow(elevation, shape, clip = false)
//                    .background(MaterialTheme.colorScheme.background, shape)
//            ) {
//                Text(
//                    text = state.value.toString(),
//                    color = colors.thumbColor
//                )
//            }
//            Spacer(
//                modifier
//                    .size(thumbSize)
//                    .indication(
//                        interactionSource = interactionSource,
//                        indication = androidx.compose.material.ripple.rememberRipple(
//                            bounded = false,
//                            radius = 20.dp
//                        )
//                    )
//                    .hoverable(interactionSource = interactionSource)
//                    .shadow(elevation, shape, clip = false)
//                    .background(colors.thumbColor, shape)
//            )
//        }
//    }

}