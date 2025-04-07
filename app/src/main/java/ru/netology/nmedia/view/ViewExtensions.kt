package ru.netology.nmedia.view

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Shader
import android.util.TypedValue
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.util.Util
import java.nio.ByteBuffer
import java.security.MessageDigest

fun ImageView.load(url: String, vararg transforms: BitmapTransformation = emptyArray()) =
    Glide.with(this)
        .load(url)
        .transform(*transforms)
        .into(this)

fun ImageView.loadCircleCrop(url: String, vararg transforms: BitmapTransformation = emptyArray()) =
    load(url, CircleCrop(), *transforms)

fun ImageView.loadCircleCropWithBorder(
    url: String,
    vararg transforms: BitmapTransformation = emptyArray()
) =
    load(url, CircleCrop(), BorderTransformation(4f, Color.WHITE), *transforms)

class BorderTransformation(
    private val borderWidth: Float,
    private val borderColor: Int
) : BitmapTransformation() {


    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(("BorderTransformation:$borderWidth:$borderColor").toByteArray())
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val size = Math.min(toTransform.width, toTransform.height)
        val result = pool.get(size, size, Bitmap.Config.ARGB_8888)
            ?: Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(result)

        // Рисуем основное изображение (оно уже круглое из-за CircleCrop)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }
        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)

        // Рисуем белую границу
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = borderColor
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
        }
        canvas.drawCircle(radius, radius, radius - borderWidth / 2, borderPaint)

        return result
    }
}
