package me.abhigya.bourbon.core.ui.caloriecalc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copperleaf.ballast.navigation.routing.RouterContract
import dev.jeziellago.compose.markdowntext.MarkdownText
import me.abhigya.bourbon.core.ui.AppScreen
import me.abhigya.bourbon.core.ui.components.AnimatedLoadingGradient
import me.abhigya.bourbon.core.ui.components.AppBar
import me.abhigya.bourbon.core.ui.components.BackButton
import me.abhigya.bourbon.core.ui.components.StatCard
import me.abhigya.bourbon.core.ui.components.TileCard
import me.abhigya.bourbon.core.ui.components.TileDropDown
import me.abhigya.bourbon.core.ui.components.TileSeparator
import me.abhigya.bourbon.core.ui.components.TileTextBox
import me.abhigya.bourbon.core.ui.components.TiledRow
import me.abhigya.bourbon.core.ui.components.TypewriterTextEffect
import me.abhigya.bourbon.core.ui.router.LocalRouter
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

object CalorieViewerScreen : AppScreen {

    @Composable
    override operator fun invoke() {
        val router = LocalRouter.current
        val coroutine = rememberCoroutineScope()
        val viewModel: CalorieViewerViewModel = remember(coroutine) { get { parametersOf(coroutine) } }
        val uiState by viewModel.observeStates().collectAsState()

        Scaffold(
            topBar = {
                AppBar {
                    BackButton {
                        router.trySend(RouterContract.Inputs.GoBack())
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TileCard(
                    width = 360.dp
                ) {
                    var item by remember { mutableStateOf("") }
                    TileTextBox(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .height(60.dp),
                        value = item,
                        onValueChange = {
                            item = it
                            viewModel.trySend(CalorieViewerContract.Inputs.ItemChanged(it))
                        },
                        label = {
                            Text(text = "Item")
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    TiledRow(elements = listOf(
                        {
                            var amount by remember { mutableStateOf("") }
                            TileTextBox(
                                value = amount,
                                onValueChange = {
                                    runCatching {
                                        if (it.isEmpty()) return@runCatching 0f
                                        it.toFloat()
                                    }.onSuccess { value ->
                                        amount = it
                                        viewModel.trySend(CalorieViewerContract.Inputs.AmountChanged(value))
                                    }
                                },
                                label = {
                                    Text(text = "Amount")
                                }
                            )
                        },
                        {
                            TileDropDown(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary),
                                selected = uiState.unit.ordinal,
                                entries = CalorieViewerContract.QuantityUnit.entries.map { it.toString() },
                                fontWeight = FontWeight.Bold
                            ) {
                                viewModel.trySend(CalorieViewerContract.Inputs.UnitChanged(CalorieViewerContract.QuantityUnit.entries[it]))
                            }
                        }
                    ))

                    HorizontalDivider(thickness = 8.dp)

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        onClick = {
                            viewModel.trySend(CalorieViewerContract.Inputs.Fetch)
                        },
                        colors = ButtonDefaults.buttonColors()
                            .copy(
                                containerColor = MaterialTheme.colorScheme.primary,
                            ),
                        shape = RoundedCornerShape(8.dp),
                    ) {
                        Text(
                            text = "Check",
                            color = MaterialTheme.colorScheme.background,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    

                    AnimatedVisibility(visible = uiState.outputState != CalorieViewerContract.OutputState.None) {
                        Column(
                            modifier = Modifier
                                .padding(top = 8.dp)
                        ) {
                            TileSeparator()

                            when (val state = uiState.outputState) {
                                CalorieViewerContract.OutputState.Loading -> RenderLoading()
                                is CalorieViewerContract.OutputState.Data -> RenderOutput(text = state.text)
                                is CalorieViewerContract.OutputState.Error -> RenderOutput(text = "An error occurred: ${state.error.localizedMessage}")
                                else -> Unit
                            }
                        }
                    }
                }

                HorizontalDivider(thickness = 12.dp)

                StatCard(
                    modifier = Modifier
                        .width(360.dp)
                        .height(168.dp),
                    calorieEaten = 1291,
                    calorieRemaining = 826,
                    calorieBurned = 244,
                    totalCalorie = 1291 + 826
                )
            }
        }
    }

    @Composable
    fun RenderLoading() {
        Box(
            modifier = Modifier
                .height(200.dp)
                .padding(top = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedLoadingGradient(rows = 4)
        }
    }

    @Composable
    fun RenderOutput(text: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .height(64.dp)
                    .width(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = "AI Text"
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp, end = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                TypewriterTextEffect(text = text) {
                    MarkdownText(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .clickable(
                                enabled = false,
                                onClick = { }
                            ),
                        markdown = it,
                        style = TextStyle(
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }

}