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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import me.abhigya.bourbon.data.firebase.get
import me.abhigya.bourbon.data.firebase.handleAsResult
import me.abhigya.bourbon.domain.ExerciseRepository
import me.abhigya.bourbon.domain.entities.ExerciseData
import java.util.concurrent.ConcurrentHashMap

class ExerciseRepositoryImpl(context: Context) : ExerciseRepository {

    private val cache: MutableMap<String, ExerciseData> = ConcurrentHashMap()
    private val database: DatabaseReference = Firebase.database(context.getString(R.string.database_url))
        .getReference("exercise-data")
    private val storage: FirebaseStorage = Firebase.storage(context.getString(R.string.storage_url))
    private val imageStorage: StorageReference = storage.getReference("images")
    private val videoStorage: StorageReference = storage.getReference("videos")

    override fun getExerciseData(id: String): Flow<Result<ExerciseData>> {
        if (id in cache) {
            return flowOf(Result.success(cache[id]!!))
        }

        return database
            .child(id)
            .get<ExerciseData>()
            .onEach { cache[it.id] = it }
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    @kotlin.OptIn(ExperimentalCoroutinesApi::class)
    override fun getExerciseData(id: Iterable<String>): Flow<Result<ExerciseData>> {
        val (cached, remaining) = id.partition { it in cache }
        val flow = cached.asFlow().mapNotNull { cache[it] }
            .map { Result.success(it) }

        if (remaining.isNotEmpty()) {
            val missing = remaining.asFlow()
                .flatMapConcat { database.child(it).get<ExerciseData>() }
                .onEach { cache[it.id] = it }
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }

            return flowOf(flow, missing).flattenConcat()
        }

        return flow
    }

    override fun getExerciseImageById(file: String): Flow<Result<Bitmap>> {
        return imageStorage
            .child(file)
            .getBytes(1024 * 1024 * 10)
            .handleAsResult {
                trySend(Result.success(BitmapFactory.decodeByteArray(it, 0, it.size)))
            }
    }

    @OptIn(UnstableApi::class)
    override fun getExerciseVideoById(file: String, context: Context): Flow<Result<ExoPlayer>> {
        return videoStorage
            .child(file)
            .getBytes(1024 * 1024 * 50)
            .handleAsResult {
                val ds = ByteArrayDataSource(it)
                val factory = DataSource.Factory { ds }
                val mediaSource = ProgressiveMediaSource.Factory(factory)
                    .createMediaSource(MediaItem.fromUri(file))
                val player = ExoPlayer.Builder(context)
                    .setTrackSelector(DefaultTrackSelector(context))
                    .build()
                player.setMediaSource(mediaSource)
                player.prepare()
                trySend(Result.success(player))
            }
    }
}