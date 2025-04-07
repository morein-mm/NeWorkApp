package ru.netology.nmedia.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.nmedia.dao.EventDao
import ru.netology.nmedia.dao.EventRemoteKeyDao
import ru.netology.nmedia.dao.JobDao
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.dao.UserDao
import ru.netology.nmedia.entity.EventEntity
import ru.netology.nmedia.entity.EventRemoteKeyEntity
import ru.netology.nmedia.entity.JobEntity
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.entity.UserEntity
import ru.netology.nmedia.util.ListLongConverter
import ru.netology.nmedia.util.TypeConverter
import ru.netology.nmedia.util.UserPreviewConverter

@Database(
    entities = [
        PostEntity::class,
        EventEntity::class,
        JobEntity::class,
        UserEntity::class,
        PostRemoteKeyEntity::class,
        EventRemoteKeyEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(TypeConverter::class, UserPreviewConverter::class, ListLongConverter::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun eventDao(): EventDao
    abstract fun jobDao(): JobDao
    abstract fun userDao(): UserDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
}