package com.example.myfirstapplication.util

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

@Composable
private fun rememberImageBitmapFromUri(uri: Uri?): ImageBitmap? {
    val context = LocalContext.current
    return produceState<ImageBitmap?>(initialValue = null, key1 = uri) {
        value = uri?.let {
            try {
                val bitmap = if (Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                } else {
                    val src = ImageDecoder.createSource(context.contentResolver, it)
                    ImageDecoder.decodeBitmap(src)
                }
                bitmap.asImageBitmap()
            } catch (e: Exception) { null }
        }
    }.value
}