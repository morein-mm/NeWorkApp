package ru.netology.nmedia.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.dao.UserDao
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.entity.UserEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dao: UserDao,
) : UserRepository {
    // Данные пользователей
    override val data: Flow<List<User>> = dao.getAll().map { entities ->
        entities.toDto()
    }.flowOn(Dispatchers.Default)

    override suspend fun getAllUsers(): List<User> {
        try {
            val response = apiService.getAllUsers()
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val users = response.body() ?: throw ApiError(response.code(), response.message())
            // Очищаем локальную базу и сохраняем новых пользователей
            dao.clear()
            dao.insert(users.toEntity())
            return users
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun getById(id: Long): User {
        try {
            val response = apiService.getUserById(id)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            val user = response.body() ?: throw ApiError(response.code(), response.message())
            dao.insert(UserEntity.fromDto(user))
            return user
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    // Авторизация и регистрация
    override suspend fun login(login: String, pass: String): Token {
        try {
            val response = apiService.authenticateUser(login, pass)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun registerUser(
        login: String,
        pass: String,
        name: String,
        file: File?
    ): Token {
        try {
            val loginPart = login.toRequestBody()
            val passPart = pass.toRequestBody()
            val namePart = name.toRequestBody()
            val avatarPart = file?.let {
                MultipartBody.Part.createFormData("file", it.name, it.asRequestBody())
            }
            val response =
                apiService.registerUser(loginPart, passPart, namePart, avatarPart)
            if (!response.isSuccessful) throw ApiError(response.code(), response.message())
            return response.body() ?: throw ApiError(response.code(), response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }
}
