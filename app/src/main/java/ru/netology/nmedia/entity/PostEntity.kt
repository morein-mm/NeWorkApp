package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.UserPreview

@Entity
data class PostEntity(
    @PrimaryKey
    val id: Long,
    val author: String,
    val authorId: Long,
    val authorJob: String?,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    val coordsLat: Double?,
    val coordsLong: Double?,
    val link: String? = null,
    val likeOwnerIds: List<Long>?,
    val likedByMe: Boolean,
    val mentionIds: List<Long>?,
    val mentionedMe: Boolean = false,
    @Embedded(prefix = "attachment_")
    var attachment: AttachmentEmbeddable?,
    val users: Map<Long, UserPreview>?,
) {
    fun toDto() = Post(
        id,
        author,
        authorId,
        authorJob,
        authorAvatar,
        content,
        published,
        if (coordsLat != null && coordsLong != null) Coordinates(coordsLat, coordsLong) else null,
        link,
        likeOwnerIds,
        likedByMe,
        mentionIds,
        mentionedMe,
        attachment?.toDto(),
        users,
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.author,
                dto.authorId,
                dto.authorJob,
                dto.authorAvatar,
                dto.content,
                dto.published,
                dto.coords?.lat,
                dto.coords?.long,
                dto.link,
                dto.likeOwnerIds,
                dto.likedByMe,
                dto.mentionIds,
                dto.mentionedMe,
                AttachmentEmbeddable.fromDto(dto.attachment),
                dto.users
            )

    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> =
    map { PostEntity.fromDto(it) }