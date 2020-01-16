package de.mloy.archeology.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

fun readImageFromPath(context: Context, path: String): Bitmap? {
    val uri = Uri.parse(path) ?: return null
    try {
        val parcelFD = context.contentResolver.openFileDescriptor(uri, "r")
        val fd = parcelFD?.fileDescriptor
        val bitmap = BitmapFactory.decodeFileDescriptor(fd)
        parcelFD?.close()
        return bitmap
    } catch (e: Exception) {
    }

    return null
}