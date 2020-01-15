package dev.sunnyday.postcreator.postcreator.util

import androidx.lifecycle.SavedStateHandle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun saveableInt(defaultValue: Int, handle: SavedStateHandle): ReadWriteProperty<Any, Int> = object : ReadWriteProperty<Any, Int> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Int =
        handle[property.name] ?: defaultValue

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        handle[property.name] = value
    }

}

fun saveableOptionalLong(handle: SavedStateHandle): ReadWriteProperty<Any, Long?> = object : ReadWriteProperty<Any, Long?> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Long? =
        handle[property.name]

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long?) {
        if (value == null) {
            handle.remove<Long>(property.name)
        } else {
            handle[property.name] = value
        }
    }

}