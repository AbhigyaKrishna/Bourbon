package me.abhigya.bourbon.domain.entities

import androidx.compose.ui.graphics.Color

const val BMI_MIN_VALUE = 0.0
const val BMI_MAX_VALUE = 49.9

val BMI_UNDERWEIGHT_RANGE = BMI_MIN_VALUE..18.4
val BMI_NORMAL_RANGE = 18.5..24.9
val BMI_OVERWEIGHT_RANGE = 25.0..29.9
val BMI_OBESE_RANGE = 30.0..BMI_MAX_VALUE

enum class BmiCategory(
    val color: Color,
    val range: ClosedFloatingPointRange<Double>
) {
    Underweight(Color(0xFFFDD835), BMI_UNDERWEIGHT_RANGE), // Yellow
    Normal(Color(0xFF66BB6A), BMI_NORMAL_RANGE), // Green
    Overweight(Color(0xFFFFA726), BMI_OVERWEIGHT_RANGE), // Orange
    Obese(Color(0xFFF44336), BMI_OBESE_RANGE); // Red

    companion object {
        fun from(bmi: Double): BmiCategory {
            return entries.first { category ->
                val value = bmi.coerceIn(
                    minimumValue = BMI_MIN_VALUE,
                    maximumValue = BMI_MAX_VALUE
                )
                category.range.contains(value)
            }
        }
    }

}

fun calculateBmi(weightInKg: Int, heightInCm: Int): Double {
    val heightInMeter = heightInCm / 100.0
    return weightInKg / (heightInMeter * heightInMeter)
}