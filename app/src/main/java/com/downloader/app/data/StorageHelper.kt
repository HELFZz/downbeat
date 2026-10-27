package com.downloader.app.data

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File

/**
 * Everything is downloaded into an app-private work folder first (no permissions needed),
 * then copied into the public Music/Downbeat collection through MediaStore.
 */
object StorageHelper {
    private const val ALBUM_FOLDER = "Downbeat"
    const val PUBLIC_FOLDER_LABEL = "Music/$ALBUM_FOLDER"

    fun workspace(context: Context, id: String): File {
        val base = context.getExternalFilesDir(null) ?: context.filesDir
        return File(base, "work/$id").apply { mkdirs() }
    }

    fun publish(context: Context, source: File): PublishedFile {
        val mime = mimeTypeFor(source.name)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            publishScoped(context, source, mime)
        } else {
            publishLegacy(context, source, mime)
        }
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
    private fun publishScoped(context: Context, source: File, mime: String): PublishedFile {
        val resolver = context.contentResolver
        val collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val values = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, source.name)
            put(MediaStore.Audio.Media.MIME_TYPE, mime)
            put(MediaStore.Audio.Media.RELATIVE_PATH, "${Environment.DIRECTORY_MUSIC}/$ALBUM_FOLDER")
            put(MediaStore.Audio.Media.IS_MUSIC, 1)
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }
        val uri = resolver.insert(collection, values) ?: return fallback(context, source)
        val written = runCatching {
            resolver.openOutputStream(uri)?.use { output ->
                source.inputStream().use { input -> input.copyTo(output) }
            } ?: error("Could not open the destination file")
        }
        if (written.isFailure) {
            resolver.delete(uri, null, null)
            return fallback(context, source)
        }
        values.clear()
        values.put(MediaStore.Audio.Media.IS_PENDING, 0)
        resolver.update(uri, values, null, null)
        return PublishedFile("$PUBLIC_FOLDER_LABEL/${source.name}", uri.toString())
    }

    @Suppress("DEPRECATION")
    private fun publishLegacy(context: Context, source: File, mime: String): PublishedFile {
        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), ALBUM_FOLDER)
        if (!dir.exists() && !dir.mkdirs()) return fallback(context, source)
        val destination = File(dir, source.name)
        return runCatching {
            source.copyTo(destination, overwrite = true)
            val values = ContentValues().apply {
                put(MediaStore.Audio.Media.DISPLAY_NAME, destination.name)
                put(MediaStore.Audio.Media.MIME_TYPE, mime)
                put(MediaStore.Audio.Media.IS_MUSIC, 1)
                put(MediaStore.Audio.Media.DATA, destination.absolutePath)
            }
            val uri = context.contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
            PublishedFile("$PUBLIC_FOLDER_LABEL/${destination.name}", uri?.toString())
        }.getOrElse { fallback(context, source) }
    }

    private fun fallback(context: Context, source: File): PublishedFile {
        val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC) ?: context.filesDir, ALBUM_FOLDER)
        runCatching {
            if (!dir.exists()) dir.mkdirs()
            source.copyTo(File(dir, source.name), overwrite = true)
        }
        return PublishedFile("${dir.absolutePath}/${source.name}", null)
    }

    private fun mimeTypeFor(name: String): String = when (name.substringAfterLast('.', "").lowercase()) {
        "mp3" -> "audio/mpeg"
        "m4a", "aac" -> "audio/mp4"
        "ogg", "opus" -> "audio/ogg"
        "wav" -> "audio/wav"
        "flac" -> "audio/flac"
        else -> "audio/mpeg"
    }
}
