package dev.sunnyday.postcreator.core.common.util

import java.lang.ref.Reference
import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Weak<T: Any>(value: T? = null): ReferenceWrapper<T>(value) {

    override fun wrap(value: T) = WeakReference(value)

}

abstract class ReferenceWrapper<T: Any>(value: T? = null) {

    private var ref: Reference<T>? = null

    var value: T?
        get() = synchronized(this) { ref?.get() }
        set(value) = synchronized(this) {
            ref = when {
                value == null -> null
                value === this.value -> ref
                else -> wrap(value)
            }
        }

    init {
        this.value = value
    }

    protected abstract fun wrap(value: T): Reference<T>

    operator fun component1(): T? = value

}

fun <T: Any> weak(
    value: T? = null,
    onSet: ((T?) -> Unit)? = null
): ReadWriteProperty<Any, T?> = object : ReadWriteProperty<Any, T?> {

    private val valueWrapper = Weak(value)

    override fun getValue(thisRef: Any, property: KProperty<*>): T? = valueWrapper.value

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        valueWrapper.value = value
        onSet?.invoke(value)
    }

}