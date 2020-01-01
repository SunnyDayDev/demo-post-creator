package dev.sunnyday.postcreator.domain.backgrounds.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
internal abstract class BackgroundsDao {

    @get:Query("SELECT * FROM backgrounds")
    abstract val items: Observable<List<BackgroundEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(item: BackgroundEntity): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(items: List<BackgroundEntity>): Completable

}