package ru.netology.nmedia.dto

data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String?,
    val isSelected: Boolean = false, // Нет на сервере, заполняется в приложении, чтобы отображать выбранных пользователей
)

data class UserPreview(
    val name: String,
    val avatar: String?
)