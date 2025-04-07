//package ru.netology.nmedia.adapter
//
//import android.content.Context
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.PopupMenu
//import android.widget.TextView
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.core.content.ContextCompat
//import androidx.core.content.ContextCompat.getString
//import androidx.core.view.isVisible
//import androidx.core.view.marginEnd
//import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
//import androidx.paging.PagingDataAdapter
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import ru.netology.nmedia.R
//import ru.netology.nmedia.databinding.CardPostBinding
//import ru.netology.nmedia.databinding.UserAvatarCircleBinding
//import ru.netology.nmedia.dto.Post
//import ru.netology.nmedia.enumeration.AttachmentType
//import ru.netology.nmedia.enumeration.ViewType
//import ru.netology.nmedia.util.AvatarUtils
//import ru.netology.nmedia.view.load
//import ru.netology.nmedia.view.loadCircleCrop
//import ru.netology.nmedia.view.loadCircleCropWithBorder
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//import java.util.concurrent.PriorityBlockingQueue
//
//class PostsAdapter(
//    private val onInteractionListener: OnInteractionListener,
//    private val context: Context,
//) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
//        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return PostViewHolder(binding, onInteractionListener, ViewType.FEED, context)
//    }
//
//    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
//        val post = getItem(position) ?: return
//        holder.bind(post)
//    }
//}
//
//class PostViewHolder(
//    private val binding: CardPostBinding,
//    private val onInteractionListener: OnInteractionListener,
//    private val viewType: ViewType,
//    private val context: Context
//) : RecyclerView.ViewHolder(binding.root) {
//
//    fun bind(post: Post) {
//        binding.apply {
//            if (post.authorAvatar != null) {
//                cardHeader.monogramBgAvatar.loadCircleCrop(post.authorAvatar)
//                cardHeader.monogramm.visibility = View.GONE
//            } else {
//                cardHeader.monogramBgAvatar.setImageResource(R.drawable.ic_monogram_background)
//                if (post.author != "") {
//                    cardHeader.monogramm.text = post.author.first().uppercaseChar().toString()
//                    cardHeader.monogramm.visibility = View.VISIBLE
//                } else {
//                    cardHeader.monogramm.visibility = View.GONE
//                }
//            }
//
//            cardHeader.header.text = post.author
//
//            binding.cardHeader.selectCheckbox.visibility = View.GONE
//
//            cardHeader.delete.visibility = View.GONE
////            TODO: Разобраться с составом и работой меню
//            cardHeader.menu.isVisible = post.ownedByMe
//            cardHeader.menu.setOnClickListener {
//                PopupMenu(it.context, it).apply {
//                    inflate(R.menu.options_post)
//
//                    if (post.draft) {
//                        menu.findItem(R.id.retryLoad).setVisible(true)
//                    } else {
//                        menu.findItem(R.id.retryLoad).setVisible(false)
//                    }
//                    setOnMenuItemClickListener { item ->
//                        when (item.itemId) {
//                            R.id.remove -> {
//                                onInteractionListener.onRemove(post)
//                                true
//                            }
//
//                            R.id.edit -> {
//                                onInteractionListener.onEdit(post)
//                                true
//                            }
//
//                            R.id.retryLoad -> {
//                                onInteractionListener.onRetryLoad(post)
//                                true
//                            }
//
//                            else -> false
//                        }
//                    }
//                }.show()
//            }
//
////            TODO: Что выводить, когда аудио?
//            when (post.attachment?.type) {
//                AttachmentType.IMAGE -> {
//                    cardAttachment.imageAttachment.visibility = View.VISIBLE
//                    cardAttachment.imageAttachment.load(post.attachment.url)
//                    cardAttachment.imageAttachment.setOnClickListener {
//                        onInteractionListener.onOpenImageAttachment(post.attachment.url)
//                    }
//                    cardAttachment.play.visibility = View.GONE
//                }
//
//                AttachmentType.VIDEO -> {
//                    cardAttachment.imageAttachment.visibility = View.VISIBLE
//                    cardAttachment.imageAttachment.load(post.attachment.url)
//                    cardAttachment.imageAttachment.setOnClickListener {
//                        onInteractionListener.onPlayVideoAttachment(post.attachment.url)
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
//            cardContent.content.text = post.content
//            cardContent.link.visibility = View.GONE
//
//            when (viewType) {
//                ViewType.CARD -> {
//                    cardHeader.subheader.text = post.authorJob
//
//                    cardContent.cardTitleSubtitleAudio.titleLayout.visibility = View.VISIBLE
//                    cardContent.cardTitleSubtitleAudio.title.visibility = View.VISIBLE
//                    cardContent.cardTitleSubtitleAudio.title.text = getItemDateTime(post.published)
//                    cardContent.cardTitleSubtitleAudio.subtitle.visibility = View.GONE
//
//                    cardLikers.cardUsersData.visibility = View.VISIBLE
//                    cardActions.cardActions.visibility = View.GONE
//                    cardLikers.title.text = context.getString(R.string.likers)
//                    cardLikers.like.isChecked = post.likedByMe
//                    (post.likeOwnerIds?.size ?: 0).toString().also { cardLikers.like.text = it }
////                    TODO: Не обновляется количество лайков при снятии / установке
//                    cardLikers.like.setOnClickListener {
//                        onInteractionListener.onLike(post)
//                    }
//
//                    cardLikers.users.visibility = View.GONE
//
//                    val likers = post.likeOwnerIds
//                        ?.mapNotNull { id -> post.users?.get(id.toLong()) } // Получаем пользователей, соответствующих likeOwnerIds
//                        ?.take(5) // Ограничиваем список до первых 5
//                        ?: emptyList()
//
//                    val likersContainer: LinearLayout = itemView.findViewById(R.id.card_users_line)
//                    likersContainer.removeAllViews()
//                    likers.forEach { user ->
//                        val bindingUserAvatar = UserAvatarCircleBinding.inflate(LayoutInflater.from(itemView.context), likersContainer, false)
//                        AvatarUtils.setupAvatar(user, bindingUserAvatar.monogramBgAvatar, bindingUserAvatar.monogramm)
//                        likersContainer.addView(bindingUserAvatar.root)
//                    }
//                    // TODO: Поменять на 5
//                    if (post.likeOwnerIds?.size!! > 2) {
//                        val moreView = LayoutInflater.from(itemView.context)
//                            .inflate(R.layout.user_avatar_circle, null) as ConstraintLayout
//                        moreView.findViewById<TextView>(R.id.monogramm).text = "+"
//                        moreView.findViewById<ImageView>(R.id.monogram_bg_avatar)
//                            .setImageResource(R.drawable.ic_monogram_background_with_border)
//                        moreView.setOnClickListener {
//                            onInteractionListener.onViewUsers(post)
//                        }
//                        likersContainer.addView(moreView)
//                    }
//                }
//
//                ViewType.FEED -> {
//
//                    cardHeader.subheader.text = getItemDateTime(post.published)
//
//                    cardContent.cardTitleSubtitleAudio.titleLayout.visibility = View.GONE
////                    cardContent.title.visibility = View.GONE
////                    cardContent.subtitle.visibility = View.GONE
//
//                    cardLikers.cardUsersData.visibility = View.GONE
//
//                    cardActions.cardActions.visibility = View.VISIBLE
//                    cardActions.like.isChecked = post.likedByMe
//                    (post.likeOwnerIds?.size ?: 0).toString().also { cardActions.like.text = it }
////                    cardActions.like.text = "${post.likes}"
//                    cardActions.like.setOnClickListener {
//                        onInteractionListener.onLike(post)
//                    }
//                }
//
//                ViewType.FEED_WITH_SELECT -> {
//                }
//
//            }
//
//            cardActions.share.setOnClickListener {
//                onInteractionListener.onShare(post)
//            }
//
//            cardActions.users.visibility = View.GONE
//
//            postCard.setOnClickListener {
//                onInteractionListener.onPostDetails(post)
//            }
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
//
//}
//
//class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
//    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
//        return oldItem.id == newItem.id
//    }
//
//    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
//        return oldItem == newItem
//    }
//}
//
//interface OnInteractionListener {
//    fun onLike(post: Post) {}
//    fun onEdit(post: Post) {}
//    fun onRemove(post: Post) {}
//    fun onShare(post: Post) {}
//    fun onRetryLoad(post: Post) {}
//    fun onOpenImageAttachment(url: String)
//    fun onPlayVideoAttachment(url: String)
//    fun onPostDetails(post: Post)
//    fun onViewUsers(post: Post)
//}
