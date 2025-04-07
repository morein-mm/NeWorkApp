package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.nmedia.entity.EventRemoteKeyEntity

//TODO: Разобратья, используются ли оба метода insert, когда используются методы max и min
@Dao
interface EventRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(eventRemoteKeyEntity: EventRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(eventRemoteKeyEntity: List<EventRemoteKeyEntity>)

    @Query("SELECT max(`key`) FROM EventRemoteKeyEntity")
    suspend fun max(): Long?

    @Query("SELECT min(`key`) FROM EventRemoteKeyEntity")
    suspend fun min(): Long?

    @Query("DELETE FROM EventRemoteKeyEntity")
    suspend fun clear()
}