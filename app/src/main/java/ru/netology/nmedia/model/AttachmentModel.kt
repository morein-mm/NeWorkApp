package ru.netology.nmedia.model

import android.net.Uri
import ru.netology.nmedia.enumeration.AttachmentType
import java.io.File

data class AttachmentModel(
    val uri: Uri? = null,
    val file: File? = null,
) {
    val isEmpty: Boolean
        get() = uri == null && file == null
}