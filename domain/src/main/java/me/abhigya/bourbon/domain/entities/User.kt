package me.abhigya.bourbon.domain.entities

import kotlinx.serialization.Serializable

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val data: UserData = UserData(),
)

@Serializable
data class UserData(
    val gender: Gender = Gender.Male,
    val height: Centimeters = Centimeters(),
    val weight: Kilograms = Kilograms(),
)

@Serializable
@JvmInline
value class Centimeters(val value: Int = 0)

@Serializable
@JvmInline
value class Kilograms(val value: Int = 0)

enum class Gender {
    Male,
    Female
}