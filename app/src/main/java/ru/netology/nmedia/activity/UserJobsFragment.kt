package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.JobOnInteractionListener
import ru.netology.nmedia.adapter.JobsAdapter
import ru.netology.nmedia.databinding.FragmentFeedJobsBinding
import ru.netology.nmedia.dto.Job
import ru.netology.nmedia.viewmodel.AuthViewModel
import ru.netology.nmedia.viewmodel.JobViewModel

@AndroidEntryPoint
open class UserJobsFragment : Fragment() {

    private val viewModel: JobViewModel by activityViewModels()
    private val viewModelAuth: AuthViewModel by activityViewModels()
    private var userId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userId = requireArguments().getLong(ARG_USER_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentFeedJobsBinding.inflate(inflater, container, false)

        val adapter = JobsAdapter(
            object : JobOnInteractionListener {
                override fun onRemove(job: Job) {
//                    viewModel.removeById(job.id)
                }
            },
            requireContext()
        )
        binding.list.adapter = adapter

        viewModel.loadJobs(userId)
        viewModel.data.observe(viewLifecycleOwner) { model ->
            adapter.submitList(model.jobs) {
                binding.emptyText.isVisible = model.empty
            }
        }

        viewModelAuth.auth.observe(viewLifecycleOwner) {
            binding.fab.isVisible = viewModelAuth.isAuthorized
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(
                R.id.action_profileFragment_to_newJobFragment,
            )
        }

        return binding.root
    }

    companion object {
        private const val ARG_USER_ID = "user_id"
        fun newInstance(userId: Long) = UserJobsFragment().apply {
            arguments = Bundle().apply {
                putLong(ARG_USER_ID, userId)
            }
        }
    }
}
