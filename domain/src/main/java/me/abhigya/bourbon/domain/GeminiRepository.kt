package me.abhigya.bourbon.domain

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

interface GeminiRepository {

    fun prompt(text: String, image: Bitmap?): Flow<Result<String>>

    fun promptCalorieFetch(item: String, amount: Float, unit: String): Flow<Result<String>>

}