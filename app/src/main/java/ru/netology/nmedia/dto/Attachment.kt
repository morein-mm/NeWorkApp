package ru.netology.nmedia.dto

import ru.netology.nmedia.enumeration.AttachmentType

data class Attachment(
    val url: String,
    val type: AttachmentType
)

data class Media(val url: String)