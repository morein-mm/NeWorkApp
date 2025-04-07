package ru.netology.nmedia.util

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.FileUtils.copy
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    @RequiresApi(Build.VERSION_CODES.Q)
    fun fileFromContentUri(context: Context, contentUri: Uri): File {
        val fileExtension = getFileExtension(context, contentUri)
        val fileName = "temp_file" + if (fileExtension != null) ".$fileExtension" else ""
        val tempFile = File(context.cacheDir, fileName)
        tempFile.createNewFile()

        try {
            val oStream = FileOutputStream(tempFile)
            val inputStream = context.contentResolver.openInputStream(contentUri)

            inputStream?.let {
                copy(inputStream, oStream)
            }

            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return tempFile
    }

    private fun getFileExtension(context: Context, uri: Uri): String? {
        val fileType: String? = context.contentResolver.getType(uri)
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(fileType)
    }
}

