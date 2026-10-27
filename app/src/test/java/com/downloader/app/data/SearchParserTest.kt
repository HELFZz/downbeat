package com.downloader.app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchParserTest {

    @Test
    fun parsesFlatPlaylistEntries() {
        val raw = """
            {
              "id": "ytsearch15:test",
              "entries": [
                {
                  "id": "abc123",
                  "title": "Song One",
                  "channel": "Artist One",
                  "duration": 212.0,
                  "url": "https://www.youtube.com/watch?v=abc123",
                  "thumbnails": [
                    { "url": "https://img/small.jpg" },
                    { "url": "https://img/large.jpg" }
                  ]
                }
              ]
            }
        """.trimIndent()

        val tracks = SearchParser.parse(raw)

        assertEquals(1, tracks.size)
        val track = tracks.first()
        assertEquals("abc123", track.id)
        assertEquals("Song One", track.title)
        assertEquals("Artist One", track.artist)
        assertEquals(212L, track.durationSeconds)
        assertEquals("https://www.youtube.com/watch?v=abc123", track.webpageUrl)
        assertEquals("https://img/large.jpg", track.thumbnailUrl)
    }

    @Test
    fun buildsWatchUrlFromBareVideoId() {
        val raw = """{"entries":[{"id":"xyz","title":"T","url":"xyz"}]}"""

        val track = SearchParser.parse(raw).single()

        assertEquals("https://www.youtube.com/watch?v=xyz", track.webpageUrl)
    }

    @Test
    fun skipsEntriesWithoutTitle() {
        val raw = """{"entries":[{"id":"a","title":null},{"id":"b","title":"B"}]}"""

        val tracks = SearchParser.parse(raw)

        assertEquals(1, tracks.size)
        assertEquals("b", tracks.single().id)
    }

    @Test
    fun handlesStringDuration() {
        val raw = """{"entries":[{"id":"a","title":"A","duration":"95"}]}"""

        assertEquals(95L, SearchParser.parse(raw).single().durationSeconds)
    }

    @Test
    fun toleratesInvalidJsonAndMissingEntries() {
        assertTrue(SearchParser.parse("not json at all").isEmpty())
        assertTrue(SearchParser.parse("""{"id":"ytsearch15:x"}""").isEmpty())
        assertTrue(SearchParser.parse("""{"entries":null}""").isEmpty())
        assertTrue(SearchParser.parse("""{"entries":[]}""").isEmpty())
    }

    @Test
    fun fallsBackToUnknownArtist() {
        val raw = """{"entries":[{"id":"a","title":"A"}]}"""

        assertEquals("Unknown artist", SearchParser.parse(raw).single().artist)
    }
}
