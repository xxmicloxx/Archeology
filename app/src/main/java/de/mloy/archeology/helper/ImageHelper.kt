package de.mloy.archeology.helper

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import de.mloy.archeology.R

fun createImagePickerIntent(ctx: Context): Intent {
    val intent = Intent().apply {
        type = "image/*"
        action = Intent.ACTION_OPEN_DOCUMENT
        addCategory(Intent.CATEGORY_OPENABLE)
    }
    return Intent.createChooser(intent, ctx.getString(R.string.site_image_select))
}

fun readImageFromPath(context: Context, path: String): Bitmap? {
    var bitmap: Bitmap? = null
    val uri = Uri.parse(path)
    if (uri != null) {
        try {
            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.fileDescriptor
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()
        } catch (e: Exception) {
        }
    }
    return bitmap
}