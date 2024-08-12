package me.abhigya.bourbon.data

import android.content.Context
import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import me.abhigya.bourbon.domain.GeminiRepository
import me.abhigya.bourbon.domain.entities.ActivityLevel
import me.abhigya.bourbon.domain.entities.Diet
import me.abhigya.bourbon.domain.entities.Exercise
import me.abhigya.bourbon.domain.entities.UserData
import java.time.DayOfWeek

class GeminiRepositoryImpl(private val context: Context) : GeminiRepository {

    private val api = GenerativeModel(
        modelName = "gemini-1.5-pro",
        apiKey = BuildConfig.apiKey
    )
    @OptIn(ExperimentalSerializationApi::class)
    private val json: Json = Json {
        ignoreUnknownKeys = true
        decodeEnumsCaseInsensitive = true
    }

    override fun prompt(text: String, image: Bitmap?): Flow<Result<String>> {
        return query(
            content {
                text(text)
                image?.let { image(it) }
            }
        )
    }

    override fun promptCalorieFetch(item: String, amount: Float, unit: String): Flow<Result<String>> {
        return query(
            content {
                text("How many calories are there in $amount $unit of $item?")
            }
        )
    }

    override fun promptUserExercisePlan(userData: UserData): Flow<Result<Map<DayOfWeek, List<Exercise>>>> {
        return query(
            content {
                text(context.getString(
                    R.string.ai_prompt_exercise,
                    userData.gender.name,
                    userData.weight.value,
                    userData.height.value,
                    userData.age,
                    userData.goal.display,
                    userData.aimWeight.value,
                    userData.training.joinToString(", "),
                    when (userData.activityLevel) {
                        ActivityLevel.Sedentary -> 3
                        ActivityLevel.Moderate -> 5
                        ActivityLevel.Active -> 8
                    },
                    userData.workoutDays.joinToString(", "),
                    userData.equipments.joinToString(", ").ifEmpty { "None" },
                ).trimIndent())
            }
        )
            .map { it.getOrThrow().trimToJson() }
            .map { json.decodeFromString<Map<DayOfWeek, List<Exercise>>>(it) }
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    override fun promptUserDietPlan(userData: UserData): Flow<Result<Map<DayOfWeek, Diet>>> {
        return query(
            content {
                text(context.getString(
                    R.string.ai_prompt_diet,
                    userData.gender.name,
                    userData.weight.value,
                    userData.height.value,
                    userData.age,
                    userData.goal.display,
                    userData.aimWeight.value,
                    userData.mealFrequency,
                    userData.dietPreference.display,
                    userData.location.ifEmpty { "Anywhere" }
                ).trimIndent())
            }
        )
            .map { it.getOrThrow().trimToJson() }
            .map { json.decodeFromString<Map<DayOfWeek, Diet>>(it) }
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    private fun String.trimToJson(): String {
        return dropWhile { it != '`' }
            .dropLastWhile { it != '`' }
            .replace("```json", "")
            .replace("```", "")
            .trim()
    }

    private fun query(content: Content): Flow<Result<String>> {
        return flow {
            runCatching {
                api.generateContent(content)
            }
                .onFailure {
                    emit(Result.failure(it))
                }
                .onSuccess {
                    emit(Result.success(it.text ?: ""))
                }
        }
    }

}