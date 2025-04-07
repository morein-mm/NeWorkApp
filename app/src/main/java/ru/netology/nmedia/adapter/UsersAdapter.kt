package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardUserBinding
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.enumeration.ViewType
import ru.netology.nmedia.view.load
import ru.netology.nmedia.view.loadCircleCrop
import java.nio.file.attribute.UserPrincipal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface UserOnInteractionListener {
    fun onViewUserProfile(user: User) {}
    fun onSelectUser(user: User) {}
    fun onUnSelectUser(user: User) {}
}

class UsersAdapter(
    private val listener: UserOnInteractionListener,
    private val isSelectionMode: Boolean,
    private val mentionIds: List<Long>
//    private val itemViewType: ViewType,
) : ListAdapter<User, UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding, listener, isSelectionMode)//, itemViewType)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position) ?: return
//        val isSelected = mentionIds.contains(user.id) // Проверяем, был ли пользователь выбран
        holder.bind(user)
//        holder.setCheckboxChecked(isSelected) // Устанавливаем состояние чекбокс
    }
}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val listener: UserOnInteractionListener,
    private val isSelectionMode: Boolean
//    private val viewType: ViewType,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {

        binding.apply {

//            when (viewType) {
//                ViewType.FEED_WITH_SELECT -> {
//                    binding.cardHeader.selectCheckbox
//                        .setOnCheckedChangeListener(null)
//                    binding.cardHeader.selectCheckbox.isChecked = user.isSelected
//                    binding.cardHeader.selectCheckbox.visibility = View.VISIBLE
//                    binding.cardHeader.selectCheckbox
//                        .setOnCheckedChangeListener { buttonView, isChecked ->
//                                if (isChecked) {
//                                    listener.onSelectUser(user)
//                                } else {
//                                    listener.onUnSelectUser(user)
//                                }
//                        }
//                }
//
//
//                else -> {
//                    binding.cardHeader.selectCheckbox.visibility = View.GONE
//
//                }
//            }

            if (user.avatar != null) {
                cardHeader.monogramBgAvatar.loadCircleCrop(user.avatar)
                cardHeader.monogramm.visibility = View.GONE
            } else {
                cardHeader.monogramBgAvatar.setImageResource(R.drawable.ic_monogram_background)
                if (user.name != "") {
                    cardHeader.monogramm.text = user.name.first().uppercaseChar().toString()
                    cardHeader.monogramm.visibility = View.VISIBLE
                } else {
                    cardHeader.monogramm.visibility = View.GONE
                }
            }

            cardHeader.header.text = user.name
            cardHeader.subheader.text = user.login

            cardHeader.delete.visibility = View.GONE

            cardHeader.selectCheckbox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
            cardHeader.selectCheckbox.isChecked = user.isSelected
            // Подписка на изменения состояния чекбокса
            cardHeader.selectCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    listener.onSelectUser(user)  // Вызов функции выбора
                } else {
                    listener.onUnSelectUser(user)  // Вызов функции отмены выбора
                }
            }


            cardHeader.menu.visibility = View.GONE

            userCard.setOnClickListener {
                listener.onViewUserProfile(user)
            }
        }
    }

    fun setCheckboxChecked(isChecked: Boolean) {
        binding.cardHeader.selectCheckbox.isChecked = isChecked
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}
