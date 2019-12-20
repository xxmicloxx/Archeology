package de.mloy.archeology.helper

import android.content.Context
import android.util.Log
import java.io.FileNotFoundException
import java.io.IOException

fun write(context: Context, filename: String, data: String) {
    try {
        context.openFileOutput(filename, Context.MODE_PRIVATE).writer().use {
            it.write(data)
        }
    } catch (e: Exception) {
        Log.e("FileHelper", "Cannot write file", e)
    }
}

fun read(context: Context, filename: String): String? {
    try {
        context.openFileInput(filename).reader().use {
            return it.readText()
        }
    } catch (e: FileNotFoundException) {
        Log.e("FileHelper", "File not found", e)
    } catch (e: IOException) {
        Log.e("FileHelper", "Cannot read file", e)
    }

    return null
}

fun exists(context: Context, filename: String): Boolean {
    val file = context.getFileStreamPath(filename)
    return file.exists()
}