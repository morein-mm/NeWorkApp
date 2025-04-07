package ru.netology.nmedia.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.Response
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dao.PostRemoteKeyDao
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.PostRemoteKeyEntity
import ru.netology.nmedia.error.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val apiService: ApiService,
    private val dao: PostDao,
    private val remoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>
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
        state: PagingState<Int, PostEntity>
    ): Response<List<Post>>? {
        return when (loadType) {
            LoadType.REFRESH -> apiService.getLatestPosts(state.config.pageSize)
            LoadType.APPEND -> {
                val id = remoteKeyDao.min() ?: return null
                apiService.getPostsBefore(id, state.config.pageSize)
            }

            LoadType.PREPEND -> {
                val id = remoteKeyDao.max() ?: return null
                apiService.getPostsNewerThan(id, state.config.pageSize)
            }
        }
    }

    /**
     * Обновляет базу данных на основе загруженных данных.
     */
    private suspend fun updateDatabase(
        loadType: LoadType,
        body: List<Post>
    ) {
        when (loadType) {
            LoadType.REFRESH -> {
                // Очистка данных и добавление новых ключей
                remoteKeyDao.clear()
                remoteKeyDao.insert(
                    listOf(
                        PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.AFTER, body.first().id),
                        PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.BEFORE, body.last().id),
                    )
                )
                dao.clear()
            }

            LoadType.PREPEND -> {
                remoteKeyDao.insert(
                    PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.AFTER, body.first().id)
                )
            }

            LoadType.APPEND -> {
                remoteKeyDao.insert(
                    PostRemoteKeyEntity(PostRemoteKeyEntity.KeyType.BEFORE, body.last().id)
                )
            }
        }

        // Добавление новых данных в таблицу Post
        dao.insert(body.map { PostEntity.fromDto(it) })
    }

}