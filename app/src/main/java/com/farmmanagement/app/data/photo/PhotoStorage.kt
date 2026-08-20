package com.farmmanagement.app.data.photo

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

/**
 * All farm photos are written to app-private storage (context.filesDir/photos)
 * — never external/shared storage, never uploaded anywhere. See docs/SECURITY.md.
 */
class PhotoStorage(private val context: Context) {

    private val photosDir: File
        get() = File(context.filesDir, "photos").apply { if (!exists()) mkdirs() }

    /** Creates a destination file + content:// Uri for CameraX/TakePicture to write into. */
    fun createCaptureTarget(): Pair<File, Uri> {
        val file = File(photosDir, "farm_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        return file to uri
    }

    /** Copies a gallery/Photo-Picker selection into app-private storage so it persists. */
    fun copyIntoAppStorage(sourceUri: Uri): String? {
        return try {
            val destFile = File(photosDir, "farm_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                destFile.outputStream().use { output -> input.copyTo(output) }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
