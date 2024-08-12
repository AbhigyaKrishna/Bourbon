package me.abhigya.bourbon.domain

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow
import me.abhigya.bourbon.domain.entities.Diet
import me.abhigya.bourbon.domain.entities.Exercise
import me.abhigya.bourbon.domain.entities.UserData
import java.time.DayOfWeek

interface GeminiRepository {

    fun prompt(text: String, image: Bitmap?): Flow<Result<String>>

    fun promptCalorieFetch(item: String, amount: Float, unit: String): Flow<Result<String>>

    fun promptUserExercisePlan(userData: UserData): Flow<Result<Map<DayOfWeek, List<Exercise>>>>

    fun promptUserDietPlan(userData: UserData): Flow<Result<Map<DayOfWeek, Diet>>>

}