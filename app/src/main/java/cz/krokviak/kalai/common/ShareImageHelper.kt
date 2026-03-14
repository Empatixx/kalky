package cz.krokviak.kalai.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.FileProvider
import cz.krokviak.kalai.R
import java.io.File
import java.io.FileOutputStream

object ShareImageHelper {

    fun createShareImage(
        context: Context,
        imagePath: String,
        name: String,
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int
    ): Uri? {
        val original = BitmapFactory.decodeFile(imagePath) ?: return null
        val bitmap = original.copy(Bitmap.Config.ARGB_8888, true)
        original.recycle()

        val canvas = Canvas(bitmap)
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()

        // Scale relative to image width
        val scale = width / 1080f
        val nameSizePx = 22f * 3f * scale
        val macroSizePx = 16f * 3f * scale
        val iconSize = (macroSizePx * 1.3f).toInt()
        val padding = 24f * scale
        val iconTextGap = 4f * scale
        val macroGap = 20f * scale

        // Semi-transparent gradient overlay at bottom
        val gradientHeight = height * 0.30f
        val gradientPaint = Paint().apply {
            shader = LinearGradient(
                0f, height - gradientHeight,
                0f, height,
                Color.TRANSPARENT,
                Color.argb(200, 0, 0, 0),
                Shader.TileMode.CLAMP
            )
        }
        canvas.drawRect(0f, height - gradientHeight, width, height, gradientPaint)

        // Text paint for macro values
        val macroPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = macroSizePx
            typeface = Typeface.DEFAULT
        }

        // Macro row: icon + value for each macro
        val macroY = height - padding
        val iconTop = (macroY - iconSize + 4f * scale).toInt()
        var cursorX = padding

        // Calories (fire icon)
        drawIconAndText(
            context, canvas, R.drawable.ic_local_fire_department,
            "$calories kcal", macroPaint,
            cursorX, iconTop, iconSize, macroY, iconTextGap
        ).let { cursorX = it + macroGap }

        // Protein (chicken_leg icon)
        drawIconAndText(
            context, canvas, R.drawable.chicken_leg,
            "${protein}g", macroPaint,
            cursorX, iconTop, iconSize, macroY, iconTextGap
        ).let { cursorX = it + macroGap }

        // Carbs (wheat icon)
        drawIconAndText(
            context, canvas, R.drawable.wheat,
            "${carbs}g", macroPaint,
            cursorX, iconTop, iconSize, macroY, iconTextGap
        ).let { cursorX = it + macroGap }

        // Fat (avocado icon)
        drawIconAndText(
            context, canvas, R.drawable.avocado,
            "${fat}g", macroPaint,
            cursorX, iconTop, iconSize, macroY, iconTextGap
        )

        // Food name
        val namePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = nameSizePx
            typeface = Typeface.DEFAULT_BOLD
        }
        val nameY = macroY - iconSize - 12f * scale
        canvas.drawText(name, padding, nameY, namePaint)

        // Save to cache
        val shareDir = File(context.cacheDir, "share_images")
        shareDir.mkdirs()
        val shareFile = File(shareDir, "share_food.jpg")
        FileOutputStream(shareFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }
        bitmap.recycle()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            shareFile
        )
    }

    private fun drawIconAndText(
        context: Context,
        canvas: Canvas,
        drawableRes: Int,
        text: String,
        paint: Paint,
        x: Float,
        iconTop: Int,
        iconSize: Int,
        textY: Float,
        gap: Float
    ): Float {
        val drawable = AppCompatResources.getDrawable(context, drawableRes)
        if (drawable != null) {
            drawable.setBounds(x.toInt(), iconTop, x.toInt() + iconSize, iconTop + iconSize)
            drawable.setColorFilter(PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN))
            drawable.draw(canvas)
        }
        val textX = x + iconSize + gap
        canvas.drawText(text, textX, textY, paint)
        return textX + paint.measureText(text)
    }
}
