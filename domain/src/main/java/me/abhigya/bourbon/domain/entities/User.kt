package me.abhigya.bourbon.domain.entities

data class User(
    val name: String,
    val email: String,
    val height: Centimeters = Centimeters(),
    val weight: Kilograms = Kilograms(),
)

@JvmInline
value class Centimeters(val value: Int = 0)

@JvmInline
value class Kilograms(val value: Float = 0f)