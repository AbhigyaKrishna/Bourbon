package me.abhigya.bourbon.domain

import android.content.Context
import android.graphics.Bitmap
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.Flow
import me.abhigya.bourbon.domain.entities.Exercise

interface ExerciseRepository {

    fun getExercises(): Flow<Result<Map<String, Exercise>>>

    fun getExerciseById(id: String): Flow<Result<Exercise>>

    fun getExerciseImageById(file: String): Flow<Result<Bitmap>>

    fun getExerciseVideoById(file: String, context: Context): Flow<Result<ExoPlayer>>

}