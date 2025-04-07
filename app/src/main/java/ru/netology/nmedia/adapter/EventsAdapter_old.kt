//package ru.netology.nmedia.adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.PopupMenu
//import androidx.core.view.isVisible
//import androidx.paging.PagingDataAdapter
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import ru.netology.nmedia.R
//import ru.netology.nmedia.databinding.CardEventBinding
//import ru.netology.nmedia.dto.Event
//import ru.netology.nmedia.enumeration.AttachmentType
//import ru.netology.nmedia.enumeration.ViewType
//import ru.netology.nmedia.view.load
//import ru.netology.nmedia.view.loadCircleCrop
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//
//class EventsAdapter(
//    private val onInteractionListener: EventOnInteractionListener,
//    private val context: Context,
//) : PagingDataAdapter<Event, EventViewHolder>(EventDiffCallback()) {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
//        val binding = CardEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return EventViewHolder(binding, onInteractionListener, ViewType.FEED, context)
//    }
//
//    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
//        val event = getItem(position) ?: return
//        holder.bind(event)
//    }
//}
//
//class EventViewHolder(
//    private val binding: CardEventBinding,
//    private val onInteractionListener: EventOnInteractionListener,
//    private val viewType: ViewType,
//    private val context: Context
//) : RecyclerView.ViewHolder(binding.root) {
//
//    fun bind(event: Event) {
//        binding.apply {
//            if (event.authorAvatar != null) {
//                cardHeader.monogramBgAvatar.loadCircleCrop(event.authorAvatar)
//                cardHeader.monogramm.visibility = View.GONE
//            } else {
//                cardHeader.monogramBgAvatar.setImageResource(R.drawable.ic_monogram_background)
//                if (event.author != "") {
//                    cardHeader.monogramm.text = event.author.first().uppercaseChar().toString()
//                    cardHeader.monogramm.visibility = View.VISIBLE
//                } else {
//                    cardHeader.monogramm.visibility = View.GONE
//                }
//            }
//
//            cardHeader.header.text = event.author
//
//            cardHeader.delete.visibility = View.GONE
////            TODO: Разобраться с составом и работой меню
//            cardHeader.menu.isVisible = event.ownedByMe
//            cardHeader.menu.setOnClickListener {
//                PopupMenu(it.context, it).apply {
//                    inflate(R.menu.options_feeditem)
//
//                    setOnMenuItemClickListener { item ->
//                        when (item.itemId) {
//                            R.id.remove -> {
//                                onInteractionListener.onRemove(event)
//                                true
//                            }
//
//                            R.id.edit -> {
//                                onInteractionListener.onEdit(event)
//                                true
//                            }
//
////                            R.id.retryLoad -> {
////                                onInteractionListener.onRetryLoad(event)
////                                true
////                            }
//
//                            else -> false
//                        }
//                    }
//                }.show()
//            }
//
////            TODO: Что выводить, когда аудио?
//            when (event.attachment?.type) {
//                AttachmentType.IMAGE -> {
//                    cardAttachment.imageAttachment.visibility = View.VISIBLE
//                    cardAttachment.imageAttachment.load(event.attachment.url)
//                    cardAttachment.imageAttachment.setOnClickListener {
//                        onInteractionListener.onOpenImageAttachment(event.attachment.url)
//                    }
//                    cardAttachment.play.visibility = View.GONE
//                }
//
//                AttachmentType.VIDEO -> {
//                    cardAttachment.imageAttachment.visibility = View.VISIBLE
//                    cardAttachment.imageAttachment.load(event.attachment.url)
//                    cardAttachment.imageAttachment.setOnClickListener {
//                        onInteractionListener.onPlayVideoAttachment(event.attachment.url)
//                    }
//                    cardAttachment.play.visibility = View.VISIBLE
//                }
//
//                AttachmentType.AUDIO -> {
//                    cardAttachment.imageAttachment.visibility = View.GONE
//                    cardAttachment.play.visibility = View.GONE
//                }
//
//                null -> {
//                    cardAttachment.imageAttachment.visibility = View.GONE
//                    cardAttachment.play.visibility = View.GONE
//                }
//            }
//
//
//            cardContent.cardTitleSubtitleAudio.titleLayout.visibility = View.GONE
//            cardContent.cardTitleSubtitleAudio.title.visibility = View.GONE
//            cardContent.cardTitleSubtitleAudio.subtitle.visibility = View.GONE
//            cardContent.content.visibility = View.VISIBLE
//            cardContent.content.text = event.content
//            cardContent.link.visibility = View.GONE
//
//            when (viewType) {
//                ViewType.CARD -> {
//                    cardHeader.subheader.text = event.authorJob
//
//                    cardContent.cardTitleSubtitleAudio.titleLayout.visibility = View.VISIBLE
//                    cardContent.cardTitleSubtitleAudio.title.visibility = View.VISIBLE
//                    cardContent.cardTitleSubtitleAudio.title.text = getItemDateTime(event.published)
//                    cardContent.cardTitleSubtitleAudio.subtitle.visibility = View.GONE
//
//                    cardLikers.cardUsersData.visibility = View.VISIBLE
//                    cardActions.cardActions.visibility = View.GONE
//                    cardLikers.title.text = context.getString(R.string.likers)
//                    cardLikers.like.isChecked = event.likedByMe
//                    (event.likeOwnerIds?.size ?: 0).toString().also { cardLikers.like.text = it }
////                    TODO: Не обновляется количество лайков при снятии / установке
//                    cardLikers.like.setOnClickListener {
//                        onInteractionListener.onLike(event)
//                    }
//                    cardLikers.users.visibility = View.GONE
//                    println("SIZE")
//                    println(event.likeOwnerIds?.size)
//                    when (event.likeOwnerIds?.size) {
//                        in 1..Int.MAX_VALUE -> {
//                            cardLikers.cardUsersLine.cardUsersLine.visibility = View.VISIBLE
//                            event.likeOwnerIds?.forEachIndexed { index, likeOwner ->
//                                if (index in 1..5) {
////                                    TODO: заполнить аватар
////                                    if (post.authorAvatar != null) {
////                                        cardLikers.cardUsersLine.userAvatarCircle1.monogramBgAvatar.loadCircleCrop(
////                                            post.authorAvatar
////                                        )
////                                        cardLikers.cardUsersLine.userAvatarCircle1.monogramm.visibility =
////                                            View.GONE
////                                    } else {
////                                        cardLikers.cardUsersLine.userAvatarCircle1.monogramBgAvatar.setImageResource(
////                                            R.drawable.ic_monogram_background
////                                        )
////                                        if (post.author != "") {
////                                            cardLikers.cardUsersLine.userAvatarCircle1.monogramm.text =
////                                                post.author.first().uppercaseChar().toString()
////                                            cardLikers.cardUsersLine.userAvatarCircle1.monogramm.visibility =
////                                                View.VISIBLE
////                                        } else {
////                                            cardLikers.cardUsersLine.userAvatarCircle1.monogramm.visibility =
////                                                View.GONE
////                                        }
//                                    }
//                                if (index == 5) {
////
//                                }
//                            }
//                        }
//                        else -> cardLikers.cardUsersLine.cardUsersLine.visibility = View.GONE
//                    }
//                }
//
//                ViewType.FEED -> {
//
//                    cardHeader.subheader.text = getItemDateTime(event.published)
//
//                    cardContent.cardTitleSubtitleAudio.titleLayout.visibility = View.GONE
////                    cardContent.title.visibility = View.GONE
////                    cardContent.subtitle.visibility = View.GONE
//
//                    cardLikers.cardUsersData.visibility = View.GONE
//
//                    cardActions.cardActions.visibility = View.VISIBLE
//                    cardActions.like.isChecked = event.likedByMe
//                    (event.likeOwnerIds?.size ?: 0).toString().also { cardActions.like.text = it }
////                    cardActions.like.text = "${post.likes}"
//                    cardActions.like.setOnClickListener {
//                        onInteractionListener.onLike(event)
//                    }
//                }
//
//                ViewType.FEED_WITH_SELECT -> TODO()
//            }
//
//            cardActions.share.setOnClickListener {
//                onInteractionListener.onShare(event)
//            }
//
//            cardActions.users.visibility = View.GONE
//
//            eventCard.setOnClickListener {
////                onInteractionListener.onPostDetails(event)
//            }
//
//        }
//    }
//
//    companion object {
//        val incomeDateTimePattern: DateTimeFormatter =
//            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
//        val outgoingDateTimePattern: DateTimeFormatter =
//            DateTimeFormatter.ofPattern("dd.MM.yy' 'HH:mm")
//
//        fun getItemDateTime(itemDateTime: String): String = LocalDateTime
//            .parse(itemDateTime, incomeDateTimePattern)
//            .format(outgoingDateTimePattern)
//            .toString()
//
//    }
//
//
//}
//
//class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
//    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
//        return oldItem.id == newItem.id
//    }
//
//    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
//        return oldItem == newItem
//    }
//}
//
//interface EventOnInteractionListener {
//    fun onLike(event: Event) {}
//    fun onEdit(event: Event) {}
//    fun onRemove(event: Event) {}
//    fun onShare(event: Event) {}
//    fun onRetryLoad(event: Event) {}
//    fun onOpenImageAttachment(url: String)
//    fun onPlayVideoAttachment(url: String)
////    fun onPostDetails(event: Event)
//}
