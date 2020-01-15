package dev.sunnyday.postcreator.core.app.util

import androidx.lifecycle.SavedStateHandle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T: Any> restorable(defaultValue: T, handle: SavedStateHandle): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T =
        handle[property.name] ?: defaultValue

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        handle[property.name] = value
    }

}

fun <T: Any> restorable(handle: SavedStateHandle): ReadWriteProperty<Any, T?> = object : ReadWriteProperty<Any, T?> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T? =
        handle[property.name]

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        if (value == null) {
            handle.remove<T?>(property.name)
        } else {
            handle[property.name] = value
        }
    }

}