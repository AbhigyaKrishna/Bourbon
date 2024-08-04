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
value class Kilograms(val value: Float = 0f)

enum class Gender {
    Male,
    Female
}

enum class AgeGroup(val display: String) {
    _18_29("18-29"),
    _30_39("30-39"),
    _40_49("40-49"),
    _50("50+"),
}