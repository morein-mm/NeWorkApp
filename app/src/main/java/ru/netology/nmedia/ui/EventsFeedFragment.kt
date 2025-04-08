package ru.netology.nmedia.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.EventsAdapter
import ru.netology.nmedia.adapter.OnEventInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.EventViewModel

@AndroidEntryPoint
class EventsFeedFragment : Fragment() {

    private val viewModel: EventViewModel by activityViewModels()
    private val viewModelAuth: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)

        // Настройка адаптера
        val adapter = EventsAdapter(
            object : OnEventInteractionListener {
                override fun onLike(event: Event) {
                    if (viewModelAuth.isAuthorized) {
                        viewModel.likeById(event)
                    } else {
                        context?.let {
                            AlertDialog.Builder(it)
                                .setMessage(getString(R.string.like_post_dialog))
                                .setTitle(getString(R.string.like_post_dialog_header))
                                .setPositiveButton(getString(R.string.sign_in)) { dialog, which ->
                                    findNavController().navigate(R.id.action_global_signInFragment)
                                }
                                .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                                }
                                .create()
                                .show()
                        }
                    }
                }

                override fun onEdit(event: Event) {
                    viewModel.edit(event)
                    findNavController().navigate(R.id.action_events_feed_to_eventNewEditFragment)
                }

                override fun onRemove(event: Event) {
                    viewModel.removeById(event.id)
                }

                override fun onShare(event: Event) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, event.content)
                        type = "text/plain"
                    }
                    val shareIntent =
                        Intent.createChooser(
                            intent,
                            getString(R.string.share)
                        )
                    startActivity(shareIntent)
                }

                override fun onEventDetails(event: Event) {
                    findNavController().navigate(
                        R.id.action_events_feed_to_eventDetailsFragment,
                        Bundle().apply {
                            putLong("eventId", event.id)
                        }
                    )
                }

                override fun onLink(eventLink: String) {
                    val fixedLink = if (eventLink.startsWith("http://") || eventLink.startsWith("https://")) {
                        eventLink
                    } else {
                        "https://$eventLink"
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fixedLink))
                    startActivity(intent)
                }
            },
            isDetailedView = false,
            requireContext(),
        )

        binding.list.adapter = adapter

        // Подписка на поток данных постов
        lifecycleScope.launchWhenCreated {
            viewModel.data.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }

        // Обработка состояния загрузки (показ/скрытие индикатора обновления)
        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.swiperefresh.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }

        // Настройка обновления списка при свайпе
        binding.swiperefresh.setOnRefreshListener {
            adapter.refresh()
        }

        // Скролл вверх при добавлении новых элементов
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.list.smoothScrollToPosition(0)
                }
            }
        })

        // Подписка на изменения состояния авторизации
        viewModelAuth.auth.observe(viewLifecycleOwner) {
            val isAuthorized = viewModelAuth.isAuthorized
            binding.fab.visibility = if (isAuthorized) View.VISIBLE else View.GONE
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_events_feed_to_eventNewEditFragment)
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
            }
        }

        return binding.root
    }
}