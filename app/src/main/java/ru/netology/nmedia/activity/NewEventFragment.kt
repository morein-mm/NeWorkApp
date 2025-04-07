package ru.netology.nmedia.activity//package ru.netology.nmedia.activity
//
//import android.app.Activity
//import android.app.AlertDialog
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.Menu
//import android.view.MenuInflater
//import android.view.MenuItem
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.net.toFile
//import androidx.core.view.MenuProvider
//import androidx.core.view.isVisible
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import com.bumptech.glide.Glide
//import com.github.dhaval2404.imagepicker.ImagePicker
//import com.google.android.material.snackbar.Snackbar
//import dagger.hilt.android.AndroidEntryPoint
//import ru.netology.nmedia.R
//import ru.netology.nmedia.activity.ImageFragment.Companion.textArg
//import ru.netology.nmedia.ui.UsersFeedFragment.Companion.viewType
//import ru.netology.nmedia.databinding.FragmentNewEventBinding
//import ru.netology.nmedia.databinding.FragmentNewPostBinding
//import ru.netology.nmedia.enumeration.AttachmentType
//import ru.netology.nmedia.enumeration.ViewType
//import ru.netology.nmedia.util.AndroidUtils
//import ru.netology.nmedia.util.StringArg
//import ru.netology.nmedia.view.load
//import ru.netology.nmedia.viewmodel.EventViewModel
//import ru.netology.nmedia.viewmodel.JobViewModel
//import ru.netology.nmedia.viewmodel.PostViewModel
//import java.io.File
//
//@AndroidEntryPoint
//class NewEventFragment : Fragment() {
//
//
//    companion object {
//        var Bundle.textArg: String? by StringArg
//
//        val MAX_ATTACHMENT_SIZE_MB = 15
//        val MAX_ATTACHMENT_SIZE_BYTES = MAX_ATTACHMENT_SIZE_MB * 1024 * 1024
//    }
//
//    private val viewModel: EventViewModel by activityViewModels()
//
//    private var _binding: FragmentNewEventBinding? = null
//    private val binding get() = _binding!!
//
//
//    private val filePickerLauncher =
//        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
//            if (uri != null) {
//                val fileSize = getFileSize(uri)
//                if (fileSize > MAX_ATTACHMENT_SIZE_BYTES) {
//                    Snackbar.make(requireView(), "Файл превышает 15 МБ", Snackbar.LENGTH_LONG)
//                        .show()
//                } else {
//                    viewModel.changeAttachment(uri, saveUriContentToFile(uri))
//                }
//            } else {
//                viewModel.dropAttachment() // Пользователь отменил выбор
//            }
//        }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
////        val binding = FragmentNewEventBinding.inflate(
////            inflater,
////            container,
////            false
////        )
//
//        _binding = FragmentNewEventBinding.inflate(
//            inflater,
//            container,
//            false
//        )
//
//        val pickPhotoLauncher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                when (it.resultCode) {
//                    ImagePicker.RESULT_ERROR -> {
//                        Snackbar.make(
//                            binding.root,
//                            ImagePicker.getError(it.data),
//                            Snackbar.LENGTH_SHORT
//                        ).show()
//                    }
//
//                    Activity.RESULT_OK -> {
//                        val uri = it.data?.data
//                        uri?.let {
//                            viewModel.changeAttachment(uri, uri.toFile())
//                        }
//                    }
//                }
//            }
//
//
//        if (viewModel.edited.value?.id != 0L) {
//            if (!viewModel.edited.value?.content.isNullOrEmpty()) {
//                binding.content.setText(viewModel.edited.value?.content.toString())
//            }
//        }
//
//        binding.content.requestFocus()
//
//
//        viewModel.attachment.observe(viewLifecycleOwner) { attachment ->
//            val attachmentType = viewModel.attachmentType.value
//
//            if (attachment != null && attachmentType != null) {
//                val uri = attachment.uri
//
//                when (attachmentType) {
//                    AttachmentType.IMAGE -> {
//                        // Отобразить изображение
//                        binding.mediaContainer.isVisible = true
//                        binding.imageAttachment.isVisible = true
//                        binding.cardTitleSubtitleAudio.playAudio.isVisible = false
//                        binding.play.isVisible = false
//
//                        // Загрузить изображение через Glide
//                        Glide.with(binding.imageAttachment)
//                            .load(uri)
//                            .centerInside()
//                            .into(binding.imageAttachment)
//                    }
//
//                    AttachmentType.VIDEO -> {
//                        // Отобразить изображение с кнопкой Play
//                        binding.mediaContainer.isVisible = true
//                        binding.imageAttachment.isVisible = true
//                        binding.cardTitleSubtitleAudio.playAudio.isVisible = false
//                        binding.play.isVisible = true
//
//                        // Загрузить изображение через Glide (обложка видео)
//                        Glide.with(binding.imageAttachment)
//                            .load(uri)
//                            .centerInside()
//                            .into(binding.imageAttachment)
//                    }
//
//                    AttachmentType.AUDIO -> {
//                        // Отобразить кнопку PlayAudio и скрыть изображение
//                        binding.mediaContainer.isVisible = false
//                        binding.imageAttachment.isVisible = false
//                        binding.cardTitleSubtitleAudio.playAudio.isVisible = true
//                    }
//                }
//
//                // Отображение кнопки удаления вложения
//                binding.removeAttachment.isVisible = true
//            } else {
//                // Скрыть все элементы, если вложение отсутствует
//                binding.mediaContainer.isVisible = false
//                binding.imageAttachment.isVisible = false
//                binding.play.isVisible = false
//                binding.cardTitleSubtitleAudio.playAudio.isVisible = false
//                binding.removeAttachment.isVisible = false
//            }
//        }
//
//        binding.removeAttachment.setOnClickListener {
//            viewModel.dropAttachment() // Сбрасываем вложение и его тип
//        }
//
//        viewModel.edited.observe(viewLifecycleOwner) { edited ->
//            binding.cardTitleSubtitleAudio.title.text = edited.type.name
////            TODO: Исправить формат вывода
//            binding.cardTitleSubtitleAudio.subtitle.text = edited.datetime
//        }
//
//        binding.pickPhoto.setOnClickListener {
//            if (!viewModel.attachment.value?.isEmpty!!) {
//                showReplaceAttachmentDialog {
//                    viewModel.changeAttachmentType(AttachmentType.IMAGE)
//                    ImagePicker.with(this)
//                        .crop()
//                        .compress(2048)
//                        .createIntent(pickPhotoLauncher::launch)
//                }
//            } else {
//                viewModel.changeAttachmentType(AttachmentType.IMAGE)
//                ImagePicker.with(this)
//                    .crop()
//                    .compress(2048)
//                    .createIntent(pickPhotoLauncher::launch)
//            }
//        }
//
//        binding.attachVideoAudio.setOnClickListener {
//            if (!viewModel.attachment.value?.isEmpty!!) {
//                showReplaceAttachmentDialog {
//                    BottomSheetDialogAttachmentFragment().show(
//                        childFragmentManager,
//                        "ModalBottomSheet"
//                    )
//                }
//            } else {
//                BottomSheetDialogAttachmentFragment().show(
//                    childFragmentManager,
//                    "ModalBottomSheet"
//                )
//            }
//        }
//
//        binding.selectLocation.setOnClickListener {
//            findNavController().navigate(
//                R.id.action_newEventFragment_to_mapsFragment,
//                Bundle().apply {
//                    viewModel.coords.value?.let {
//                        putParcelable("coords", it)
//                    }
//                }
//            )
//        }
//
//        binding.selectSpeakers.setOnClickListener {
//            findNavController().navigate(R.id.action_newEventFragment_to_users_feed,
//                Bundle().apply {
//                    viewType = ViewType.FEED_WITH_SELECT.toString()
//                    putLongArray(
//                        "userIds",
//                        viewModel.edited.value?.speakerIds?.map { it.toLong() }?.toLongArray()
//                    )
//                })
//        }
//
//        binding.selectDateEventType.setOnClickListener {
//            BottomSheetDialogFragment().show(
//                parentFragmentManager,
//                "ModalBottomSheet"
//            )
//        }
//
//        viewModel.eventSaveResult.observe(viewLifecycleOwner) { success ->
//            if (success) {
//                // Успешное сохранение: выполняем навигацию
//                findNavController().navigateUp()
//            } else {
//                // Ошибка сохранения: показываем сообщение
//                Snackbar.make(requireView(), "Произошла ошибка", Snackbar.LENGTH_SHORT).show()
//            }
//        }
//
//        return binding.root
//
//        //        viewModel.postCreated.observe(viewLifecycleOwner) {
////            findNavController().navigateUp()
////        }
//
//        //        binding.ok.setOnClickListener {
////            viewModel.changeContent(binding.edit.text.toString())
////            viewModel.save()
////            AndroidUtils.hideKeyboard(requireView())
////        }
//
//
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    fun getContent(): String? {
//        return _binding?.content?.text?.toString()?.trim()
//    }
//
//    fun saveEvent(content: String) {
////        println("saveEvent")
////        val content = binding.content.text.toString().trim()
////        println(content.isEmpty())
////        if (content.isEmpty()) {
////            println("inside")
////            Toast.makeText(requireContext(), "Content cannot be empty", Toast.LENGTH_SHORT)
////                .show()
////            return
////        }
//        println("Save event")
//        viewModel.changeContent(content)
//        println("Save event2")
//        viewModel.save()
////        findNavController().navigateUp()
//    }
//
//    // Метод для запуска FilePicker
//    fun openFilePicker(mimeType: String) {
//        println("Opening FilePicker for type: $mimeType")
//        filePickerLauncher.launch(arrayOf(mimeType))
//    }
//
//    fun saveUriContentToFile(uri: Uri): File {
//        val context = requireContext()
//        val tempFile = File.createTempFile("temp", null, context.cacheDir)
//        context.contentResolver.openInputStream(uri)?.use { inputStream ->
//            tempFile.outputStream().use { outputStream ->
//                inputStream.copyTo(outputStream)
//            }
//        }
//        return tempFile
//    }
//
//    private fun getFileSize(uri: Uri): Long {
//        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
//        return cursor?.use {
//            val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
//            it.moveToFirst()
//            if (sizeIndex != -1) it.getLong(sizeIndex) else 0L
//        } ?: 0L
//    }
//
//    fun showReplaceAttachmentDialog(onConfirm: () -> Unit) {
//        AlertDialog.Builder(requireContext())
//            .setTitle("Заменить вложение?")
//            .setMessage("Вы уже добавили вложение. Если вы продолжите, текущее вложение будет удалено. Продолжить?")
//            .setPositiveButton("Продолжить") { dialog, _ ->
//                onConfirm() // Выполнить действие
//                dialog.dismiss()
//            }
//            .setNegativeButton("Отмена") { dialog, _ ->
//                dialog.dismiss() // Закрыть диалог
//            }
//            .show()
//    }
//
//}