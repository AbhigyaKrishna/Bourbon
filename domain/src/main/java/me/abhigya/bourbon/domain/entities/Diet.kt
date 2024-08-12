package me.abhigya.bourbon.domain.entities

import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class Diet(
    val calorieIntake: Int,
    val food: List<Food>
)

@Serializable
data class Food(
    val id: String,
    val name: String,
    val description: String,
    val calories: Int,
    val type: DietPreference
)