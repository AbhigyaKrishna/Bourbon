package me.abhigya.bourbon.core.ui.onboarding

import android.media.AudioManager
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import me.abhigya.bourbon.core.R
import me.abhigya.bourbon.core.ui.components.Scale
import me.abhigya.bourbon.core.utils.systemBarsPadding
import me.abhigya.bourbon.domain.entities.Centimeters
import kotlin.math.roundToInt

object HeightStepScreen : StepScreen {
    @Composable
    override fun invoke(viewModel: OnBoardingViewModel, uiState: State<OnBoardingContract.State>) {
        val haptic = LocalHapticFeedback.current
        val context = LocalContext.current

        val audioManager = remember {
            ContextCompat.getSystemService(context, AudioManager::class.java)
        }

        LaunchedEffect(uiState.value.height.value) {
            audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1f)
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }

        HeightScreenContent(
            currentHeight = uiState.value.height.value,
            onHeightChange = {
                viewModel.trySend(OnBoardingContract.Inputs.HeightChanged(Centimeters(it)))
            }
        )
    }

    @Composable
    fun HeightScreenContent(
        currentHeight: Int,
        onHeightChange: (Int) -> Unit,
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
                        text = stringResource(R.string.how_tall_are_you),
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
                                text = currentHeight.toString(),
                                style = MaterialTheme.typography.displayMedium
                            )
                            Text(
                                modifier = Modifier.alignByBaseline(),
                                color = MaterialTheme.colorScheme.outline,
                                text = "cm",
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
                                val feet = (currentHeight * 0.0328).toInt()
                                val inches = (currentHeight * 0.3937).roundToInt() % 12
                                Text(
                                    modifier = Modifier.alignByBaseline(),
                                    text = "$feet' $inches\"",
                                )
                                Text(
                                    modifier = Modifier.alignByBaseline(),
                                    text = stringResource(R.string.ft_unit)
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
                Scale(
                    modifier = Modifier.fillMaxSize(),
                    minValue = 40,
                    maxValue = 220,
                    orientation = Orientation.Horizontal,
                    currentValue = currentHeight,
                    onValueChanged = onHeightChange,
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
            }
        }
    }

}