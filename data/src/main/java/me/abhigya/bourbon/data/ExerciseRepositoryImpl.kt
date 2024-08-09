package me.abhigya.bourbon.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.ByteArrayDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import me.abhigya.bourbon.data.firebase.handleAsResult
import me.abhigya.bourbon.data.firebase.valueEvent
import me.abhigya.bourbon.data.firebase.valueEventOnce
import me.abhigya.bourbon.data.firebase.valueOrThrow
import me.abhigya.bourbon.domain.ExerciseRepository
import me.abhigya.bourbon.domain.entities.Exercise

class ExerciseRepositoryImpl(private val context: Context) : ExerciseRepository {

    private val database: DatabaseReference = Firebase.database(context.getString(R.string.database_url)).getReference("bourbon")
    private val storage: FirebaseStorage = Firebase.storage(context.getString(R.string.storage_url))
    private val imageStorage: StorageReference = storage.getReference("images")
    private val videoStorage: StorageReference = storage.getReference("videos")

    override fun getExercises(): Flow<Result<Map<String, Exercise>>> {
        return database.child("exercise")
            .valueEvent()
            .map { Result.success(it.valueOrThrow<Map<String, Exercise>>()) }
            .catch { emit(Result.failure(it)) }
    }

    override fun getExerciseById(id: String): Flow<Result<Exercise>> {
        return database.child("exercise")
            .child(id)
            .valueEventOnce()
            .map { Result.success(it.valueOrThrow<Exercise>()) }
            .catch { emit(Result.failure(it)) }
    }

    override fun getExerciseImageById(id: String): Flow<Result<Bitmap>> {
        return imageStorage
            .child("exercise_$id.jpg")
            .getBytes(1024 * 1024 * 10)
            .handleAsResult {
                trySend(Result.success(BitmapFactory.decodeByteArray(it, 0, it.size)))
            }
    }

    @OptIn(UnstableApi::class)
    override fun getExerciseVideoById(id: String, context: Context): Flow<Result<ExoPlayer>> {
        return videoStorage
            .child("exercise_$id.mp4")
            .getBytes(1024 * 1024 * 50)
            .handleAsResult {
                val ds = ByteArrayDataSource(it)
                val factory = DataSource.Factory { ds }
                val mediaSource = ProgressiveMediaSource.Factory(factory)
                    .createMediaSource(MediaItem.fromUri("exercise_$id.mp4"))
                val player = ExoPlayer.Builder(context)
                    .setTrackSelector(DefaultTrackSelector(context))
                    .build()
                player.setMediaSource(mediaSource)
                player.prepare()
                trySend(Result.success(player))
            }
    }
}