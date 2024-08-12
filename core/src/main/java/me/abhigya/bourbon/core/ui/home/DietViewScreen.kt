package me.abhigya.bourbon.core.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.abhigya.bourbon.domain.entities.Diet
import me.abhigya.bourbon.domain.entities.Food
import me.abhigya.bourbon.domain.entities.User

object DietViewScreen : SubScreen {

    @Composable
    override fun invoke(uiState: HomeContract.State, user: User) {
        val diet = user.diet[uiState.selectedDate.dayOfWeek] ?: Diet(0, listOf())
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 4.dp, top = 12.dp, bottom = 12.dp),
                text = "Day Calorie: ${diet.calorieIntake}",
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.primary,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                for (food in diet.food) {
                    Element(
                        food = food,
                        onInfoClick = {},
                        onCheckClick = {},
                        onCrossClick = {}
                    )
                }
            }
        }
    }

    @Composable
    internal fun Element(
        food: Food,
        onInfoClick: () -> Unit,
        onCheckClick: () -> Unit,
        onCrossClick: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(2f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = food.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${food.calories} calories",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            modifier = Modifier
                                .clip(CircleShape)
                                .padding(4.dp)
                                .weight(1f),
                            onClick = onInfoClick
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                modifier = Modifier
                                    .fillMaxSize(0.8f),
                                tint = Color.White,
                                contentDescription = null
                            )
                        }

                        IconButton(
                            modifier = Modifier
                                .clip(CircleShape)
                                .padding(4.dp)
                                .weight(1f),
                            onClick = onCheckClick
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                modifier = Modifier
                                    .fillMaxSize(0.8f),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                        }

                        IconButton(
                            modifier = Modifier
                                .clip(CircleShape)
                                .padding(4.dp)
                                .weight(1f),
                            onClick = onCrossClick
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                modifier = Modifier
                                    .fillMaxSize(0.8f),
                                tint = MaterialTheme.colorScheme.error,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    }

}