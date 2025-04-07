package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.netology.nmedia.api.*
import ru.netology.nmedia.dao.EventDao
import ru.netology.nmedia.dao.EventRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Attachment
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.entity.EventEntity
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.File
import okio.IOException
import ru.netology.nmedia.dto.Post
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val dao: EventDao,
    private val apiService: ApiService,
    remoteKeyDao: EventRemoteKeyDao,
    appDb: AppDb,
) : BaseRepository<Event> {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<Event>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { dao.getPagingSource() },
        remoteMediator = EventRemoteMediator(
            apiService = apiService,
            dao = dao,
            remoteKeyDao = remoteKeyDao,
            appDb = appDb,
        )
    ).flow.map { pagingData ->
        pagingData.map { entity -> entity.toDto() }
    }

    // Save operations
    override suspend fun save(item: Event) {
        try {
            val response = apiService.saveEvent(item)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun saveWithAttachment(item: Event, file: File) {
        val upload = upload(file)
        val copy = item.copy(attachment = Attachment(upload.url, AttachmentType.IMAGE))
        save(copy)
    }

    // File upload operation
    override suspend fun upload(file: File): Media {
        try {
            val part = MultipartBody.Part.createFormData("file", file.name, file.asRequestBody())
            val response = apiService.upload(part)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    // Delete operation
    override suspend fun delete(id: Long) {
        try {
            val response = apiService.deleteEvent(id)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            dao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    // Like operations
    override suspend fun likeById(id: Long) {
        try {
            val response = apiService.likeEvent(id)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun dislikeById(id: Long) {
        try {
            val response = apiService.dislikeEvent(id)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(EventEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getById(id: Long): Event =
        dao.getById(id).toDto()
}
