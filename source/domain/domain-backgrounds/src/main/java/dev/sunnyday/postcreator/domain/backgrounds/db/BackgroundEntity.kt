package dev.sunnyday.postcreator.domain.backgrounds.db

import androidx.room.*

@Entity(
    tableName = "backgrounds"
)
@TypeConverters(BackgroundEntityConverters::class)
internal data class BackgroundEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val type: BackgroundEntityType,
    val value: String,
    val icon: String?)

internal enum class BackgroundEntityType {
    COLOR, GRADIENT, RES, STORED;
}

internal object BackgroundEntityConverters {

    @JvmStatic
    @TypeConverter
    fun toInt(type: BackgroundEntityType): Int = type.ordinal

    @JvmStatic
    @TypeConverter
    fun fromInt(position: Int): BackgroundEntityType = BackgroundEntityType.values()[position]

}

