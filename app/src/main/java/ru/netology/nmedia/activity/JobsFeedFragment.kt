//package ru.netology.nmedia.activity
//
//import android.app.AlertDialog
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.view.isVisible
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import com.google.android.material.snackbar.Snackbar
//import dagger.hilt.android.AndroidEntryPoint
//import ru.netology.nmedia.R
//import ru.netology.nmedia.ui.PostDetailsFragment.Companion.postId
//import ru.netology.nmedia.activity.ProfileFragment.Companion.userId
//import ru.netology.nmedia.adapter.JobOnInteractionListener
//import ru.netology.nmedia.adapter.JobsAdapter
//import ru.netology.nmedia.adapter.UserOnInteractionListener
//import ru.netology.nmedia.adapter.UsersAdapter
//import ru.netology.nmedia.databinding.FragmentFeedJobsBinding
//import ru.netology.nmedia.databinding.FragmentUsersFeedBinding
//import ru.netology.nmedia.dto.Job
//import ru.netology.nmedia.dto.User
//import ru.netology.nmedia.enumeration.ViewType
//import ru.netology.nmedia.viewmodel.AuthViewModel
//import ru.netology.nmedia.viewmodel.JobViewModel
//import ru.netology.nmedia.viewmodel.UserViewModel
//
//@AndroidEntryPoint
//open class JobsFeedFragment : Fragment() {
//
//    private val viewModelJob: JobViewModel by activityViewModels()
//    private val viewModelAuth: AuthViewModel by activityViewModels()
//    private val viewModelUser: UserViewModel by activityViewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//
//        viewModelJob.loadJobs(viewModelJob.userId.value?.toLong() ?: -1)
//
//        val binding = FragmentFeedJobsBinding.inflate(inflater, container, false)
//
//        val adapter = JobsAdapter(
//            object : JobOnInteractionListener {
//                override fun onRemove(job: Job) {
//                    viewModelJob.removeById(job.id)
//                }
//            },
//            requireContext()
//        )
//        binding.list.adapter = adapter
//
//// TODO: разобраться с этими параметрами
//        println(parentFragment?.arguments?.getLong("userId"))
//        println(viewModelJob.userId.value)
//
//        val userId = parentFragment?.arguments?.getLong("userId")
//        viewModelJob.dataState.observe(viewLifecycleOwner) { state ->
//            binding.progress.isVisible = state.loading
//            binding.swiperefresh.isRefreshing = state.refreshing
//            if (state.error) {
////                TODO: откуда взять userid
//                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
//                    .setAction(R.string.retry_loading) { viewModelJob.loadJobs(300) }
//                    .show()
//            }
//        }
//
//        viewModelJob.data.observe(viewLifecycleOwner) { model ->
//            adapter.submitList(model.jobs) {
//                binding.emptyText.isVisible = model.empty
//            }
//        }
//
//        viewModelAuth.auth.observe(viewLifecycleOwner) {
//            binding.fab.isVisible = viewModelAuth.isAuthorized
//        }
//
//        binding.fab.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_profileFragment_to_newJobFragment,
//            )
//        }
//
//        return binding.root
//    }
//}
