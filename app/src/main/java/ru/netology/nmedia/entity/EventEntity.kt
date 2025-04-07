package ru.netology.nmedia.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Coordinates
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.enumeration.EventType

@Entity
data class EventEntity(
    @PrimaryKey
    val id: Long,
    val author: String,
    val authorId: Long,
    val authorJob: String? = null,
    val authorAvatar: String? = null,
    val content: String,
    val datetime: String,
    val published: String,
    val coordsLat: Double?,
    val coordsLong: Double?,
    val type: EventType,
    val likeOwnerIds: List<Long>?,
    val likedByMe: Boolean = false,
    val speakerIds: List<Long>?,
    val participantsIds: List<Long>?,
    val participatedByMe: Boolean = false,
    @Embedded(prefix = "attachment_")
    var attachment: AttachmentEmbeddable? = null,
    val link: String? = null,
    val ownedByMe: Boolean = false,
) {
    fun toDto() = Event(
        id,
        author,
        authorId,
        authorJob,
        authorAvatar,
        content,
        datetime,
        published,
        if (coordsLat != null && coordsLong != null) Coordinates(coordsLat, coordsLong) else null,
        type,
        likeOwnerIds,
        likedByMe,
        speakerIds,
        participantsIds,
        participatedByMe,
        attachment?.toDto(),
        link,
        ownedByMe,
    )

    companion object {
        fun fromDto(dto: Event) =
            EventEntity(
                dto.id,
                dto.author,
                dto.authorId,
                dto.authorJob,
                dto.authorAvatar,
                dto.content,
                dto.datetime,
                dto.published,
                dto.coords?.lat,
                dto.coords?.long,
                dto.type,
                dto.likeOwnerIds,
                dto.likedByMe,
                dto.speakerIds,
                dto.participantsIds,
                dto.participatedByMe,
                AttachmentEmbeddable.fromDto(dto.attachment),
                dto.link,
                dto.ownedByMe,
            )
    }
}

fun List<EventEntity>.toDto(): List<Event> = map(EventEntity::toDto)
fun List<Event>.toEntity(): List<EventEntity> = map { EventEntity.fromDto(it) }