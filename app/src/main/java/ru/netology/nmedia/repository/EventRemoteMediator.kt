package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.Response
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.EventDao
import ru.netology.nmedia.dao.EventRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.entity.EventEntity
import ru.netology.nmedia.entity.EventRemoteKeyEntity
import ru.netology.nmedia.error.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator(
    private val apiService: ApiService,
    private val dao: EventDao,
    private val remoteKeyDao: EventRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, EventEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): MediatorResult {
        try {
            // Получаем данные с сервера в зависимости от типа загрузки
            val response = fetchFromApi(loadType, state) ?: return MediatorResult.Success(
                endOfPaginationReached = true
            )

            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }

            val body = response.body() ?: throw ApiError(response.code(), response.message())

            // Если данные пустые, завершить пагинацию
            if (body.isEmpty()) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            // Транзакция для обновления базы данных
            appDb.withTransaction {
                updateDatabase(loadType, body)
            }

            return MediatorResult.Success(endOfPaginationReached = false)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: ApiError) {
            return MediatorResult.Error(e)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    /**
     * Получает данные с сервера в зависимости от типа загрузки.
     */
    private suspend fun fetchFromApi(
        loadType: LoadType,
        state: PagingState<Int, EventEntity>
    ): Response<List<Event>>? {
        return when (loadType) {
            LoadType.REFRESH -> apiService.getLatestEvents(state.config.pageSize)
            LoadType.APPEND -> {
                val id = remoteKeyDao.min() ?: return null
                apiService.getEventsBefore(id, state.config.pageSize)
            }

            LoadType.PREPEND -> {
                val id = remoteKeyDao.max() ?: return null
                apiService.getEventsNewerThan(id, state.config.pageSize)
            }
        }
    }

    /**
     * Обновляет базу данных на основе загруженных данных.
     */
    private suspend fun updateDatabase(
        loadType: LoadType,
        body: List<Event>
    ) {
        when (loadType) {
            LoadType.REFRESH -> {
                // Очистка данных и добавление новых ключей
                remoteKeyDao.clear()
                remoteKeyDao.insert(
                    listOf(
                        EventRemoteKeyEntity(EventRemoteKeyEntity.KeyType.AFTER, body.first().id),
                        EventRemoteKeyEntity(EventRemoteKeyEntity.KeyType.BEFORE, body.last().id),
                    )
                )
                dao.clear()
            }

            LoadType.PREPEND -> {
                remoteKeyDao.insert(
                    EventRemoteKeyEntity(EventRemoteKeyEntity.KeyType.AFTER, body.first().id)
                )
            }

            LoadType.APPEND -> {
                remoteKeyDao.insert(
                    EventRemoteKeyEntity(EventRemoteKeyEntity.KeyType.BEFORE, body.last().id)
                )
            }
        }

        // Добавление новых данных в таблицу Post
        dao.insert(body.map { EventEntity.fromDto(it) })
    }

}