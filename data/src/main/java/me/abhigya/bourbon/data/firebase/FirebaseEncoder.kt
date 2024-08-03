package me.abhigya.bourbon.data.firebase

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

class FirebaseEncoder(
    internal val settings: FirebaseSerializerSettings,
) : Encoder {

    internal var value: Any? = null

    override val serializersModule: SerializersModule = settings.serializersModule

    private var polymorphicDiscriminator: String? = null

    @OptIn(ExperimentalSerializationApi::class)
    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        val encoder = structureEncoder(descriptor)
        if (polymorphicDiscriminator != null) {
            encoder.encodePolymorphicClassDiscriminator(polymorphicDiscriminator!!, descriptor.serialName)
            polymorphicDiscriminator = null
        }
        return encoder
    }

    override fun encodeBoolean(value: Boolean) {
        this.value = value
    }

    override fun encodeByte(value: Byte) {
        this.value = value
    }

    override fun encodeChar(value: Char) {
        this.value = value
    }

    override fun encodeDouble(value: Double) {
        this.value = value
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        this.value = enumDescriptor.getElementName(index)
    }

    override fun encodeFloat(value: Float) {
        this.value = value
    }

    override fun encodeInt(value: Int) {
        this.value = value
    }

    override fun encodeLong(value: Long) {
        this.value = value
    }

    @ExperimentalSerializationApi
    override fun encodeNotNullMark() {
        // no-op
    }

    @ExperimentalSerializationApi
    override fun encodeNull() {
        this.value = null
    }

    override fun encodeShort(value: Short) {
        this.value = value
    }

    override fun encodeString(value: String) {
        this.value = value
    }

    override fun encodeInline(descriptor: SerialDescriptor): Encoder = this

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        encodePolymorphically(serializer, value) {
            polymorphicDiscriminator = it
        }
    }
}

open class FirebaseCompositeEncoder(
    private val settings: FirebaseSerializerSettings,
    private val end: () -> Unit = {},
    private val setPolymorphicType: (String, String) -> Unit = { _, _ -> },
    private val set: (descriptor: SerialDescriptor, index: Int, value: Any?) -> Unit,
) : CompositeEncoder {

//    private fun <T> SerializationStrategy<T>.toFirebase(): SerializationStrategy<T> = when(descriptor.kind) {
//        StructureKind.MAP -> FirebaseMapSerializer<Any>(descriptor.getElementDescriptor(1)) as SerializationStrategy<T>
//        StructureKind.LIST -> FirebaseListSerializer<Any>(descriptor.getElementDescriptor(0)) as SerializationStrategy<T>
//        else -> this
//    }

    override val serializersModule: SerializersModule = settings.serializersModule

    override fun endStructure(descriptor: SerialDescriptor): Unit = end()

    @ExperimentalSerializationApi
    override fun shouldEncodeElementDefault(descriptor: SerialDescriptor, index: Int): Boolean = settings.encodeDefaults

    @ExperimentalSerializationApi
    override fun <T : Any> encodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T?,
    ): Unit = set(
        descriptor,
        index,
        value?.let {
            FirebaseEncoder(settings).apply {
                encodeSerializableValue(serializer, value)
            }.value
        },
    )

    override fun <T> encodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        serializer: SerializationStrategy<T>,
        value: T,
    ): Unit = set(
        descriptor,
        index,
        FirebaseEncoder(settings).apply {
            encodeSerializableValue(serializer, value)
        }.value,
    )

    fun <T> encodeObject(descriptor: SerialDescriptor, index: Int, value: T): Unit = set(descriptor, index, value)

    override fun encodeBooleanElement(descriptor: SerialDescriptor, index: Int, value: Boolean): Unit = set(descriptor, index, value)

    override fun encodeByteElement(descriptor: SerialDescriptor, index: Int, value: Byte): Unit = set(descriptor, index, value)

    override fun encodeCharElement(descriptor: SerialDescriptor, index: Int, value: Char): Unit = set(descriptor, index, value)

    override fun encodeDoubleElement(descriptor: SerialDescriptor, index: Int, value: Double): Unit = set(descriptor, index, value)

    override fun encodeFloatElement(descriptor: SerialDescriptor, index: Int, value: Float): Unit = set(descriptor, index, value)

    override fun encodeIntElement(descriptor: SerialDescriptor, index: Int, value: Int): Unit = set(descriptor, index, value)

    override fun encodeLongElement(descriptor: SerialDescriptor, index: Int, value: Long): Unit = set(descriptor, index, value)

    override fun encodeShortElement(descriptor: SerialDescriptor, index: Int, value: Short): Unit = set(descriptor, index, value)

    override fun encodeStringElement(descriptor: SerialDescriptor, index: Int, value: String): Unit = set(descriptor, index, value)

    override fun encodeInlineElement(descriptor: SerialDescriptor, index: Int): Encoder = FirebaseEncoder(settings)

    fun encodePolymorphicClassDiscriminator(discriminator: String, type: String) {
        setPolymorphicType(discriminator, type)
    }

}

@OptIn(ExperimentalSerializationApi::class)
private fun FirebaseEncoder.structureEncoder(descriptor: SerialDescriptor): FirebaseCompositeEncoder {
    return when (descriptor.kind) {
        StructureKind.LIST -> mutableListOf<Any?>()
            .also { value = it }
            .let { FirebaseCompositeEncoder(settings) { _, index, value -> it.add(index, value) } }
        StructureKind.MAP -> mutableListOf<Any?>()
            .let { FirebaseCompositeEncoder(settings, { value = it.chunked(2).associate { (k, v) -> k to v } }) { _, _, value -> it.add(value) } }
        StructureKind.CLASS, StructureKind.OBJECT -> encodeAsMap(descriptor)
        is PolymorphicKind -> encodeAsMap(descriptor)
        else -> throw UnsupportedOperationException()
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun FirebaseEncoder.encodeAsMap(descriptor: SerialDescriptor): FirebaseCompositeEncoder {
    val map = mutableMapOf<Any?, Any?>()
    value = map
    return FirebaseCompositeEncoder(
        settings,
        setPolymorphicType = { discriminator, type ->
            map[discriminator] = type
        },
        set = { _, index, value -> map[descriptor.getElementName(index)] = value },
    )
}