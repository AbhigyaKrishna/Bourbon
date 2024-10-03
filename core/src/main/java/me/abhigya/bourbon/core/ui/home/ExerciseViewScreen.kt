package me.abhigya.bourbon.core.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.abhigya.bourbon.core.R
import me.abhigya.bourbon.core.ui.components.UiButton
import me.abhigya.bourbon.domain.entities.Exercise
import me.abhigya.bourbon.domain.entities.Rest
import me.abhigya.bourbon.domain.entities.User

object ExerciseViewScreen : SubScreen {

    @Composable
    override fun invoke(uiState: HomeContract.State, user: User) {
        val exercises = user.exercises[uiState.selectedDate.dayOfWeek] ?: listOf(Rest)
        Scaffold(
            bottomBar = {
                UiButton(
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    text = stringResource(R.string.exercise_view_start)
                ) {

                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                for (exercise in exercises) {
                    Element(exercise)
                }
            }
        }
    }

    @Composable
    internal fun Element(exercise: Exercise) {
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
                        .padding(start = 16.dp, end = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = exercise.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        val quantity = exercise.quantity
                        if (quantity != null) {
                            Text(
                                text = "${quantity.amount} ${quantity.unit}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.stopwatch),
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .size(24.dp),
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null
                        )
                        Text(
                            text = "${exercise.duration ?: 60}s",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

}