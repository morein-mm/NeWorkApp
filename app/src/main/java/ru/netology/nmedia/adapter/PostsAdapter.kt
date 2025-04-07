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
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.view.load
import ru.netology.nmedia.view.loadCircleCrop
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class PostsAdapter(
    private val onInteractionListener: OnPostInteractionListener,
    private val isDetailedView: Boolean = false,
    private val context: Context,
) :
    PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener, isDetailedView, context)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnPostInteractionListener,
    private val isDetailedView: Boolean = false,
    private val context: Context,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {

            postCard.setOnClickListener {
                onInteractionListener.onPostDetails(post)
            }

            // Установка данных поста
            cardHeader.header.text = post.author

            val inputFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault())
            val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm", Locale.getDefault())

            val formattedDate = try {
                ZonedDateTime.parse(post.published, inputFormatter).format(outputFormatter)
            } catch (e: Exception) {
                post.published // Если формат некорректен, оставляем исходную строку
            }

            cardHeader.subheader.text = if (isDetailedView) {
                if (post.authorJob.isNullOrBlank()) {
                    context.getString(R.string.looking_for_a_job) // Строка из ресурсов, если нет данных
                } else {
                    post.authorJob
                }
            } else {
                formattedDate
            }

            // Скрыть чекбокс и кнопку удаления
            cardHeader.selectCheckbox.visibility = View.GONE
            cardHeader.delete.visibility = View.GONE

            // Показать или скрыть кнопку меню в зависимости от ownedByMe
            cardHeader.menu.visibility = if (post.ownedByMe) View.VISIBLE else View.GONE


            cardHeader.menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_feeditem)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }


            cardContent.content.text = post.content

            // Отображение аватара автора
            if (post.authorAvatar != null) {
                cardHeader.monogramBgAvatar.loadCircleCrop(post.authorAvatar)
                cardHeader.monogramm.visibility = View.GONE
            } else {
                cardHeader.monogramBgAvatar.setImageResource(R.drawable.ic_monogram_background)
                cardHeader.monogramm.text = post.author.firstOrNull()?.uppercaseChar()?.toString()
                cardHeader.monogramm.visibility = View.VISIBLE
            }


            // Отображение вложений
            if (post.attachment != null) {
                when (post.attachment.type) {
                    AttachmentType.IMAGE -> {
                        cardAttachment.cardAttachment.visibility = View.VISIBLE
                        cardAttachment.imageAttachment.load(post.attachment.url)
                        cardAttachment.imageAttachment.visibility = View.VISIBLE
                        cardAttachment.play.visibility = View.GONE
                        cardContent.cardTitleSubtitleAudio.playAudio.visibility = View.GONE

                    }

                    AttachmentType.VIDEO -> {
                        cardAttachment.cardAttachment.visibility = View.VISIBLE
                        cardAttachment.imageAttachment.load(post.attachment.url)
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

            if (isDetailedView) cardContent.cardTitleSubtitleAudio.subtitle.text = formattedDate

            cardContent.link.visibility = if (post.link.isNullOrEmpty()) View.GONE else View.VISIBLE
            cardContent.link.apply {
                if (post.link.isNullOrEmpty()) {
                    visibility = View.GONE
                } else {
                    visibility = View.VISIBLE
                    text = post.link
                    setOnClickListener {
                        onInteractionListener.onLink(post.link)
                    }
                }
            }

            cardLikers.cardUsersData.visibility = if (isDetailedView) View.VISIBLE else View.GONE
            cardLikers.title.text = context.getString(R.string.likers)
            cardLikers.like.isChecked = post.likedByMe
            (post.likeOwnerIds?.size ?: 0).toString().also { cardLikers.like.text = it }
            cardLikers.like.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            cardLikers.users.visibility = View.GONE

            cardMentioned.cardUsersData.visibility = if (isDetailedView) View.VISIBLE else View.GONE
            cardMentioned.title.text = context.getString(R.string.mentioned)
            cardMentioned.like.visibility = View.GONE
            (post.mentionIds?.size ?: 0).toString().also { cardMentioned.users.text = it }

            cardActions.cardActions.visibility = if (isDetailedView) View.GONE else View.VISIBLE
            cardActions.like.isChecked = post.likedByMe
            (post.likeOwnerIds?.size ?: 0).toString().also { cardActions.like.text = it }
            cardActions.like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            cardActions.share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            cardActions.users.visibility = View.GONE

            mapContainer.visibility =
                if (isDetailedView && post.coords != null) View.VISIBLE else View.GONE
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

interface OnPostInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onPostDetails(post: Post) {}
    fun onLink(postLink: String) {}
}