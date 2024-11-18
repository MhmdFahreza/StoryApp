package com.muhammadfahreza.storyapp.view

import android.content.Context
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"

fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()),
        ".jpg",
        storageDir
    )
}
