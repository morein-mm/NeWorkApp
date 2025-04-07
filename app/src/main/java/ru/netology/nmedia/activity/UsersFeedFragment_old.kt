//package ru.netology.nmedia.activity
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.isVisible
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.fragment.app.viewModels
//import androidx.navigation.findNavController
//import androidx.navigation.fragment.findNavController
//import com.google.android.material.snackbar.Snackbar
//import dagger.hilt.android.AndroidEntryPoint
//import ru.netology.nmedia.R
//import ru.netology.nmedia.ui.PostDetailsFragment.Companion.postId
//import ru.netology.nmedia.activity.ProfileFragment.Companion.userId
//import ru.netology.nmedia.adapter.UserOnInteractionListener
//import ru.netology.nmedia.adapter.UsersAdapter
//import ru.netology.nmedia.databinding.FragmentUsersFeedBinding
//import ru.netology.nmedia.dto.User
//import ru.netology.nmedia.enumeration.ViewType
//import ru.netology.nmedia.model.UsersFeedModel
//import ru.netology.nmedia.util.StringArg
//import ru.netology.nmedia.viewmodel.EventViewModel
//import ru.netology.nmedia.viewmodel.UserViewModel
//
//@AndroidEntryPoint
//open class UsersFeedFragment : Fragment() {
//
//    companion object {
//        var Bundle.viewType: String? by StringArg
//    }
//
//    private val viewModel: UserViewModel by activityViewModels()
//    private val viewModelEvent: EventViewModel by activityViewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//
//        val binding = FragmentUsersFeedBinding.inflate(inflater, container, false)
//
//        val viewType =
//            try {
//                ViewType.entries.last { it.name == arguments?.viewType }
//            } catch (e: NoSuchElementException) {
//                ViewType.FEED
//            }
//
//        val userIds = arguments?.getLongArray("userIds")
//
//        if (viewType == ViewType.FEED_WITH_SELECT && userIds != null) {
//            userIds.forEach { id ->
//                viewModel.selectUser(id)
//            }
//        }
//
//        val adapter = UsersAdapter(
//            object : UserOnInteractionListener {
//                override fun onViewUserProfile(user: User) {
//                    findNavController().navigate(
//                        R.id.action_usersFeedFragment_to_profileFragment,
//                        Bundle().apply {
//                            userId = user.id
//                        }
//                    )
//                }
//
//                override fun onSelectUser(user: User) {
//                    viewModel.selectUser(user.id)
//                }
//
//                override fun onUnSelectUser(user: User) {
//                    viewModel.unSelectUser(user.id)
//                }
//
//            },
//            viewType
//        )
//
//        binding.usersFeed.adapter = adapter
//
//        viewModel.dataState.observe(viewLifecycleOwner) { state ->
//            binding.progress.isVisible = state.loading
//            binding.swiperefresh.isRefreshing = state.refreshing
//            if (state.error) {
//                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
//                    .setAction(R.string.retry_loading) { viewModel.loadUsers() }
//                    .show()
//            }
//        }
//
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
//
//        binding.swiperefresh.setOnRefreshListener {
//            viewModel.loadUsers()
//        }
//
//        return binding.root
//    }
//}
