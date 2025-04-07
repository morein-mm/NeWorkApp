package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.UserEntity

@Dao
interface UserDao {

    @Query("SELECT * FROM UserEntity ORDER BY id DESC")
    fun getAll(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: List<UserEntity>)

    @Query("SELECT * FROM UserEntity WHERE id = :id")
    suspend fun getById(id: Long): UserEntity?

    @Query("DELETE FROM UserEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM UserEntity")
    suspend fun clear()
}