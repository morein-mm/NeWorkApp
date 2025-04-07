package ru.netology.nmedia.error

sealed class AppError(var code: String): RuntimeException()
class ApiError(val code: Int, message: String) : Exception(message)
object NetworkError : AppError("error_network")
object UnknownError: AppError("error_unknown")