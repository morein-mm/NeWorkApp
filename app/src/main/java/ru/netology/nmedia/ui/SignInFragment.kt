package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentSignInBinding
import ru.netology.nmedia.viewmodel.AuthViewModel


@AndroidEntryPoint
class SignInFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )

        binding.loginButton.setOnClickListener {

            binding.login.error =
                if (binding.loginEdit.text.toString().trim().isEmpty()) {
                    getString(R.string.login) + getString(R.string.cannot_be_empty)
                } else {
                    null
                }

            binding.password.error =
                if (binding.passwordEdit.text.toString().isEmpty()) {
                    getString(R.string.password) + getString(R.string.cannot_be_empty)
                } else {
                    null
                }
            if (binding.login.error == null && binding.password.error == null) {
                viewModel.login(
                    binding.loginEdit.text.toString(),
                    binding.passwordEdit.text.toString()
                )
            }
        }

        binding.register.setOnClickListener {
            findNavController().navigate(R.id.action_global_signInFragment)
        }

        viewModel.auth.observe(viewLifecycleOwner) {
            if (viewModel.isAuthorized) findNavController().navigateUp()
        }

        return binding.root
    }
}