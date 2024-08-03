package me.abhigya.bourbon.data.firebase

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

class FirebaseDecoder(internal val value: Any?, internal val settings: FirebaseSerializerSettings) : Decoder {

    override val serializersModule: SerializersModule = settings.serializersModule

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = structureDecoder(descriptor, true)

    override fun decodeString(): String = decodeString(value)

    override fun decodeDouble(): Double = decodeDouble(value)

    override fun decodeLong(): Long = decodeLong(value)

    override fun decodeByte(): Byte = decodeByte(value)

    override fun decodeFloat(): Float = decodeFloat(value)

    override fun decodeInt(): Int = decodeInt(value)

    override fun decodeShort(): Short = decodeShort(value)

    override fun decodeBoolean(): Boolean = decodeBoolean(value)

    override fun decodeChar(): Char = decodeChar(value)

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = decodeEnum(value, enumDescriptor)

    @ExperimentalSerializationApi
    override fun decodeNotNullMark(): Boolean = decodeNotNullMark(value)

    @ExperimentalSerializationApi
    override fun decodeNull(): Nothing? = decodeNull(value)

    override fun decodeInline(descriptor: SerialDescriptor): Decoder = FirebaseDecoder(value, settings)

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T = decodeSerializableValuePolymorphic(value, deserializer)
}

class FirebaseClassDecoder(
    size: Int,
    settings: FirebaseSerializerSettings,
    private val containsKey: (name: String) -> Boolean,
    get: (descriptor: SerialDescriptor, index: Int) -> Any?,
) : FirebaseCompositeDecoder(size, settings, get) {
    private var index: Int = 0

    @ExperimentalSerializationApi
    override fun decodeSequentially(): Boolean = false

    @OptIn(ExperimentalSerializationApi::class)
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = (index until descriptor.elementsCount)
        .firstOrNull {
            !descriptor.isElementOptional(it) || containsKey(descriptor.getElementName(it))
        }
        ?.also { index = it + 1 }
        ?: DECODE_DONE
}

open class FirebaseCompositeDecoder(
    private val size: Int,
    private val settings: FirebaseSerializerSettings,
    private val get: (descriptor: SerialDescriptor, index: Int) -> Any?,
) : CompositeDecoder {

    override val serializersModule: SerializersModule = settings.serializersModule

    @ExperimentalSerializationApi
    override fun decodeSequentially(): Boolean = true

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = throw NotImplementedError()

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = size

    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?,
    ): T = decodeElement(descriptor, index) {
        deserializer.deserialize(FirebaseDecoder(it, settings))
    }

    override fun decodeBooleanElement(descriptor: SerialDescriptor, index: Int): Boolean =
        decodeElement(descriptor, index, ::decodeBoolean)

    override fun decodeByteElement(descriptor: SerialDescriptor, index: Int): Byte =
        decodeElement(descriptor, index, ::decodeByte)

    override fun decodeCharElement(descriptor: SerialDescriptor, index: Int): Char =
        decodeElement(descriptor, index, ::decodeChar)

    override fun decodeDoubleElement(descriptor: SerialDescriptor, index: Int): Double =
        decodeElement(descriptor, index, ::decodeDouble)

    override fun decodeFloatElement(descriptor: SerialDescriptor, index: Int): Float =
        decodeElement(descriptor, index, ::decodeFloat)

    override fun decodeIntElement(descriptor: SerialDescriptor, index: Int): Int =
        decodeElement(descriptor, index, ::decodeInt)

    override fun decodeLongElement(descriptor: SerialDescriptor, index: Int): Long =
        decodeElement(descriptor, index, ::decodeLong)

    @ExperimentalSerializationApi
    override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?,
    ): T? {
        val isNullabilitySupported = deserializer.descriptor.isNullable
        return if (isNullabilitySupported || decodeElement(descriptor, index, ::decodeNotNullMark)) {
            decodeSerializableElement(descriptor, index, deserializer, previousValue)
        } else {
            decodeElement(descriptor, index, ::decodeNull)
        }
    }

    override fun decodeShortElement(descriptor: SerialDescriptor, index: Int): Short =
        decodeElement(descriptor, index, ::decodeShort)

    override fun decodeStringElement(descriptor: SerialDescriptor, index: Int): String =
        decodeElement(descriptor, index, ::decodeString)

    override fun endStructure(descriptor: SerialDescriptor) {}

    @ExperimentalSerializationApi
    override fun decodeInlineElement(descriptor: SerialDescriptor, index: Int): Decoder =
        decodeElement(descriptor, index) {
            FirebaseDecoder(it, settings)
        }

    @OptIn(ExperimentalSerializationApi::class)
    private fun <T> decodeElement(descriptor: SerialDescriptor, index: Int, decoder: (Any?) -> T): T = try {
        decoder(get(descriptor, index))
    } catch (e: Exception) {
        throw SerializationException(
            message = "Exception during decoding ${descriptor.serialName} ${descriptor.getElementName(index)}",
            cause = e,
        )
    }
}

@OptIn(ExperimentalSerializationApi::class)
internal fun FirebaseDecoder.structureDecoder(descriptor: SerialDescriptor, polymorphicIsNested: Boolean): CompositeDecoder {
    return when (descriptor.kind) {
        StructureKind.CLASS, StructureKind.OBJECT -> decodeAsMap(false)
        StructureKind.LIST -> (value as? List<*>).orEmpty().let {
            FirebaseCompositeDecoder(it.size, settings) { _, index -> it[index] }
        }

        StructureKind.MAP -> (value as? Map<*, *>).orEmpty().entries.toList().let {
            FirebaseCompositeDecoder(
                it.size,
                settings,
            ) { _, index -> it[index / 2].run { if (index % 2 == 0) key else value } }
        }

        is PolymorphicKind -> decodeAsMap(polymorphicIsNested)
        else -> throw UnsupportedOperationException()
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun FirebaseDecoder.decodeAsMap(isNestedPolymorphic: Boolean): CompositeDecoder = (value as? Map<*, *>).orEmpty().let { map ->
    FirebaseClassDecoder(map.size, settings, { map.containsKey(it) }) { desc, index ->
        if (isNestedPolymorphic) {
            if (desc.getElementName(index) == "value") {
                map
            } else {
                map[desc.getElementName(index)]
            }
        } else {
            map[desc.getElementName(index)]
        }
    }
}

fun getPolymorphicType(value: Any?, discriminator: String): String = (value as? Map<*, *>).orEmpty()[discriminator] as String

private fun decodeString(value: Any?) = value.toString()

private fun decodeDouble(value: Any?) = when (value) {
    is Number -> value.toDouble()
    is String -> value.toDouble()
    else -> throw SerializationException("Expected $value to be double")
}

private fun decodeLong(value: Any?) = when (value) {
    is Number -> value.toLong()
    is String -> value.toLong()
    else -> throw SerializationException("Expected $value to be long")
}

private fun decodeByte(value: Any?) = when (value) {
    is Number -> value.toByte()
    is String -> value.toByte()
    else -> throw SerializationException("Expected $value to be byte")
}

private fun decodeFloat(value: Any?) = when (value) {
    is Number -> value.toFloat()
    is String -> value.toFloat()
    else -> throw SerializationException("Expected $value to be float")
}

private fun decodeInt(value: Any?) = when (value) {
    is Number -> value.toInt()
    is String -> value.toInt()
    else -> throw SerializationException("Expected $value to be int")
}

private fun decodeShort(value: Any?) = when (value) {
    is Number -> value.toShort()
    is String -> value.toShort()
    else -> throw SerializationException("Expected $value to be short")
}

private fun decodeBoolean(value: Any?) = when (value) {
    is Boolean -> value
    is Number -> value.toInt() != 0
    is String -> value.toBoolean()
    else -> throw SerializationException("Expected $value to be boolean")
}

private fun decodeChar(value: Any?) = when (value) {
    is Number -> value.toInt().toChar()
    is String -> value[0]
    else -> throw SerializationException("Expected $value to be char")
}

private fun decodeEnum(value: Any?, enumDescriptor: SerialDescriptor) = when (value) {
    is Number -> value.toInt()
    is String -> enumDescriptor.getElementIndexOrThrow(value)
    else -> throw SerializationException("Expected $value to be enum")
}

@OptIn(ExperimentalSerializationApi::class)
internal fun SerialDescriptor.getElementIndexOrThrow(name: String): Int {
    val index = getElementIndex(name)
    if (index == CompositeDecoder.UNKNOWN_NAME) {
        throw SerializationException("$serialName does not contain element with name '$name'")
    }
    return index
}

private fun decodeNotNullMark(value: Any?) = value != null

private fun decodeNull(value: Any?) = value as Nothing?