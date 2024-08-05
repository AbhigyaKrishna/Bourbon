package me.abhigya.bourbon.core.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.abhigya.bourbon.core.ui.components.TileCard
import me.abhigya.bourbon.core.ui.components.TileLabel
import me.abhigya.bourbon.core.ui.components.TileOption
import me.abhigya.bourbon.core.ui.components.TileSeparator
import me.abhigya.bourbon.core.ui.components.TiledRow
import me.abhigya.bourbon.domain.entities.DietPreference

object DietStepScreen : StepScreen {

    @Composable
    override fun invoke(viewModel: OnBoardingViewModel, uiState: State<OnBoardingContract.State>) {
        TileCard {
            DietCard(
                currentDietGuide = uiState.value.dietGuide,
                onDietGuideChanged = { viewModel.trySend(OnBoardingContract.Inputs.DietGuideChanged(it)) }
            )

            TileSeparator()

            DietPref(
                currentDietPref = uiState.value.dietPreference,
                onDietPreferenceChange = { viewModel.trySend(OnBoardingContract.Inputs.DietPreferenceChanged(it)) }
            )
        }
    }

    @Composable
    private fun DietCard(
        currentDietGuide: OnBoardingContract.DietGuide,
        onDietGuideChanged: (OnBoardingContract.DietGuide) -> Unit,
    ) {
        TileLabel(text = "Diet")
        TiledRow(itemsPerRow = 2, elements = OnBoardingContract.DietGuide.entries.map {
            {
                val selected = currentDietGuide == it
                TileOption(
                    modifier = Modifier
                        .matchParentSize(),
                    isSelected = selected,
                    onClick = {
                        if (selected) return@TileOption
                        onDietGuideChanged(it)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .matchParentSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = it.name,
                            tint = if (selected) MaterialTheme.colorScheme.background else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        VerticalDivider(thickness = 4.dp)
                        Text(text = it.display, color = if (selected) MaterialTheme.colorScheme.background else Color.White)
                    }
                }
            }
        })
    }

    @Composable
    private fun DietPref(
        currentDietPref: DietPreference,
        onDietPreferenceChange: (DietPreference) -> Unit
    ) {
        TileLabel(text = "Dietary Preference")
        TiledRow(itemsPerRow = 2, elements = DietPreference.entries.map {
            {
                val selected = currentDietPref == it
                TileOption(
                    modifier = Modifier
                        .matchParentSize(),
                    isSelected = selected,
                    onClick = {
                        if (selected) return@TileOption
                        onDietPreferenceChange(it)
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .matchParentSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = it.name,
                            tint = if (selected) MaterialTheme.colorScheme.background else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        VerticalDivider(thickness = 4.dp)
                        Text(text = it.display, color = if (selected) MaterialTheme.colorScheme.background else Color.White)
                    }
                }
            }
        })
    }

}