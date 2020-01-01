package dev.sunnyday.postcreator.domain.backgrounds.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import javax.inject.Inject

@Database(
    entities = [BackgroundEntity::class],
    version = 1,
    exportSchema = true
)
internal abstract class BackgroundsDatabase : RoomDatabase() {

    abstract val backgroundsDao: BackgroundsDao

    class Factory @Inject constructor(private val context: Context) {

        fun create(): BackgroundsDatabase = Room
            .databaseBuilder(context, BackgroundsDatabase::class.java, "backgrounds.db")
            .build()

    }

}