package ru.netology.nmedia.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import ru.netology.nmedia.R
import ru.netology.nmedia.dto.UserPreview
import ru.netology.nmedia.view.loadCircleCropWithBorder

object AvatarUtils {
    fun setupAvatar(
        userPreview: UserPreview,
        monogramBgAvatar: ImageView,
        monogramText: TextView
    ) {
        if (userPreview.avatar != null) {
            monogramBgAvatar.loadCircleCropWithBorder(userPreview.avatar)
            monogramBgAvatar.setBackgroundResource(0)
            monogramText.visibility = View.GONE
        } else {
            monogramBgAvatar.setImageResource(R.drawable.ic_monogram_background_with_border)
            if (userPreview.name.isNotEmpty()) {
                monogramText.text = userPreview.name.first().uppercaseChar().toString()
                monogramText.visibility = View.VISIBLE
            } else {
                monogramText.visibility = View.GONE
            }
        }
    }
}

