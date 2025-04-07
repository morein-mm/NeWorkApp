package ru.netology.nmedia.model

import ru.netology.nmedia.dto.User

data class UsersFeedModel(
    val users: List<User> = emptyList(),
    val empty: Boolean = false,
)

data class UsersModelState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val refreshing: Boolean = false,
)
