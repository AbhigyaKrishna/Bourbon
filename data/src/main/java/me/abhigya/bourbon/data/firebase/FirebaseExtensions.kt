package me.abhigya.bourbon.data.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlin.coroutines.cancellation.CancellationException

inline fun <T, R> Task<T>.handle(
    crossinline failureTransform: (Throwable) -> R,
    crossinline successScope: ProducerScope<R>.(T) -> Unit
): Flow<R> = channelFlow {
    addOnSuccessListener {
        successScope(it)
    }.addOnFailureListener {
        trySendBlocking(failureTransform(it))
    }.addOnCanceledListener {
        trySendBlocking(failureTransform(CancellationException()))
    }
}

inline fun <T, R> Task<T>.handleAsResult(crossinline successScope: ProducerScope<Result<R>>.(T) -> Unit): Flow<Result<R>> {
    return handle({ Result.failure(it) }, successScope)
}

fun DatabaseReference.valueEvent(): Flow<DataSnapshot> = callbackFlow {
    val listener = addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            trySendBlocking(snapshot)
        }

        override fun onCancelled(error: DatabaseError) {
            close(error.toException())
        }
    })

    awaitClose {
        removeEventListener(listener)
    }
}

fun DatabaseReference.valueEventOnce(): Flow<DataSnapshot> = callbackFlow {
    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            trySendBlocking(snapshot)
            close()
        }

        override fun onCancelled(error: DatabaseError) {
            close(error.toException())
        }
    }
    addListenerForSingleValueEvent(listener)

    awaitClose()
}

inline fun <reified T> DataSnapshot.value(serializer: FirebaseSerializer = FirebaseSerializer()): T? = value?.let { serializer.decode<T>(it) }

inline fun <reified T> DataSnapshot.valueOrThrow(serializer: FirebaseSerializer = FirebaseSerializer()): T = value(serializer) ?: throw NullPointerException()

inline fun <reified T> DatabaseReference.value(value: T, serializer: FirebaseSerializer = FirebaseSerializer()) {
    setValue(serializer.encode(value))
}

fun DatabaseReference.getAsFlow(): Flow<DataSnapshot> = callbackFlow {
    get().addOnSuccessListener {
        trySendBlocking(it)
        close()
    }.addOnFailureListener {
        throw it
    }.addOnCanceledListener {
        throw CancellationException()
    }

    awaitClose()
}

inline fun <reified T> DatabaseReference.get(serializer: FirebaseSerializer = FirebaseSerializer()): Flow<T> = callbackFlow {
    get().addOnSuccessListener {
        trySendBlocking(it.valueOrThrow<T>(serializer))
        close()
    }.addOnFailureListener {
        throw it
    }.addOnCanceledListener {
        throw CancellationException()
    }

    awaitClose()
}
