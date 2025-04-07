package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentImageBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.view.load
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class ImageFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentImageBinding.inflate(
            inflater,
            container,
            false
        )
        if (arguments?.textArg != null) {
            binding.apply {
                image.visibility = View.GONE

//                TODO: "красивый" плейсхолдер, пока загрущка
                Glide.with(image)
                    .load(requireArguments().textArg)
//                    .placeholder(R.drawable.)
//                    .error(R.drawable.baseline_error_24)
                    .into(image)

                image.visibility = View.VISIBLE
            }
        }
//        TODO: было бы правильно позиционировать список на тот же объект, из которого открыли картинку
        binding.image.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }
}