package com.yuno.payments.example.extensions

import android.content.Context
import android.util.TypedValue

fun Int.dpToPixels(context: Context):Int = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics
).toInt()