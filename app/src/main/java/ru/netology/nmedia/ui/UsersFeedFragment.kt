package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.UserOnInteractionListener
import ru.netology.nmedia.adapter.UsersAdapter
import ru.netology.nmedia.databinding.FragmentUsersFeedBinding
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.UserViewModel

@AndroidEntryPoint
open class UsersFeedFragment : Fragment() {

    companion object {
        var Bundle.viewType: String? by StringArg
    }

    private val viewModel: UserViewModel by activityViewModels()
//    private val viewModelEvent: EventViewModel by activityViewModels()

    private var isSelectionMode: Boolean = false
    private var mentionIds: List<Long> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSelectionMode = arguments?.getBoolean("isSelectionMode", false) ?: false
        mentionIds = arguments?.getLongArray("mentionIds")?.toList() ?: emptyList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentUsersFeedBinding.inflate(inflater, container, false)

        viewModel.setSelectedUsers(mentionIds)

//        val viewType =
//            try {
//                ViewType.entries.last { it.name == arguments?.viewType }
//            } catch (e: NoSuchElementException) {
//                ViewType.FEED
//            }
//
//        val userIds = arguments?.getLongArray("userIds")

//        if (viewType == ViewType.FEED_WITH_SELECT && userIds != null) {
//            userIds.forEach { id ->
//                viewModel.selectUser(id)
//            }
//        }

        val adapter = UsersAdapter(
            object : UserOnInteractionListener {
                override fun onViewUserProfile(user: User) {
                    findNavController().navigate(
                        R.id.action_usersFeedFragment_to_profileFragment,
                        Bundle().apply {
                            putLong("userId", user.id)
                        }
                    )
                }

                override fun onSelectUser(user: User) {
                    viewModel.selectUser(user.id)
                }

                override fun onUnSelectUser(user: User) {
                    viewModel.unSelectUser(user.id)
                }

            },
            isSelectionMode,
            mentionIds
//            viewType
        )

        binding.usersFeed.adapter = adapter

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.swiperefresh.isRefreshing = state.refreshing
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
//                    .setAction(R.string.retry_loading) { viewModel.loadUsers() }
                    .show()
            }

        }

        // Наблюдаем за списком пользователей
        viewModel.data.observe(viewLifecycleOwner) { model ->
            // Если режим выбора активен
            if (isSelectionMode) {
                // Наблюдаем за выбранными пользователями
                viewModel.selectedUsers.observe(viewLifecycleOwner) { selectedUsers ->
                    // Обновляем список пользователей с учетом выбранных
                    val updatedUsers = model.users.map { user ->
                        if (selectedUsers.contains(user.id)) {
                            user.copy(isSelected = true)
                        } else {
                            user.copy(isSelected = false)
                        }
                    }

                    // Передаем обновленный список в адаптер
                    adapter.submitList(updatedUsers) {
                        binding.emptyText.isVisible = model.empty
                    }
                }
            } else {
                // Если режим выбора не активен, просто отображаем всех пользователей
                adapter.submitList(model.users) {
                    binding.emptyText.isVisible = model.empty
                }
            }
        }


//        viewModel.data.observe(viewLifecycleOwner) { model ->
//            adapter.submitList(model.users) {
//                binding.emptyText.isVisible = model.empty
//            }
//        }

//        viewModel.data.observe(viewLifecycleOwner) { model ->
//            if (viewType == ViewType.FEED_WITH_SELECT) {
//                viewModel.selectedUsers.observe(viewLifecycleOwner) { selectedUsers ->
//                    val list: List<User> = model.users.map { user ->
//                        if (selectedUsers?.contains(user.id) == true) {
//                            user.copy(isSelected = true)
//                        } else {
//                            user
//                        }
//                    }
//                    adapter.submitList(list) {
//                        binding.emptyText.isVisible = model.empty
//                    }
//                }
//            } else {
//
//                val filteredUsers = userIds?.let { ids ->
//                    model.users.filter { it.id in ids }
//                } ?: model.users
//
//                adapter.submitList(filteredUsers) {
//                    binding.emptyText.isVisible = filteredUsers.isEmpty()
//                }
//
////                adapter.submitList(model.users) {
////                    binding.emptyText.isVisible = model.empty
////                }
//            }
//        }

        binding.swiperefresh.setOnRefreshListener {
            viewModel.loadUsers()
        }

        return binding.root
    }

    fun getSelectedUsers(): List<Long> {
        return viewModel.selectedUsers.value?.toList() ?: emptyList()
    }
}
