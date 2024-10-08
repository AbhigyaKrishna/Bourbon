package me.abhigya.bourbon.domain.entities

import kotlinx.serialization.Serializable
import java.time.DayOfWeek

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val data: UserData = UserData(),
    val exercises: Map<DayOfWeek, List<Exercise>> = emptyMap(),
    val diet: Map<DayOfWeek, Diet> = emptyMap(),
)

@Serializable
data class UserData(
    val gender: Gender = Gender.Male,
    val height: Centimeters = Centimeters(),
    val weight: Kilograms = Kilograms(),
    val age: Int = 5,
    val goal: Goal = Goal.WeightLoss,
    val aimWeight: Kilograms = Kilograms(),
    val training: Set<DefaultTraining> = mutableSetOf(),
    val workoutDays: Set<DayOfWeek> = mutableSetOf(),
    val activityLevel: ActivityLevel = ActivityLevel.Sedentary,
    val dietGuide: DietGuide = DietGuide.PreMade,
    val dietPreference: DietPreference = DietPreference.Vegetarian,
    val mealFrequency: Int = 1,
    val equipments: Set<String> = mutableSetOf(),
    val location: String = "",
)

@Serializable
@JvmInline
value class Centimeters(val value: Int = 0)

@Serializable
@JvmInline
value class Kilograms(val value: Int = 0)

enum class Gender {
    Male,
    Female,
    ;
}

enum class Goal(val display: String) {
    WeightLoss("Weight Loss"),
    WeightGain("Weight Gain"),
    GainMuscles("Gain Muscles"),
    ;
}

enum class DefaultTraining {
    Arms,
    Chest,
    Shoulder,
    Core,
    Legs,
    ;
}

enum class ActivityLevel {
    Sedentary,
    Moderate,
    Active,
    ;
}

enum class DietPreference(val display: String) {
    Vegetarian("Veg"),
    NonVegetarian("Non-Veg"),
    ;
}

enum class DietGuide(val display: String) {
    PreMade("Pre-Made"),
    Create("Create"),
    ;
}

val DayOfWeek.abbreviation: String
    get() = this.name.take(1) + if (this == DayOfWeek.THURSDAY || this == DayOfWeek.SATURDAY) {
         this.name[1].lowercase()
    } else {
        ""
    }