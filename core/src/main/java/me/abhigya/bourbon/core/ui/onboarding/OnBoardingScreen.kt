package me.abhigya.bourbon.core.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.abhigya.bourbon.core.ui.AppScreen
import me.abhigya.bourbon.core.utils.navigationBarsHeight
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
                            .background(color = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(4.dp)),
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
                HeightAndWeightCard()
                GenderAndDOBCard(uiState.gender, {
                    viewModel.trySend(OnBoardingContract.Inputs.GenderChanged(it))
                }, uiState.ageGroup) {
                    viewModel.trySend(OnBoardingContract.Inputs.AgeGroupChanged(it))
                }
            }
        }
    }

    @Composable
    internal fun HeightAndWeightCard(onWeightChanged: (Float) -> Unit = {}, onHeightChanged: (Int) -> Unit = {}) {
        @Composable
        fun BoxScope.DropButton(text: String) {
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = text, color = MaterialTheme.colorScheme.background)
                VerticalDivider(thickness = 4.dp)
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        TileCard {
            TiledRow(elements = listOf(
                { Text(text = "Weight", color = Color.White) },
                { DropButton(text = "Kg") }
            ))

            TileSeparator()

            TiledRow(elements = listOf(
                { Text(text = "Height", color = Color.White) },
                { DropButton(text = "Cm") }
            ))
        }
    }

    @Composable
    internal fun GenderAndDOBCard(currentGender: Gender, onGenderChanged: (Gender) -> Unit = {}, currentAgeGroup: AgeGroup, onAgeGroupChanged: (AgeGroup) -> Unit = {}) {
        @Composable
        fun BoxScope.GenderButton(gender: Gender) {
            val genderSelected = currentGender == gender
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .run {
                        if (genderSelected) {
                            background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            )
                        } else {
                            this
                        }
                    }
                    .clickable {
                        if (genderSelected) return@clickable
                        onGenderChanged(gender)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = gender.name,
                    tint = if (genderSelected) MaterialTheme.colorScheme.background else Color.White,
                    modifier = Modifier.size(16.dp)
                )
                VerticalDivider(thickness = 4.dp)
                Text(text = gender.name, color = if (genderSelected) MaterialTheme.colorScheme.background else Color.White)
            }
        }

        @Composable
        fun BoxScope.AgeGroup(ageGroup: AgeGroup) {
            val ageSelected = currentAgeGroup == ageGroup
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .run {
                        if (ageSelected) {
                            background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            )
                        } else {
                            this
                        }
                    }
                    .clickable {
                        if (ageSelected) return@clickable
                        onAgeGroupChanged(ageGroup)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = ageGroup.display, color = if (ageSelected) MaterialTheme.colorScheme.background else Color.White)
            }
        }

        TileCard {
            Label(text = "Gender")
            TiledRow(elements = listOf(
                { GenderButton(gender = Gender.Male) },
                { GenderButton(gender = Gender.Female) }
            ))

            TileSeparator()

            Label(text = "Age Group")
            TiledRow(itemsPerRow = 4, elements = listOf(
                { AgeGroup(ageGroup = AgeGroup._18_29) },
                { AgeGroup(ageGroup = AgeGroup._30_39) },
                { AgeGroup(ageGroup = AgeGroup._40_49) },
                { AgeGroup(ageGroup = AgeGroup._50) }
            ))
        }
    }

}