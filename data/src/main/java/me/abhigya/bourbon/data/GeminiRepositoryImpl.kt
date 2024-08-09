package me.abhigya.bourbon.data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.abhigya.bourbon.domain.GeminiRepository

class GeminiRepositoryImpl : GeminiRepository {

    private val api = GenerativeModel(
        modelName = "gemini-1.5-pro",
        apiKey = BuildConfig.apiKey
    )

    override fun prompt(text: String, image: Bitmap?): Flow<Result<String>> {
        return buildPrompt(
            content {
                text(text)
                image?.let { image(it) }
            }
        )
    }

    override fun promptCalorieFetch(item: String, amount: Int, unit: String): Flow<Result<String>> {
        return buildPrompt(
            content {
                text("How many calories are in $amount $unit of $item?")
            }
        )
    }

    private fun buildPrompt(content: Content): Flow<Result<String>> {
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