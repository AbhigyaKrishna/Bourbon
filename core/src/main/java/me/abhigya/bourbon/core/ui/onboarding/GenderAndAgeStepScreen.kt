package me.abhigya.bourbon.core.ui.onboarding

import android.media.AudioManager
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import me.abhigya.bourbon.core.R
import me.abhigya.bourbon.core.ui.components.Scale
import me.abhigya.bourbon.core.ui.components.TileCard
import me.abhigya.bourbon.core.ui.components.TileLabel
import me.abhigya.bourbon.core.ui.components.TileOption
import me.abhigya.bourbon.core.ui.components.TileSeparator
import me.abhigya.bourbon.core.ui.components.TiledRow
import me.abhigya.bourbon.domain.entities.Gender

object GenderAndAgeStepScreen : StepScreen {

    @Composable
    override fun invoke(viewModel: OnBoardingViewModel, uiState: State<OnBoardingContract.State>) {
        val haptic = LocalHapticFeedback.current
        val context = LocalContext.current

        val audioManager = remember {
            ContextCompat.getSystemService(context, AudioManager::class.java)
        }

        LaunchedEffect(uiState.value.age) {
            audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, 1f)
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }

        TileCard {
            GenderCard(uiState.value.gender) {
                viewModel.trySend(OnBoardingContract.Inputs.GenderChanged(it))
            }

            TileSeparator()

            AgeCard(uiState.value.age) {
                viewModel.trySend(OnBoardingContract.Inputs.AgeChanged(it))
            }
        }

    }

    @Composable
    internal fun GenderCard(
        currentGender: Gender,
        onGenderChanged: (Gender) -> Unit = {},
    ) {
        TileLabel(text = stringResource(R.string.gender))
        TiledRow(elements = Gender.entries.map {
            {
                val genderSelected = currentGender == it
                TileOption(
                    modifier = Modifier
                        .matchParentSize(),
                    isSelected = genderSelected,
                    onClick = {
                        if (genderSelected) return@TileOption
                        onGenderChanged(it)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .matchParentSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = it.icon(),
                            contentDescription = it.name,
                            tint = if (genderSelected) MaterialTheme.colorScheme.background else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        VerticalDivider(thickness = 4.dp)
                        Text(text = it.name, color = if (genderSelected) MaterialTheme.colorScheme.background else Color.White)
                    }
                }
            }
        })
    }

    @Composable
    internal fun AgeCard(
        currentAge: Int,
        onAgeChanged: (Int) -> Unit = {}
    ) {
        TileLabel(text = stringResource(R.string.age))
        TiledRow(height = 350.dp, elements = listOf(
            {
                Scale(
                    modifier = Modifier.fillMaxSize(),
                    minValue = 5,
                    maxValue = 100,
                    orientation = Orientation.Vertical,
                    currentValue = currentAge,
                    onValueChanged = onAgeChanged,
                    horizontalAlignment = Alignment.CenterHorizontally,
                )
            },
            {
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 4.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                    ) {
                        Text(
                            modifier = Modifier.alignByBaseline(),
                            text = currentAge.toString(),
                            style = MaterialTheme.typography.displayMedium
                        )
                        Text(
                            modifier = Modifier.alignByBaseline(),
                            color = MaterialTheme.colorScheme.outline,
                            text = stringResource(R.string.years),
                        )
                    }
                }
            }
        ))
    }

    @Composable
    private fun Gender.icon(): Painter {
        return when (this) {
            Gender.Male -> painterResource(id = R.drawable.male)
            Gender.Female -> painterResource(id = R.drawable.female)
        }
    }

}