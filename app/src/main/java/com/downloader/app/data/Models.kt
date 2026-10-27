package com.downloader.app.data

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val durationSeconds: Long,
    val webpageUrl: String,
    val thumbnailUrl: String?,
)

enum class DownloadStatus { DOWNLOADING, COMPLETED, FAILED }

data class DownloadItem(
    val id: String,
    val title: String,
    val sourceUrl: String,
    val status: DownloadStatus,
    val progress: Float = 0f,
    val savedPath: String? = null,
    val fileUri: String? = null,
    val error: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

data class PublishedFile(
    val displayPath: String,
    val uri: String?,
)
