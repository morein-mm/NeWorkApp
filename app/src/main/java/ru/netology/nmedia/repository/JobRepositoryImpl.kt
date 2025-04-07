package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okio.IOException
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.JobDao
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.entity.JobEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dao: JobDao,
) : JobRepository {

    override val data: Flow<List<Job>> = dao.getAll().map {
        it.toDto()
    }.flowOn(Dispatchers.Default)

    override suspend fun save(job: Job) {
        try {
            val response = apiService.saveJob(job)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val body = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(JobEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun delete(id: Long) {
        try {
            val response = apiService.deleteJobById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            dao.removeById(id)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}
