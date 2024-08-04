package me.abhigya.bourbon.core.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.abhigya.bourbon.core.ui.AppScreen
import me.abhigya.bourbon.core.utils.navigationBarsPadding
import me.abhigya.bourbon.domain.entities.AgeGroup
import me.abhigya.bourbon.domain.entities.Gender
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

object OnBoardingScreen : AppScreen {

    @Composable
    override operator fun invoke() {
        Scaffold(
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(336.dp)
                            .height(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Next",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.background
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val coroutineScope = rememberCoroutineScope()
                val viewModel: OnBoardingViewModel = remember(coroutineScope) { get { parametersOf(coroutineScope) } }
                val uiState by viewModel.observeStates().collectAsState()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Setup",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                var weight by remember { mutableIntStateOf(0) }
                var height by remember { mutableIntStateOf(0) }
                HeightAndWeightCard(weight, {
                    weight = it
                    viewModel.trySend(OnBoardingContract.Inputs.WeightChanged(uiState.weight.copy(value = it)))
                }, uiState.weight.unit, {
                    viewModel.trySend(OnBoardingContract.Inputs.WeightChanged(uiState.weight.copy(unit = it)))
                }, height, {
                    height = it
                    viewModel.trySend(OnBoardingContract.Inputs.HeightChanged(uiState.height.copy(value = it)))
                }, uiState.height.unit, {
                    viewModel.trySend(OnBoardingContract.Inputs.HeightChanged(uiState.height.copy(unit = it)))
                })

                GenderAndDOBCard(uiState.gender, {
                    viewModel.trySend(OnBoardingContract.Inputs.GenderChanged(it))
                }, uiState.ageGroup) {
                    viewModel.trySend(OnBoardingContract.Inputs.AgeGroupChanged(it))
                }
            }
        }
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

    @Composable
    internal fun GenderAndDOBCard(
        currentGender: Gender,
        onGenderChanged: (Gender) -> Unit = {},
        currentAgeGroup: AgeGroup,
        onAgeGroupChanged: (AgeGroup) -> Unit = {}
    ) {

        TileCard {
            Label(text = "Gender")
            TiledRow(elements = Gender.entries.map {
                {
                    val genderSelected = currentGender == it
                    TileOption(
                        modifier = Modifier
                            .matchParentSize(),
                        isSelected = genderSelected,
                        onSelect = { onGenderChanged(it) }
                    ) {
                        Row(
                            modifier = Modifier
                                .matchParentSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Check,
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

            TileSeparator()

            Label(text = "Age Group")
            TiledRow(itemsPerRow = 4, elements = AgeGroup.entries.map {
                {
                    val ageSelected = currentAgeGroup == it
                    TileOption(
                        modifier = Modifier
                            .matchParentSize(),
                        isSelected = ageSelected,
                        onSelect = { onAgeGroupChanged(it) }
                    ) {
                        Text(text = it.display, color = if (ageSelected) MaterialTheme.colorScheme.background else Color.White)
                    }
                }
            })
        }
    }

}