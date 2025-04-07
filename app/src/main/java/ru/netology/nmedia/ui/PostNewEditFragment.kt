package ru.netology.nmedia.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.enumeration.AttachmentType
import ru.netology.nmedia.viewmodel.PostViewModel
import java.io.File

@AndroidEntryPoint
class PostNewEditFragment : Fragment() {

    companion object {
        val MAX_ATTACHMENT_SIZE_MB = 15
        val MAX_ATTACHMENT_SIZE_BYTES = MAX_ATTACHMENT_SIZE_MB * 1024 * 1024
    }

    private lateinit var binding: FragmentNewPostBinding
    private val viewModel: PostViewModel by activityViewModels()

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                val fileSize = getFileSize(uri)
                if (fileSize > MAX_ATTACHMENT_SIZE_BYTES) {
                    Snackbar.make(requireView(), "Файл превышает 15 МБ", Snackbar.LENGTH_LONG)
                        .show()
                } else {
                    viewModel.changeAttachment(uri, saveUriContentToFile(uri))
                }
            } else {
                viewModel.dropAttachment() // Пользователь отменил выбор
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        viewModel.edited.observe(viewLifecycleOwner) { post ->
            binding.content.setText(post?.content.orEmpty())
        }

        // Устанавливаем фокус в поле ввода
        binding.content.requestFocus()

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
                        uri?.let {
                            viewModel.changeAttachment(uri, uri.toFile())
                        }
                    }
                }
            }


        viewModel.attachment.observe(viewLifecycleOwner) { attachment ->
            println("Attachment changed: $attachment")
            val type = viewModel.attachmentType.value
            val hasAttachment = attachment?.isEmpty == false

            // Обновляем UI в зависимости от типа вложения
            binding.mediaContainer.isVisible = hasAttachment
            if (type == AttachmentType.IMAGE || type == AttachmentType.VIDEO) {
//                 Загрузить изображение через Glide
                if (attachment != null) {
                    Glide.with(binding.imageAttachment)
                        .load(attachment.uri)
                        .centerInside()
                        .into(binding.imageAttachment)
                }
            }
            binding.imageAttachment.isVisible =
                hasAttachment && (type == AttachmentType.IMAGE || type == AttachmentType.VIDEO)
            binding.play.isVisible = hasAttachment && type == AttachmentType.VIDEO
            binding.cardTitleSubtitleAudio.playAudio.isVisible =
                hasAttachment && type == AttachmentType.AUDIO
            binding.removeAttachment.isVisible = hasAttachment

            binding.pickPhoto.setOnClickListener {
                if (hasAttachment) {
                    showReplaceAttachmentDialog {
                        viewModel.changeAttachmentType(AttachmentType.IMAGE)
                        ImagePicker.with(this)
                            .crop()
                            .compress(2048)
                            .createIntent(pickPhotoLauncher::launch)
                    }
                } else {
                    viewModel.changeAttachmentType(AttachmentType.IMAGE)
                    ImagePicker.with(this)
                        .crop()
                        .compress(2048)
                        .createIntent(pickPhotoLauncher::launch)
                }
            }

            binding.attachVideoAudio.setOnClickListener {
                if (hasAttachment) {
                    showReplaceAttachmentDialog {
                        BottomSheetDialogAttachmentFragment().show(
                            childFragmentManager,
                            "ModalBottomSheet"
                        )
                    }
                } else {
                    BottomSheetDialogAttachmentFragment().show(
                        childFragmentManager,
                        "ModalBottomSheet"
                    )
                }
            }
        }

        binding.selectLocation.setOnClickListener {
            findNavController().navigate(
                R.id.action_newPostFragment_to_mapsFragment,
                Bundle().apply {
                    viewModel.edited.value?.coords.let {
                        putParcelable("coords", it)
                    }
                    putString("source", "post")
                }
            )
        }

        binding.selectUsers.setOnClickListener {
            findNavController().navigate(
                R.id.action_newPostFragment_to_users_feed,
                Bundle().apply {
                    putBoolean("isSelectionMode", true)
                    putLongArray("mentionIds", viewModel.edited.value?.mentionIds?.toLongArray())
                }
            )
        }

        binding.removeAttachment.setOnClickListener {
            viewModel.dropAttachment()
        }

        binding.selectDateEventType.visibility = View.GONE

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }

    private fun getFileSize(uri: Uri): Long {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
            it.moveToFirst()
            if (sizeIndex != -1) it.getLong(sizeIndex) else 0L
        } ?: 0L
    }

    private fun showReplaceAttachmentDialog(onConfirm: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.replace_attachment))
            .setMessage(getString(R.string.you_have_already_added_an_atatchment))
            .setPositiveButton(getString(R.string.continue_action)) { dialog, _ ->
                onConfirm() // Выполнить действие
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss() // Закрыть диалог
            }
            .show()
    }

    // Метод для запуска FilePicker
    fun openFilePicker(mimeType: String) {
        filePickerLauncher.launch(arrayOf(mimeType))
    }

    private fun saveUriContentToFile(uri: Uri): File {
        val context = requireContext()
        val tempFile = File.createTempFile("temp", null, context.cacheDir)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return tempFile
    }

    fun changeContent(): Boolean {
        val content = binding.content.text.toString().trim()
        if (content.isNotEmpty()) {
            viewModel.changeContent(content)
            return true
        } else {
            viewModel.setError(getString(R.string.content_can_not_be_empty))
            return false
        }
    }
}