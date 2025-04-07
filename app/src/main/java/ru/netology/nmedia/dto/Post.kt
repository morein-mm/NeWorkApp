package ru.netology.nmedia.dto

import java.io.Serializable

data class Post(
    val id: Long,
    val author: String,
    val authorId: Long,
    val authorJob: String?,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    val coords: Coordinates?,
    val link: String? = null,
    val likeOwnerIds: List<Long>?,
    val likedByMe: Boolean = false,
    val mentionIds: List<Long>?,
    val mentionedMe: Boolean = false,
    val attachment: Attachment? = null,
    val users: Map<Long, UserPreview>? = null,
    val ownedByMe: Boolean = false, // Нет на сервере, заполняется в приложении, чтобы идентифицировать принадлежащие себе объекты
) : Serializable