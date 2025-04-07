//package ru.netology.nmedia.activity
//
//import android.app.Activity
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.Menu
//import android.view.MenuInflater
//import android.view.MenuItem
//import android.view.View
//import android.view.ViewGroup
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.net.toFile
//import androidx.core.view.MenuProvider
//import androidx.core.view.isVisible
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import com.github.dhaval2404.imagepicker.ImagePicker
//import com.google.android.material.snackbar.Snackbar
//import dagger.hilt.android.AndroidEntryPoint
//import ru.netology.nmedia.R
//import ru.netology.nmedia.databinding.FragmentNewJobBinding
//import ru.netology.nmedia.databinding.FragmentNewPostBinding
//import ru.netology.nmedia.util.AndroidUtils
//import ru.netology.nmedia.util.StringArg
//import ru.netology.nmedia.viewmodel.JobViewModel
//import ru.netology.nmedia.viewmodel.PostViewModel
//
//@AndroidEntryPoint
//class NewJobFragment : Fragment() {
//
//    private val viewModel: JobViewModel by viewModels()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val binding = FragmentNewJobBinding.inflate(
//            inflater,
//            container,
//            false
//        )
//
//        binding.createButton.setOnClickListener {
//            binding.name.error =
//                if (binding.nameEdit.text.toString().trim().isEmpty()) {
//                    getString(R.string.name) + getString(R.string.cannot_be_empty)
//                } else {
//                    null
//                }
//
//            binding.position.error =
//                if (binding.positionEdit.text.toString().trim().isEmpty()) {
//                    getString(R.string.position) + getString(R.string.cannot_be_empty)
//                } else {
//                    null
//                }
//
//            if (
//                binding.name.error == null &&
//                binding.position.error == null
//            ) {
//                viewModel.createJob(
//                    binding.nameEdit.text.toString().trim(),
//                    binding.positionEdit.text.toString().trim(),
//                    binding.linkEdit.text.toString().trim(),
//                )
//            }
//        }
//
//        viewModel.jobCreated.observe(viewLifecycleOwner) {
//            findNavController().navigateUp()
//        }
//
//        return binding.root
//    }
//}