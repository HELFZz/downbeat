package com.downloader.app.data

import android.content.Context
import com.downloader.app.MusicApplication
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

class DownloadRepository(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    suspend fun search(query: String): List<Track> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        awaitEngineReady()
        val request = YoutubeDLRequest("ytsearch15:$query").apply {
            addOption("--flat-playlist")
            addOption("--dump-single-json")
            addOption("--no-warnings")
        }
        val response = YoutubeDL.getInstance().execute(request)
        SearchParser.parse(response.out)
    }

    suspend fun downloadTrack(track: Track, onProgress: (Float) -> Unit): PublishedFile =
        downloadLink(track.webpageUrl, UUID.randomUUID().toString(), onProgress)

    suspend fun downloadLink(
        url: String,
        processId: String,
        onProgress: (Float) -> Unit,
    ): PublishedFile = withContext(Dispatchers.IO) {
        if (looksLikePlainAudio(url)) downloadDirectAudio(url, processId, onProgress)
        else downloadWithEngine(url, processId, onProgress)
    }

    fun cancel(processId: String) {
        runCatching { YoutubeDL.getInstance().destroyProcessById(processId) }
    }

    suspend fun engineVersion(): String = withContext(Dispatchers.IO) {
        awaitEngineReady()
        runCatching {
            val raw = runCatching { YoutubeDL.getInstance().version(context) }.getOrNull()
                ?: runCatching { YoutubeDL.getInstance().versionName(context) }.getOrNull()
                ?: "bundled"
            raw.removePrefix("yt-dlp").removePrefix("youtube-dl").trim().ifBlank { "bundled" }
        }.getOrElse { "bundled" }
    }

    suspend fun awaitEngineReady() {
        (context.applicationContext as? MusicApplication)?.awaitEngineReady()
    }

    private suspend fun downloadWithEngine(
        url: String,
        processId: String,
        onProgress: (Float) -> Unit,
    ): PublishedFile {
        awaitEngineReady()
        val dir = StorageHelper.workspace(context, processId)
        val template = File(dir, "%(title).200B.%(ext)s").absolutePath
        val request = YoutubeDLRequest(url).apply {
            addOption("-x")
            addOption("--audio-format", "mp3")
            addOption("--audio-quality", "0")
            addOption("--no-playlist")
            addOption("--no-mtime")
            addOption("--newline")
            addOption("--retries", 3)
            addOption("-o", template)
        }
        return try {
            YoutubeDL.getInstance().execute(
                request = request,
                processId = processId,
                callback = { percent, _, _ ->
                    onProgress((percent / 100f).coerceIn(0f, 0.99f))
                },
            )
            coroutineContext.ensureActive()
            val produced = dir.listFiles()
                ?.filter { it.isFile && it.length() > 0L }
                ?.maxByOrNull { it.lastModified() }
                ?: error("The engine finished but produced no audio file. Open the source link to download manually.")
            coroutineContext.ensureActive()
            onProgress(1f)
            StorageHelper.publish(context, produced)
        } finally {
            runCatching { dir.deleteRecursively() }
        }
    }

    private suspend fun downloadDirectAudio(
        url: String,
        processId: String,
        onProgress: (Float) -> Unit,
    ): PublishedFile {
        val dir = StorageHelper.workspace(context, processId)
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) error("The server returned HTTP ${response.code}.")
                val body = response.body ?: error("The server returned an empty response.")
                val fileName = url.substringBefore('?').substringAfterLast('/').ifBlank { "audio.mp3" }
                    .replace(Regex("[^A-Za-z0-9._ -]"), "_")
                val target = File(dir, if (fileName.contains('.')) fileName else "$fileName.mp3")
                val total = body.contentLength()
                body.byteStream().use { input ->
                    target.outputStream().use { output ->
                        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                        var copied = 0L
                        while (true) {
                            coroutineContext.ensureActive()
                            val read = input.read(buffer)
                            if (read < 0) break
                            output.write(buffer, 0, read)
                            copied += read
                            if (total > 0) onProgress((copied.toFloat() / total).coerceIn(0f, 0.99f))
                        }
                    }
                }
                coroutineContext.ensureActive()
                if (target.length() == 0L) error("The file was empty. Open the source link to download manually.")
                onProgress(1f)
                StorageHelper.publish(context, target)
            }
        } finally {
            runCatching { dir.deleteRecursively() }
        }
    }

    private fun looksLikePlainAudio(url: String): Boolean {
        val path = url.substringBefore('?').lowercase()
        if (path.endsWith(".m3u8")) return false
        return AUDIO_EXTENSIONS.any(path::endsWith)
    }

    private companion object {
        const val USER_AGENT = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0 Mobile Safari/537.36"
        val AUDIO_EXTENSIONS = listOf(".mp3", ".m4a", ".aac", ".ogg", ".opus", ".wav", ".flac")
    }
}
