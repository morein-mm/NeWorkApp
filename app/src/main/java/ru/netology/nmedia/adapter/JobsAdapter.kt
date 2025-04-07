//package ru.netology.nmedia.adapter
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import androidx.recyclerview.widget.ListAdapter
//import android.view.ViewGroup
//import android.widget.PopupMenu
//import androidx.core.content.ContextCompat.getString
//import androidx.core.view.isVisible
//import androidx.core.view.marginStart
//import androidx.fragment.app.viewModels
//import androidx.paging.PagingDataAdapter
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import ru.netology.nmedia.R
//import ru.netology.nmedia.databinding.CardJobBinding
//import ru.netology.nmedia.databinding.CardPostBinding
//import ru.netology.nmedia.dto.Job
//import ru.netology.nmedia.dto.Post
//import ru.netology.nmedia.dto.User
//import ru.netology.nmedia.enumeration.AttachmentType
//import ru.netology.nmedia.enumeration.ViewType
//import ru.netology.nmedia.view.load
//import ru.netology.nmedia.view.loadCircleCrop
//import ru.netology.nmedia.viewmodel.PostViewModel
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
//import java.util.concurrent.PriorityBlockingQueue
//
//interface JobOnInteractionListener {
//    fun onRemove(job: Job) {}
//}
//
//class JobsAdapter(
//    private val onInteractionListener: JobOnInteractionListener,
//    private val context: Context,
//) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
//        val binding = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return JobViewHolder(binding, onInteractionListener, ViewType.FEED, context)
//    }
//
//    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
//        val job = getItem(position) ?: return
//        holder.bind(job)
//    }
//}
//
//class JobViewHolder(
//    private val binding: CardJobBinding,
//    private val onInteractionListener: JobOnInteractionListener,
////    TODO viewtype убрать
//    private val viewType: ViewType,
//    private val context: Context
//) : RecyclerView.ViewHolder(binding.root) {
//
//    fun bind(job: Job) {
//        binding.apply {
//            cardHeader.monogramBgAvatar.visibility = View.GONE
//            cardHeader.monogramm.visibility = View.GONE
////            TODO: в верстке отступ слева у хэдера
//            cardHeader.header.text = job.name
//            val finish = if (job.finish.isNullOrEmpty()) {
//                context.getString(R.string.CT)
//            } else {
//                getItemDateTime(job.finish)
//            }
//            cardHeader.subheader.text = getItemDateTime(job.start) + " – " + finish
//
//
////            показывать delete только, если смотрим джобы текущего авторизованного пользователя
//            cardHeader.delete.isVisible = job.ownedByMe
//            cardHeader.delete.setOnClickListener {
//                onInteractionListener.onRemove(job)
//            }
//            cardHeader.menu.visibility = View.GONE
//            cardContent.cardTitleSubtitleAudio.title.text = job.position
//            cardContent.cardTitleSubtitleAudio.subtitle.visibility = View.GONE
//            cardContent.content.visibility = View.GONE
//            if (job.link.isNullOrEmpty()) {
//                cardContent.link.visibility = View.GONE
//            } else {
//                cardContent.link.text = job.link
//                cardContent.link.visibility = View.VISIBLE
//            }
//        }
//    }
//
//    companion object {
//        val incomeDateTimePattern: DateTimeFormatter =
//            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
//        val outgoingDateTimePattern: DateTimeFormatter =
//            DateTimeFormatter.ofPattern("dd MMMM yyy")
//
//        fun getItemDateTime(itemDateTime: String): String = LocalDateTime
//            .parse(itemDateTime, incomeDateTimePattern)
//            .format(outgoingDateTimePattern)
//            .toString()
//
//    }
//}
//
//class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
//    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
//        return oldItem.id == newItem.id
//    }
//
//    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
//        return oldItem == newItem
//    }
//}
