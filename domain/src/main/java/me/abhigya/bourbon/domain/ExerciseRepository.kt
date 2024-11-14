package me.abhigya.bourbon.domain

import android.content.Context
import android.graphics.Bitmap
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.Flow
import me.abhigya.bourbon.domain.entities.ExerciseData

interface ExerciseRepository {

    fun getExerciseData(id: String): Flow<Result<ExerciseData>>

    fun getExerciseData(id: Iterable<String>): Flow<Result<ExerciseData>>

    fun getExerciseImageById(file: String): Flow<Result<Bitmap>>

    fun getExerciseVideoById(file: String, context: Context): Flow<Result<ExoPlayer>>

}