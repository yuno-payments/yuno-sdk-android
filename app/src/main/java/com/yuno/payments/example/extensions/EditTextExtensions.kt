package com.yuno.payments.example.extensions

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.lang.reflect.Field

fun EditText.setCursorColor(context: Context, color: Int) {
    val editText = this
    val shapeDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        setSize(1.dpToPixels(context), 0)
        setColor(color)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        textCursorDrawable = shapeDrawable
    } else {
        try {
            TextView::class.java.getDeclaredField("mCursorDrawableRes").apply {
                isAccessible = true
                val drawableResId: Int = getInt(editText)

                val editorField: Field = TextView::class.java
                    .getDeclaredField("mEditor")
                editorField.isAccessible = true
                val editor: Any = editorField.get(editText)

                val drawable: Drawable? = ContextCompat
                    .getDrawable(editText.context, drawableResId)
                drawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)

                editor.javaClass.getDeclaredField("mCursorDrawable").apply {
                    isAccessible = true
                    set(editor, arrayOf(drawable, drawable))
                }
            }
        } catch (e: Exception) {
        }
    }
}