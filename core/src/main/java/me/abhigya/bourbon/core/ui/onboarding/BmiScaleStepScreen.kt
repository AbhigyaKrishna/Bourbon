package me.abhigya.bourbon.core.ui.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import me.abhigya.bourbon.core.ui.components.BmiScale
import me.abhigya.bourbon.core.ui.components.TileCard
import me.abhigya.bourbon.domain.entities.BmiCategory
import me.abhigya.bourbon.domain.entities.calculateBmi

object BmiScaleStepScreen : StepScreen {

    @Composable
    override fun invoke(viewModel: OnBoardingViewModel, uiState: State<OnBoardingContract.State>) {
        TileCard {
            BmiScaleContent(bmi = calculateBmi(uiState.value.weight.value, uiState.value.height.value))
        }
    }

    @Composable
    fun BmiScaleContent(
        bmi: Double,
    ) {
        val view = LocalView.current
        val bmiCategory = remember(bmi) { BmiCategory.from(bmi) }
        val bmiValue = remember {
            val differenceFactor = if (view.isInEditMode) 0.0 else 5.0
            val initialValue = ((bmi - differenceFactor).toFloat()).coerceAtLeast(0f)
            Animatable(initialValue)
        }

        LaunchedEffect(Unit) {
            bmiValue.animateTo(bmi.toFloat(), tween(1000, easing = LinearEasing))
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .height(350.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your BMI is ",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "%.1f".format(bmiValue.value),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "kg/m²")
            Spacer(modifier = Modifier.height(24.dp))
            BmiScale(
                value = bmi,
                scaleBorder = BorderStroke(2.dp, Color.Black),
                indicatorColor = Color.Black,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = buildAnnotatedString {
                    append("Your weight is ")
                    withStyle(SpanStyle(bmiCategory.color)) {
                        append(
                            bmiCategory.name.lowercase().replaceFirstChar(Char::uppercase)
                        )
                    }
                }
            )
        }
    }

}