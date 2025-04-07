package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.EventType

data class Event(
    val id: Long,
    val author: String,
    val authorId: Long,
    val authorJob: String? = null,
    val authorAvatar: String? = null,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates?,
    val type: EventType,
    val likeOwnerIds: List<Long>?,
    val likedByMe: Boolean = false,
    val speakerIds: List<Long>?,
    val participantsIds: List<Long>?,
    val participatedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false,
)