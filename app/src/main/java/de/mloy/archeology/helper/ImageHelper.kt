package de.mloy.archeology.helper

import android.content.Context
import android.content.Intent
import de.mloy.archeology.R

fun createImagePickerIntent(ctx: Context): Intent {
    val intent = Intent().apply {
        type = "image/*"
        action = Intent.ACTION_OPEN_DOCUMENT
        addCategory(Intent.CATEGORY_OPENABLE)
    }
    return Intent.createChooser(intent, ctx.getString(R.string.site_image_select))
}