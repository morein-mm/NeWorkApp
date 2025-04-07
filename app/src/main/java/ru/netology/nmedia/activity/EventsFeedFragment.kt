//package ru.netology.nmedia.activity
//
//import android.app.AlertDialog
//import android.content.Intent
//import android.content.Intent.createChooser
//import android.net.Uri
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.core.view.isVisible
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.fragment.findNavController
//import androidx.paging.LoadState
//import androidx.paging.filter
//import androidx.paging.map
//import androidx.recyclerview.widget.RecyclerView
//import com.google.android.material.snackbar.Snackbar
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.flow.collectLatest
//import ru.netology.nmedia.R
//import ru.netology.nmedia.activity.ImageFragment.Companion.textArg
//import ru.netology.nmedia.ui.PostDetailsFragment.Companion.postId
//import ru.netology.nmedia.adapter.EventOnInteractionListener
//import ru.netology.nmedia.adapter.EventsAdapter
//import ru.netology.nmedia.adapter.PostsAdapter
//import ru.netology.nmedia.databinding.FragmentFeedBinding
//import ru.netology.nmedia.databinding.FragmentFeedEventsBinding
//import ru.netology.nmedia.dto.Event
//import ru.netology.nmedia.dto.Post
//import ru.netology.nmedia.viewmodel.AuthViewModel
//import ru.netology.nmedia.viewmodel.EventViewModel
//import ru.netology.nmedia.viewmodel.PostViewModel
//
//@AndroidEntryPoint
//open class EventsFeedFragment : Fragment() {
//
//    private val viewModel: EventViewModel by viewModels()
//    private val viewModelAuth: AuthViewModel by activityViewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//
//        val binding = FragmentFeedEventsBinding.inflate(inflater, container, false)
//
//        val adapter = EventsAdapter(object : EventOnInteractionListener {
//            override fun onEdit(event: Event) {
//                viewModel.edit(event)
//            }
//
//            override fun onLike(event: Event) {
//                if (viewModelAuth.isAuthorized) {
////                    viewModel.likeById(post)
//                } else {
//                    AlertDialog.Builder(context)
//                        .setMessage(getString(R.string.like_post_dialog))
//                        .setTitle(getString(R.string.like_post_dialog_header))
//                        .setPositiveButton(getString(R.string.sign_in)) { dialog, which ->
//                            findNavController().navigate(R.id.action_postsFeedFragment_to_signInFragment)
//                        }
//                        .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
//                        }
//                        .create()
//                        .show()
//                }
//
//            }
//
//            override fun onRemove(event: Event) {
////                viewModel.removeById(post.id)
//            }
//
//            override fun onShare(event: Event) {
//                val intent = Intent().apply {
//                    action = Intent.ACTION_SEND
//                    putExtra(Intent.EXTRA_TEXT, event.content)
//                    type = "text/plain"
//                }
//
//                val shareIntent =
//                    createChooser(intent, getString(R.string.chooser_share_post))
//                startActivity(shareIntent)
//            }
//
//            override fun onRetryLoad(event: Event) {
////                viewModel.edit(post)
////                viewModel.save()
//            }
//
//            override fun onOpenImageAttachment(url: String) {
//                findNavController().navigate(
//                    R.id.action_feedFragment_to_imageFragment,
//                    Bundle().apply {
//                        textArg = url
//                    })
//            }
//
//            override fun onPlayVideoAttachment(url: String) {
//                try {
//                    startActivity(
//                        createChooser(
//                            Intent(Intent.ACTION_VIEW)
//                                .setDataAndType(Uri.parse(url), "video/*"),
//                            getString(R.string.select_application)
//                        )
//                    )
//                } catch (e: Exception) {
//                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
//                }
//            }
//
////            override fun onPostDetails(post: Post) {
////                println("FEED_FRAGMENT - view post - $post.id")
//////                viewModel.getById(post.id)
////                findNavController().navigate(
////                    R.id.action_feedFragment_to_postDetailsFragment,
////                    Bundle().apply {
////                        postId = post.id
////                    }
////                )
////            }
//        },
//            requireContext())
//
//        binding.list.adapter = adapter
//        viewModel.dataState.observe(viewLifecycleOwner) { state ->
//            binding.progress.isVisible = state.loading
//            binding.swiperefresh.isRefreshing = state.refreshing
//            if (state.error) {
//                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
////                    .setAction(R.string.retry_loading) { viewModel.loadPosts() }
//                    .show()
//            }
//        }
//
//        lifecycleScope.launchWhenCreated {
//            viewModel.data.collectLatest {
//                adapter.submitData(it)
//            }
//        }
//
//        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                if (positionStart == 0) {
//                    binding.list.smoothScrollToPosition(0)
//                }
//            }
//        })
//
//        lifecycleScope.launchWhenCreated {
//            adapter.loadStateFlow.collectLatest {
//                binding.swiperefresh.isRefreshing = it.refresh is LoadState.Loading
//                        || it.append is LoadState.Loading
//                        || it.prepend is LoadState.Loading
//            }
//
//        }
//
//
//        binding.swiperefresh.setOnRefreshListener {
////            viewModel.refreshPosts()
//            adapter.refresh()
//        }
//
//
//        binding.fab.setOnClickListener {
//
//            val viewModel by viewModels<AuthViewModel>()
//            viewModel.auth.observe(viewLifecycleOwner) {
//                if (viewModel.isAuthorized) {
//                    findNavController().navigate(R.id.action_eventsFeedFragment_to_newEventFragment)
//                } else {
//                    AlertDialog.Builder(context)
//                        .setMessage(getString(R.string.create_post_dialog))
//                        .setTitle(getString(R.string.create_post_dialog_header))
//                        .setPositiveButton(getString(R.string.sign_in)) { dialog, which ->
////                            TODO
//                            findNavController().navigate(R.id.action_postsFeedFragment_to_signInFragment)
//                        }
//                        .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
//                        }
//                        .create()
//                        .show()
//                }
//            }
//        }
//
//        return binding.root
//    }
//}
