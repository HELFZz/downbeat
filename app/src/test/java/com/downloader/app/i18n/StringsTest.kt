package com.downloader.app.i18n

import com.downloader.app.data.AppLanguage
import com.downloader.app.data.AppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class StringsTest {

    @Test
    fun russianPluralForms() {
        assertEquals("1 трек", RuStrings.tracksCount(1))
        assertEquals("2 трека", RuStrings.tracksCount(2))
        assertEquals("4 трека", RuStrings.tracksCount(4))
        assertEquals("5 треков", RuStrings.tracksCount(5))
        assertEquals("11 треков", RuStrings.tracksCount(11))
        assertEquals("21 трек", RuStrings.tracksCount(21))
        assertEquals("22 трека", RuStrings.tracksCount(22))
    }

    @Test
    fun englishPluralForms() {
        assertEquals("1 tracks", EnStrings.tracksCount(1))
        assertEquals("12 tracks", EnStrings.tracksCount(12))
    }

    @Test
    fun explicitLanguageSelection() {
        assertSame(EnStrings, stringsFor(AppLanguage.ENGLISH))
        assertSame(RuStrings, stringsFor(AppLanguage.RUSSIAN))
    }

    @Test
    fun themeNamesAreLocalized() {
        assertEquals("Графит", RuStrings.themeName(AppTheme.GRAPHITE))
        assertEquals("Песок", RuStrings.themeName(AppTheme.SAND))
        assertEquals("Ocean", EnStrings.themeName(AppTheme.OCEAN))
    }

    @Test
    fun formattedMessagesUseArguments() {
        assertEquals("Saved to Music/Downbeat/a.mp3", EnStrings.savedTo("Music/Downbeat/a.mp3"))
        assertEquals("Сохранено в Music/Downbeat/a.mp3", RuStrings.savedTo("Music/Downbeat/a.mp3"))
    }
}
