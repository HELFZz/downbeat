package com.downloader.app.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
private data class SavedDownload(
    val id: String,
    val title: String,
    val sourceUrl: String,
    val savedPath: String,
    val fileUri: String? = null,
    val createdAt: Long = 0L,
)

class DownloadHistory(context: Context) {
    private val prefs = context.getSharedPreferences("downbeat_history", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    fun load(): List<DownloadItem> = runCatching {
        val raw = prefs.getString(KEY, null) ?: return emptyList()
        json.decodeFromString<List<SavedDownload>>(raw).map { saved ->
            DownloadItem(
                id = saved.id,
                title = saved.title,
                sourceUrl = saved.sourceUrl,
                status = DownloadStatus.COMPLETED,
                progress = 1f,
                savedPath = saved.savedPath,
                fileUri = saved.fileUri,
                createdAt = saved.createdAt,
            )
        }
    }.getOrDefault(emptyList())

    fun save(items: List<DownloadItem>) {
        val completed = items
            .filter { it.status == DownloadStatus.COMPLETED && it.savedPath != null }
            .take(MAX_ITEMS)
            .map {
                SavedDownload(
                    id = it.id,
                    title = it.title,
                    sourceUrl = it.sourceUrl,
                    savedPath = it.savedPath.orEmpty(),
                    fileUri = it.fileUri,
                    createdAt = it.createdAt,
                )
            }
        runCatching { prefs.edit().putString(KEY, json.encodeToString(completed)).apply() }
    }

    private companion object {
        const val KEY = "items"
        const val MAX_ITEMS = 100
    }
}
