package com.downloader.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage(val tag: String) {
    SYSTEM("system"),
    ENGLISH("en"),
    RUSSIAN("ru"),
    ;

    companion object {
        fun fromTag(tag: String?): AppLanguage = entries.firstOrNull { it.tag == tag } ?: SYSTEM
    }
}

enum class AppTheme(val id: String) {
    GRAPHITE("graphite"),
    SAND("sand"),
    OCEAN("ocean"),
    ;

    companion object {
        fun fromId(id: String?): AppTheme = entries.firstOrNull { it.id == id } ?: GRAPHITE
    }
}

class AppSettings(context: Context) {

    private val prefs = context.getSharedPreferences("downbeat_settings", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(
        AppLanguage.fromTag(prefs.getString(KEY_LANGUAGE, null)),
    )
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _theme = MutableStateFlow(
        AppTheme.fromId(prefs.getString(KEY_THEME, null)),
    )
    val theme: StateFlow<AppTheme> = _theme.asStateFlow()

    fun setLanguage(value: AppLanguage) {
        _language.value = value
        prefs.edit().putString(KEY_LANGUAGE, value.tag).apply()
    }

    fun setTheme(value: AppTheme) {
        _theme.value = value
        prefs.edit().putString(KEY_THEME, value.id).apply()
    }

    private companion object {
        const val KEY_LANGUAGE = "language"
        const val KEY_THEME = "theme"
    }
}
