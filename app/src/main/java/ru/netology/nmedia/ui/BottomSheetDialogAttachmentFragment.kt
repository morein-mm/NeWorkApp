package ru.netology.nmedia.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.BottomSheetDialogAttachmentBinding
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.viewmodel.PostViewModel


@AndroidEntryPoint
class BottomSheetDialogAttachmentFragment : BottomSheetDialogFragment() {

    private val viewModel: PostViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = BottomSheetDialogAttachmentBinding.inflate(
            inflater,
            container,
            false
        )

        binding.attachVideo.setOnClickListener {
            (parentFragment as? PostNewEditFragment)?.let { parentFragment ->
                viewModel.changeAttachmentType(AttachmentType.VIDEO) // Устанавливаем тип перед открытием FilePicker
                parentFragment.openFilePicker("video/*") // Открываем FilePicker
            }
            dismiss()
        }

        binding.attachAudio.setOnClickListener {
            (parentFragment as? PostNewEditFragment)?.let { parentFragment ->
                viewModel.changeAttachmentType(AttachmentType.AUDIO) // Устанавливаем тип перед открытием FilePicker
                parentFragment.openFilePicker("audio/*") // Открываем FilePicker
            }
            dismiss()
        }

        return binding.root
    }

}