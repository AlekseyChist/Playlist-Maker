// app/src/main/java/com/example/playlistmaker/media/data/storage/CoverStorageImpl.kt
package com.example.playlistmaker.media.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.media.domain.usecase.CoverStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class CoverStorageImpl(
    private val context: Context
) : CoverStorage {

    private val storageDir: File by lazy {
        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_covers").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    override suspend fun saveCover(uri: Uri): String = withContext(Dispatchers.IO) {
        val fileName = "${UUID.randomUUID()}.jpg"
        val file = File(storageDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bitmap = BitmapFactory.decodeStream(inputStream)
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            }
        }

        // Возвращаем относительный путь
        return@withContext "playlist_covers/$fileName"
    }

    override fun getCoverPath(fileName: String): String {
        return File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName).absolutePath
    }
}