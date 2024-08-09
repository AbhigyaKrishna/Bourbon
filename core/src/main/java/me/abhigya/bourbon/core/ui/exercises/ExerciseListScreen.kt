package me.abhigya.bourbon.core.ui.exercises

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import me.abhigya.bourbon.core.R
import me.abhigya.bourbon.core.ui.AppScreen
import me.abhigya.bourbon.core.ui.components.DraggableCard
import me.abhigya.bourbon.core.utils.verticalGradientBackground
import me.abhigya.bourbon.domain.entities.Exercise
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class ExerciseListScreen(
    private val state: ExerciseListContract.State
) : AppScreen {

    @Composable
    override operator fun invoke() {
        val coroutineScope = rememberCoroutineScope()
        val exerciseListVM: ExerciseListViewModel  = remember(coroutineScope) { get { parametersOf(coroutineScope, state) } }
        val exercises by exerciseListVM.observeStates().collectAsState()
        Content(
            exercises.shownIndex,
            exercises.exercises,
        {
            exerciseListVM.trySend(ExerciseListContract.Inputs.Next)
        }) {
            exerciseListVM.trySend(ExerciseListContract.Inputs.Previous)
        }
    }

    @Composable
    internal fun Content(
        index: Int,
        exercises: List<Exercise>,
        onNext: () -> Unit,
        onBack: () -> Unit
    ) {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val cardHeight = screenHeight - 200.dp

        Surface(modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)) {
            val boxModifier = Modifier

            Box(
                modifier = boxModifier.verticalGradientBackground(
                    listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.background
                    )
                )
            ) {
                Loader(modifier = boxModifier)
                val showing = exercises.dropLast(index)
                showing.forEachIndexed { idx, album ->
                    DraggableCard(
                        item = album,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(cardHeight)
                            .padding(
                                top = 16.dp + (idx + 2).dp,
                                bottom = 16.dp,
                                start = 16.dp,
                                end = 16.dp
                            ),
                        onSwiped = {
                            if (index != exercises.lastIndex) {
                                onNext()
                            }
                        }
                    ) {
                        CardContent(album)
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = cardHeight)
                        .alpha(
                            animateFloatAsState(
                                if (showing.isEmpty()) 0f else 1f,
                                label = "Desc"
                            ).value
                        )
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            tint = Color.Gray,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            // TODO
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun CardContent(exercise: Exercise) {
        Column {
            GlideImage(
                model = exercise.imageUri,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f),
                    textAlign = TextAlign.Start
                )
                Icon(
                    imageVector = Icons.Outlined.Place,
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
                Text(
                    text = exercise.duration.inWholeSeconds.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = exercise.description,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(bottom = 4.dp, start = 16.dp, end = 16.dp)
            )
            Text(
                text = exercise.quantity?.let { "${it.amount} ${it.unit}" } ?: "",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            )
        }
    }

    @Composable
    fun Loader(modifier: Modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .clip(CircleShape)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null,
                modifier = modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }

}