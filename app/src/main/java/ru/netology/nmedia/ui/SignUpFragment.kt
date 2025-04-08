package ru.netology.nmedia.ui

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignUpBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.AuthViewModel

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    Activity.RESULT_OK -> {
                        val uri = it.data?.data
                        viewModel.changePhoto(uri, uri?.toFile())
                    }
                }
            }

        binding.avatar.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .createIntent(pickPhotoLauncher::launch)
        }

        binding.loginButton.setOnClickListener {
            binding.login.error =
                if (binding.loginEdit.text.toString().trim().isEmpty()) {
                    getString(R.string.login) + getString(R.string.cannot_be_empty)
                } else {
                    null
                }

            binding.name.error =
                if (binding.loginEdit.text.toString().trim().isEmpty()) {
                    getString(R.string.name) + getString(R.string.cannot_be_empty)
                } else {
                    null
                }

            binding.password.error =
                if (binding.passwordEdit.text.toString().isEmpty()) {
                    getString(R.string.password) + getString(R.string.cannot_be_empty)
                } else {
                    null
                }

            binding.passwordConfirm.error =
                if (binding.passwordEdit.text.toString() != binding.passwordConfirmEdit.text.toString()) {
                    getString(R.string.password_and_password_confirmation_must_match)
                } else {
                    null
                }

            if (
                binding.login.error == null &&
                binding.name.error == null &&
                binding.password.error == null &&
                binding.passwordConfirm.error == null
            ) {
                viewModel.registerUser(
                    binding.loginEdit.text.toString(),
                    binding.passwordEdit.text.toString(),
                    binding.nameEdit.text.toString(),
                )
            }
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            binding.photo.isVisible = it.uri != null
            binding.pickPhoto.isVisible = it.uri == null
            binding.photo.setImageURI(it.uri)
        }

        viewModel.auth.observe(viewLifecycleOwner) {
            if (viewModel.isAuthorized) findNavController().navigateUp()
        }

        return binding.root
    }
}