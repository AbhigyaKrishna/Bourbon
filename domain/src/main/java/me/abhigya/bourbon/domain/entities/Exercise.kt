package me.abhigya.bourbon.domain.entities

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Serializable
open class Exercise(
    val id: String,
    val name: String,
    val description: String,
    val imageUri: String?,
    val videoUri: String?,
    val duration: Duration,
    val quantity: ExerciseQuantity?
)

@Serializable
data class ExerciseQuantity(
    val amount: Int,
    val unit: String
)

data object Rest : Exercise(
    id = "rest",
    name = "Rest Card",
    description = "Take a break and relax",
    imageUri = null,
    videoUri = null,
    duration = 15.seconds,
    quantity = null
)