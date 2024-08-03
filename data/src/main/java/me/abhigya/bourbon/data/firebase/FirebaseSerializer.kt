package me.abhigya.bourbon.data.firebase

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InheritableSerialInfo
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer

class FirebaseSerializer(
    private val settings: FirebaseSerializerSettings = FirebaseSerializerSettings()
) {

    fun <T> encode(value: T, strategy: SerializationStrategy<T>): Any? {
        return FirebaseEncoder(settings).apply { encodeSerializableValue(strategy, value) }.value
    }

    fun <T> decode(value: Any?, strategy: DeserializationStrategy<T>): T {
        return FirebaseDecoder(value, settings).decodeSerializableValue(strategy)
    }

    /**
     * Encodes data as an [SerializedObject].
     * This is not recommended for manual use, but may be done by the library internally.
     * @throws IllegalArgumentException if [value] is not valid as an [SerializedObject] (e.g. not encodable in the form Map<String:Any?>
     */
    fun <T> encodeAsObject(value: T, strategy: SerializationStrategy<T>): SerializedObject {
        if (value is Map<*, *> && value.keys.any { it !is String }) {
            throw IllegalArgumentException("$value is a Map containing non-String keys. Must be of the form Map<String, Any?>")
        }
        val encoded = encode(value, strategy) ?: throw IllegalArgumentException("$value was encoded as null. Must be of the form Map<String, Any?>")
        return (encoded as? Map<*, *>)?.asSerializedObject() ?: throw IllegalArgumentException("$value was encoded as ${encoded::class}. Must be of the form Map<String, Any?>")
    }

}

inline fun <reified T> FirebaseSerializer.encode(value: T): Any? = encode(value, serializer())

inline fun <reified T> FirebaseSerializer.decode(value: Any?): T = decode(value, serializer())

inline fun <reified T> FirebaseSerializer.encodeAsObject(value: T): SerializedObject = encodeAsObject(value, serializer())

data class FirebaseSerializerSettings(
    val encodeDefaults: Boolean = true,
    val serializersModule: SerializersModule = EmptySerializersModule()
)

@JvmInline
value class SerializedObject(val raw: Map<String, Any?>)

@PublishedApi
internal fun Map<*, *>.asSerializedObject(): SerializedObject = map { (key, value) ->
    if (key is String) {
        key to value
    } else {
        throw IllegalArgumentException("Expected a String key but received $key")
    }
}.toMap().let(::SerializedObject)

@OptIn(ExperimentalSerializationApi::class)
@InheritableSerialInfo
@Target(AnnotationTarget.CLASS)
annotation class FirebaseClassDiscriminator(val discriminator: String)