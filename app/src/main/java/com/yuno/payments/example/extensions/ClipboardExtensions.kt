package com.yuno.payments.example.extensions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

/** Copies text to the system clipboard and shows a confirmation toast. */
fun Context.copyToClipboard(label: String, text: String) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
}
