package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import java.io.File

interface BaseRepository<T : Any> {
    val data: Flow<PagingData<T>>
    suspend fun save(item: T)
    suspend fun saveWithAttachment(item: T, file: File)
    suspend fun delete(id: Long)
    suspend fun likeById(id: Long)
    suspend fun dislikeById(id: Long)
    suspend fun upload(file: File): Media
    suspend fun getById(id: Long): T
}