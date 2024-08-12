package me.abhigya.bourbon.domain.entities

import kotlinx.serialization.Serializable

@Serializable
open class Exercise(
    val id: String,
    val name: String,
    val description: String,
    val duration: Int?,
    val quantity: ExerciseQuantity?
)

@Serializable
data class ExerciseQuantity(
    val amount: String,
    val unit: String
)

data object Rest : Exercise(
    id = "rest",
    name = "Rest Card",
    description = "Take a break and relax.",
    duration = 15,
    quantity = null
)