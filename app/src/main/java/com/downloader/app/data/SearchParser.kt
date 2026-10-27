package com.downloader.app.data

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Parses the `--dump-single-json` output of a `ytsearch:` query.
 * Kept separate from the repository so it can be unit tested on the JVM.
 */
object SearchParser {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    fun parse(raw: String): List<Track> {
        val root = runCatching { json.parseToJsonElement(raw).jsonObject }.getOrNull() ?: return emptyList()
        val entries = runCatching { root["entries"]?.jsonArray }.getOrNull() ?: return emptyList()
        return entries.mapNotNull { element ->
            val item = element as? JsonObject ?: return@mapNotNull null
            val id = item.string("id") ?: return@mapNotNull null
            val title = item.string("title") ?: return@mapNotNull null
            Track(
                id = id,
                title = title,
                artist = item.string("channel")
                    ?: item.string("uploader")
                    ?: item.string("channel_name")
                    ?: "Unknown artist",
                durationSeconds = item.string("duration")?.toDoubleOrNull()?.toLong() ?: 0L,
                webpageUrl = webpageUrl(item, id),
                thumbnailUrl = item.string("thumbnail")
                    ?: runCatching { item["thumbnails"]?.jsonArray?.lastOrNull()?.jsonObject?.string("url") }.getOrNull(),
            )
        }
    }

    private fun webpageUrl(item: JsonObject, id: String): String {
        item.string("webpage_url")?.let { return it }
        item.string("url")?.let { url ->
            return if (url.startsWith("http")) url else "https://www.youtube.com/watch?v=$url"
        }
        return "https://www.youtube.com/watch?v=$id"
    }

    private fun JsonObject.string(key: String): String? {
        val primitive = runCatching { this[key]?.jsonPrimitive }.getOrNull() ?: return null
        val content = primitive.content
        return content.takeIf { it.isNotBlank() && it != "null" }
    }
}
