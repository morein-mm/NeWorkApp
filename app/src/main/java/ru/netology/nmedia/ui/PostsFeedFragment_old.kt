//package ru.netology.nmedia.ui
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
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.fragment.findNavController
//import androidx.paging.LoadState
//import androidx.recyclerview.widget.RecyclerView
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.flow.collectLatest
//import ru.netology.nmedia.R
//import ru.netology.nmedia.activity.ImageFragment.Companion.textArg
//import ru.netology.nmedia.ui.PostDetailsFragment.Companion.postId
//import ru.netology.nmedia.adapter.OnInteractionListener
//import ru.netology.nmedia.adapter.PostsAdapter
//import ru.netology.nmedia.auth.AppAuth
//import ru.netology.nmedia.databinding.FragmentFeedBinding
//import ru.netology.nmedia.dto.Post
//import ru.netology.nmedia.viewmodel.AuthViewModel
//import ru.netology.nmedia.viewmodel.PostViewModel
//import javax.inject.Inject
//
//@AndroidEntryPoint
//open class PostsFeedFragment_old : Fragment() {
//
//    @Inject
//    lateinit var appAuth: AppAuth
//
//    private val viewModel: PostViewModel by activityViewModels()
//    private val viewModelAuth: AuthViewModel by activityViewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//
//        val binding = FragmentFeedBinding.inflate(inflater, container, false)
//
//        val userId = arguments?.getLong("userId") ?: 0
//
//        // Установите userId в ViewModel
//        viewModel.setUserId(userId)
//
//        val adapter = PostsAdapter(
//            object : OnInteractionListener {
//                override fun onEdit(post: Post) {
//                    viewModel.edit(post)
//                }
//
//                override fun onLike(post: Post) {
//                    if (viewModelAuth.isAuthorized) {
//                        viewModel.likeById(post)
//                    } else {
//                        AlertDialog.Builder(context)
//                            .setMessage(getString(R.string.like_post_dialog))
//                            .setTitle(getString(R.string.like_post_dialog_header))
//                            .setPositiveButton(getString(R.string.sign_in)) { dialog, which ->
//                                findNavController().navigate(R.id.action_postsFeedFragment_to_signInFragment)
//                            }
//                            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
//                            }
//                            .create()
//                            .show()
//                    }
//
//                }
//
//                override fun onRemove(post: Post) {
//                    viewModel.removeById(post.id)
//                }
//
//                override fun onShare(post: Post) {
//                    val intent = Intent().apply {
//                        action = Intent.ACTION_SEND
//                        putExtra(Intent.EXTRA_TEXT, post.content)
//                        type = "text/plain"
//                    }
//
//                    val shareIntent =
//                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
//                    startActivity(shareIntent)
//                }
//
//                override fun onRetryLoad(post: Post) {
//                    viewModel.edit(post)
//                    viewModel.save()
//                }
//
//                override fun onOpenImageAttachment(url: String) {
//                    findNavController().navigate(
//                        R.id.action_feedFragment_to_imageFragment,
//                        Bundle().apply {
//                            textArg = url
//                        })
//                }
//
//                override fun onPlayVideoAttachment(url: String) {
//                    try {
//                        startActivity(
//                            createChooser(
//                                Intent(Intent.ACTION_VIEW)
//                                    .setDataAndType(Uri.parse(url), "video/*"),
//                                getString(R.string.select_application)
//                            )
//                        )
//                    } catch (e: Exception) {
//                        Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onPostDetails(post: Post) {
//                    println("FEED_FRAGMENT - view post - $post.id")
////                viewModel.getById(post.id)
//                    findNavController().navigate(
//                        R.id.action_feedFragment_to_postDetailsFragment,
//                        Bundle().apply {
//                            postId = post.id
//                        }
//                    )
//                }
//
//                override fun onViewUsers(post: Post) {
//
//
//                }
//            },
//            requireContext()
//        )
//
//        binding.list.adapter = adapter
//
//        lifecycleScope.launchWhenCreated {
//            viewModel.data.collectLatest {
//                adapter.submitData(it)
//            }
//        }
//
////        Показывает индикатор обновления, если при прокрутке списка
////        pager загружает какие-то данные (refresh, append, prepend)
//        lifecycleScope.launchWhenCreated {
//            adapter.loadStateFlow.collectLatest {
//                binding.swiperefresh.isRefreshing = it.refresh is LoadState.Loading
//                        || it.append is LoadState.Loading
//                        || it.prepend is LoadState.Loading
//            }
//        }
//
//        binding.swiperefresh.setOnRefreshListener {
//            adapter.refresh()
//        }
//
////        TODO: Нужна ли эта часть - проверить, когда сделаю создание поста
//        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
//                if (positionStart == 0) {
//                    binding.list.smoothScrollToPosition(0)
//                }
//            }
//        })
//
//        binding.fab.setOnClickListener {
//
//            val viewModel by viewModels<AuthViewModel>()
//            viewModel.auth.observe(viewLifecycleOwner) {
//                if (viewModel.isAuthorized) {
//                    findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
//                } else {
//                    AlertDialog.Builder(context)
//                        .setMessage(getString(R.string.create_post_dialog))
//                        .setTitle(getString(R.string.create_post_dialog_header))
//                        .setPositiveButton(getString(R.string.sign_in)) { dialog, which ->
//                            findNavController().navigate(R.id.action_postsFeedFragment_to_signInFragment)
//                        }
//                        .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
//                        }
//                        .create()
//                        .show()
//                }
//            }
//        }
//        return binding.root
//    }
//}