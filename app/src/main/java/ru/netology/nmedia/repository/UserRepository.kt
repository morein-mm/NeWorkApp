package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Token
import ru.netology.nmedia.dto.User
import java.io.File

interface UserRepository {
    val data: Flow<List<User>>
    suspend fun getAllUsers(): List<User>
    suspend fun getById(id: Long): User
    suspend fun login(login: String, pass: String): Token
    suspend fun registerUser(
        login: String,
        pass: String,
        name: String,
        file: File? = null
    ): Token
}