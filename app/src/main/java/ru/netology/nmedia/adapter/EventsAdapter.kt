package ru.netology.nmedia.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardEventBinding
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.view.load
import ru.netology.nmedia.view.loadCircleCrop
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class EventsAdapter(
    private val onEventInteractionListener: OnEventInteractionListener,
    private val isDetailedView: Boolean = false,
    private val context: Context,
) :
    PagingDataAdapter<Event, EventViewHolder>(EventDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onEventInteractionListener, isDetailedView, context)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position) ?: return
        holder.bind(event)
    }
}

class EventViewHolder(
    private val binding: CardEventBinding,
    private val onEventInteractionListener: OnEventInteractionListener,
    private val isDetailedView: Boolean = false,
    private val context: Context,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(event: Event) {
        binding.apply {

            eventCard.setOnClickListener {
                onEventInteractionListener.onEventDetails(event)
            }

            // Установка данных поста
            cardHeader.header.text = event.author

            val inputFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault())
            val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm", Locale.getDefault())

            val formattedDate = try {
                ZonedDateTime.parse(event.published, inputFormatter).format(outputFormatter)
            } catch (e: Exception) {
                event.published // Если формат некорректен, оставляем исходную строку
            }

            cardHeader.subheader.text = if (isDetailedView) {
                if (event.authorJob.isNullOrBlank()) {
                    context.getString(R.string.looking_for_a_job) // Строка из ресурсов, если нет данных
                } else {
                    event.authorJob
                }
            } else {
                formattedDate
            }

            // Скрыть чекбокс и кнопку удаления
            cardHeader.selectCheckbox.visibility = View.GONE
            cardHeader.delete.visibility = View.GONE

            // Показать или скрыть кнопку меню в зависимости от ownedByMe
            cardHeader.menu.visibility = if (event.ownedByMe) View.VISIBLE else View.GONE


            cardHeader.menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_feeditem)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onEventInteractionListener.onRemove(event)
                                true
                            }

                            R.id.edit -> {
                                onEventInteractionListener.onEdit(event)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }


            cardContent.content.text = event.content

            // Отображение аватара автора
            if (event.authorAvatar != null) {
                cardHeader.monogramBgAvatar.loadCircleCrop(event.authorAvatar)
                cardHeader.monogramm.visibility = View.GONE
            } else {
                cardHeader.monogramBgAvatar.setImageResource(R.drawable.ic_monogram_background)
                cardHeader.monogramm.text = event.author.firstOrNull()?.uppercaseChar()?.toString()
                cardHeader.monogramm.visibility = View.VISIBLE
            }


            // Отображение вложений
            if (event.attachment != null) {
                when (event.attachment.type) {
                    AttachmentType.IMAGE -> {
                        cardAttachment.cardAttachment.visibility = View.VISIBLE
                        cardAttachment.imageAttachment.load(event.attachment.url)
                        cardAttachment.imageAttachment.visibility = View.VISIBLE
                        cardAttachment.play.visibility = View.GONE
                        cardContent.cardTitleSubtitleAudio.playAudio.visibility = View.GONE

                    }

                    AttachmentType.VIDEO -> {
                        cardAttachment.cardAttachment.visibility = View.VISIBLE
                        cardAttachment.imageAttachment.load(event.attachment.url)
                        cardAttachment.imageAttachment.visibility = View.VISIBLE
                        cardAttachment.play.visibility = View.VISIBLE
                        cardContent.cardTitleSubtitleAudio.playAudio.visibility = View.GONE
                    }

                    AttachmentType.AUDIO -> {
                        cardAttachment.cardAttachment.visibility = View.GONE
                        cardContent.cardTitleSubtitleAudio.playAudio.visibility = View.VISIBLE
                    }
                }
            } else {
                cardAttachment.cardAttachment.visibility = View.GONE
                cardContent.cardTitleSubtitleAudio.playAudio.visibility = View.GONE
            }

            cardContent.cardTitleSubtitleAudio.title.text =
                event.type.toString().lowercase().replaceFirstChar { it.uppercase() }

            val formattedDateTime = try {
                ZonedDateTime.parse(event.datetime, inputFormatter).format(outputFormatter)
            } catch (e: Exception) {
                event.datetime // Если формат некорректен, оставляем исходную строку
            }
            cardContent.cardTitleSubtitleAudio.subtitle.text = formattedDateTime

            cardContent.link.visibility =
                if (event.link.isNullOrEmpty()) View.GONE else View.VISIBLE
            cardContent.link.apply {
                if (event.link.isNullOrEmpty()) {
                    visibility = View.GONE
                } else {
                    visibility = View.VISIBLE
                    text = event.link
                    setOnClickListener {
                        onEventInteractionListener.onLink(event.link)
                    }
                }
            }

            cardSpeackers.cardUsersData.visibility = if (isDetailedView) View.VISIBLE else View.GONE
            cardSpeackers.title.text = context.getString(R.string.speackers)
            cardSpeackers.like.visibility = View.GONE
            cardSpeackers.users.visibility = View.GONE

            cardLikers.cardUsersData.visibility = if (isDetailedView) View.VISIBLE else View.GONE
            cardLikers.title.text = context.getString(R.string.likers)
            cardLikers.like.isChecked = event.likedByMe
            (event.likeOwnerIds?.size ?: 0).toString().also { cardLikers.like.text = it }
            cardLikers.like.setOnClickListener {
                onEventInteractionListener.onLike(event)
            }
            cardLikers.users.visibility = View.GONE

            cardMentioned.cardUsersData.visibility = if (isDetailedView) View.VISIBLE else View.GONE
            cardMentioned.title.text = context.getString(R.string.participants)
            cardMentioned.like.visibility = View.GONE
            (event.participantsIds?.size ?: 0).toString().also { cardMentioned.users.text = it }

            cardActions.cardActions.visibility = if (isDetailedView) View.GONE else View.VISIBLE
            cardActions.like.isChecked = event.likedByMe
            (event.likeOwnerIds?.size ?: 0).toString().also { cardActions.like.text = it }
            cardActions.like.setOnClickListener {
                onEventInteractionListener.onLike(event)
            }

            cardActions.share.setOnClickListener {
                onEventInteractionListener.onShare(event)
            }

            (event.participantsIds?.size ?: 0).toString().also { cardActions.users.text = it }

            mapContainer.visibility =
                if (isDetailedView && event.coords != null) View.VISIBLE else View.GONE
        }
    }
}

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}

interface OnEventInteractionListener {
    fun onLike(event: Event) {}
    fun onEdit(event: Event) {}
    fun onRemove(event: Event) {}
    fun onShare(event: Event) {}
    fun onEventDetails(event: Event) {}
    fun onLink(eventLink: String) {}
}