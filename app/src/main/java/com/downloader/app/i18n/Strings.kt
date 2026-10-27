package com.downloader.app.i18n

import androidx.compose.runtime.staticCompositionLocalOf
import com.downloader.app.data.AppLanguage
import com.downloader.app.data.AppTheme
import java.util.Locale

abstract class AppStrings {

    abstract val tabSearch: String
    abstract val tabDownloads: String
    abstract val tabSettings: String

    abstract val searchSubtitle: String
    abstract val searchPill: String
    abstract val pasteLink: String
    abstract val searchPlaceholder: String
    abstract val clearSearch: String
    abstract val searchAction: String
    abstract val results: String
    abstract val alreadyDownloaded: String
    abstract val listeningTitle: String
    abstract val listeningSubtitle: String
    abstract val welcomeTitle: String
    abstract val welcomeBody: String
    abstract val downloadFromLinkInstead: String
    abstract val noMatchesTitle: String
    abstract val noMatchesBody: String
    abstract val downloadFromLink: String
    abstract val suggestions: List<String>

    abstract val downloadsTitle: String
    abstract val downloadsSubtitle: String
    abstract val nothingSavedTitle: String
    abstract val nothingSavedBody: String
    abstract val findMusic: String
    abstract val downloadingAudio: String
    abstract val directDownloadFailed: String
    abstract val removeFromList: String
    abstract val downloading: String
    abstract val converting: String
    abstract val somethingWentWrong: String
    abstract val downloadLink: String
    abstract val retry: String
    abstract val openLink: String
    abstract val copyLink: String
    abstract val play: String
    abstract val defaultFolder: String

    abstract val settingsTitle: String
    abstract val settingsSubtitle: String
    abstract val appearance: String
    abstract val theme: String
    abstract val language: String
    abstract val languageSystem: String
    abstract val languageEnglish: String
    abstract val languageRussian: String
    abstract val audioFormat: String
    abstract val audioFormatValue: String
    abstract val saveLocation: String
    abstract val saveLocationValue: String
    abstract val engine: String
    abstract val updatingEngine: String
    abstract val updateEngine: String
    abstract val linkCardTitle: String
    abstract val linkCardBody: String
    abstract val about: String
    abstract val aboutBody: String

    abstract val linkDialogTitle: String
    abstract val linkDialogSubtitle: String
    abstract val linkDialogNote: String
    abstract val pasteFromClipboard: String
    abstract val download: String
    abstract val cancel: String
    abstract val alreadySaved: String
    abstract val linkCopied: String
    abstract val contentDownloadFromLink: String
    abstract val engineChecking: String
    abstract val linkedAudio: String

    abstract val msgTypeQuery: String
    abstract val msgNoTracks: String
    abstract val msgPasteFullLink: String
    abstract val msgManualFallback: String
    abstract val msgEngineUpdated: String
    abstract val msgEngineUpToDate: String
    abstract val msgEngineFinished: String
    abstract val searchFailedPrefix: String
    abstract val downloadFailedPrefix: String
    abstract val engineUpdateFailedPrefix: String

    open fun tracksCount(count: Int): String = "$count tracks"
    open fun savedCount(count: Int): String = "$count saved"
    open fun inProgressCount(count: Int): String = "$count in progress"
    open fun failedCount(count: Int): String = "$count failed"
    open fun downloadTrackDesc(title: String): String = "Download $title"
    open fun engineValue(version: String): String = "yt-dlp $version · youtubedl-android 0.18.1"
    open fun savedTo(path: String): String = "Saved to $path"

    abstract fun languageName(language: AppLanguage): String

    abstract fun themeName(theme: AppTheme): String
}

object EnStrings : AppStrings() {
    override val tabSearch = "Search"
    override val tabDownloads = "Downloads"
    override val tabSettings = "Settings"

    override val searchSubtitle = "Search a track, keep it as MP3"
    override val searchPill = "YouTube search · yt-dlp"
    override val pasteLink = "Paste a link"
    override val searchPlaceholder = "Song title or artist"
    override val clearSearch = "Clear search"
    override val searchAction = "Search"
    override val results = "Results"
    override val alreadyDownloaded = "Already downloaded"
    override val listeningTitle = "Listening for matches…"
    override val listeningSubtitle = "yt-dlp is searching YouTube"
    override val welcomeTitle = "Find your next repeat"
    override val welcomeBody = "Search a track by title or artist, or paste a link to save the audio."
    override val downloadFromLinkInstead = "Download from a link instead"
    override val noMatchesTitle = "No matches"
    override val noMatchesBody = "Try a different spelling, or paste a direct link."
    override val downloadFromLink = "Download from a link"
    override val suggestions = listOf("Lo-fi beats", "Daft Punk", "Tame Impala", "Nujabes", "Fred again..")

    override val downloadsTitle = "Downloads"
    override val downloadsSubtitle = "Saved as MP3 · Music/Downbeat"
    override val nothingSavedTitle = "Nothing saved yet"
    override val nothingSavedBody = "Search for a song and it will appear here."
    override val findMusic = "Find music"
    override val downloadingAudio = "Downloading audio…"
    override val directDownloadFailed = "Direct download failed"
    override val removeFromList = "Remove from list"
    override val downloading = "Downloading"
    override val converting = "Converting to MP3…"
    override val somethingWentWrong = "Something went wrong."
    override val downloadLink = "Download link"
    override val retry = "Retry"
    override val openLink = "Open link"
    override val copyLink = "Copy link"
    override val play = "Play"
    override val defaultFolder = "Music/Downbeat"

    override val settingsTitle = "Settings"
    override val settingsSubtitle = "Audio, storage and the download engine"
    override val appearance = "Appearance"
    override val theme = "Theme"
    override val language = "Language"
    override val languageSystem = "System"
    override val languageEnglish = "English"
    override val languageRussian = "Русский"
    override val audioFormat = "Audio format"
    override val audioFormatValue = "MP3 · best quality (V0), converted with ffmpeg"
    override val saveLocation = "Save location"
    override val saveLocationValue = "Music/Downbeat — visible in your music apps"
    override val engine = "Download engine"
    override val updatingEngine = "Updating the engine…"
    override val updateEngine = "Update yt-dlp engine"
    override val linkCardTitle = "Download from a link"
    override val linkCardBody = "YouTube and thousands of other yt-dlp supported sites"
    override val about = "About"
    override val aboutBody = "Downbeat saves audio tracks you are allowed to download. " +
        "Search results and metadata come from YouTube through yt-dlp; " +
        "the app itself stores nothing in the cloud."

    override val linkDialogTitle = "Download from a link"
    override val linkDialogSubtitle = "YouTube and other sites supported by yt-dlp"
    override val linkDialogNote = "The audio is downloaded and converted to MP3 in Music/Downbeat. " +
        "If the direct download fails, the source link stays available."
    override val pasteFromClipboard = "Paste from clipboard"
    override val download = "Download"
    override val cancel = "Cancel"
    override val alreadySaved = "Already saved in Music/Downbeat"
    override val linkCopied = "Download link copied"
    override val contentDownloadFromLink = "Download from link"
    override val engineChecking = "checking…"
    override val linkedAudio = "Linked audio"

    override val msgTypeQuery = "Type a song title or artist first."
    override val msgNoTracks = "No tracks found. Try a different search."
    override val msgPasteFullLink = "Paste a full http(s) link to download."
    override val msgManualFallback = "Couldn't download directly — use the source link instead."
    override val msgEngineUpdated = "Download engine updated."
    override val msgEngineUpToDate = "The download engine is already up to date."
    override val msgEngineFinished = "Engine update finished."
    override val searchFailedPrefix = "Search failed"
    override val downloadFailedPrefix = "Download failed"
    override val engineUpdateFailedPrefix = "Engine update failed"

    override fun themeName(theme: AppTheme): String = when (theme) {
        AppTheme.GRAPHITE -> "Graphite"
        AppTheme.SAND -> "Sand"
        AppTheme.OCEAN -> "Ocean"
    }

    override fun languageName(language: AppLanguage): String = when (language) {
        AppLanguage.SYSTEM -> languageSystem
        AppLanguage.ENGLISH -> languageEnglish
        AppLanguage.RUSSIAN -> languageRussian
    }
}

object RuStrings : AppStrings() {
    override val tabSearch = "Поиск"
    override val tabDownloads = "Загрузки"
    override val tabSettings = "Настройки"

    override val searchSubtitle = "Найди трек и сохрани его в MP3"
    override val searchPill = "Поиск YouTube · yt-dlp"
    override val pasteLink = "Вставить ссылку"
    override val searchPlaceholder = "Название трека или исполнитель"
    override val clearSearch = "Очистить поиск"
    override val searchAction = "Искать"
    override val results = "Результаты"
    override val alreadyDownloaded = "Уже скачано"
    override val listeningTitle = "Ищем совпадения…"
    override val listeningSubtitle = "yt-dlp ищет на YouTube"
    override val welcomeTitle = "Найди, что послушать снова"
    override val welcomeBody = "Ищи трек по названию или исполнителю либо вставь ссылку, чтобы сохранить аудио."
    override val downloadFromLinkInstead = "Скачать по ссылке"
    override val noMatchesTitle = "Ничего не найдено"
    override val noMatchesBody = "Попробуй другое написание или вставь прямую ссылку."
    override val downloadFromLink = "Скачать по ссылке"
    override val suggestions = listOf("Лоу-фай", "Nujabes", "Tame Impala", "Русский рок", "Fred again..")

    override val downloadsTitle = "Загрузки"
    override val downloadsSubtitle = "Сохранено в MP3 · Music/Downbeat"
    override val nothingSavedTitle = "Пока ничего нет"
    override val nothingSavedBody = "Найди песню — она появится здесь."
    override val findMusic = "Найти музыку"
    override val downloadingAudio = "Скачиваем аудио…"
    override val directDownloadFailed = "Не удалось скачать напрямую"
    override val removeFromList = "Убрать из списка"
    override val downloading = "Скачиваем"
    override val converting = "Конвертируем в MP3…"
    override val somethingWentWrong = "Что-то пошло не так."
    override val downloadLink = "Ссылка на скачивание"
    override val retry = "Повторить"
    override val openLink = "Открыть ссылку"
    override val copyLink = "Копировать ссылку"
    override val play = "Слушать"
    override val defaultFolder = "Music/Downbeat"

    override val settingsTitle = "Настройки"
    override val settingsSubtitle = "Звук, хранилище и движок загрузки"
    override val appearance = "Оформление"
    override val theme = "Тема"
    override val language = "Язык"
    override val languageSystem = "Как в системе"
    override val languageEnglish = "English"
    override val languageRussian = "Русский"
    override val audioFormat = "Формат аудио"
    override val audioFormatValue = "MP3 · лучшее качество (V0), конвертация через ffmpeg"
    override val saveLocation = "Куда сохранять"
    override val saveLocationValue = "Music/Downbeat — видно в музыкальных приложениях"
    override val engine = "Движок загрузки"
    override val updatingEngine = "Обновляем движок…"
    override val updateEngine = "Обновить движок yt-dlp"
    override val linkCardTitle = "Скачать по ссылке"
    override val linkCardBody = "YouTube и тысячи других сайтов, поддерживаемых yt-dlp"
    override val about = "О приложении"
    override val aboutBody = "Downbeat сохраняет аудио, которое тебе разрешено скачивать. " +
        "Результаты поиска и метаданные приходят с YouTube через yt-dlp; " +
        "приложение ничего не хранит в облаке."

    override val linkDialogTitle = "Скачать по ссылке"
    override val linkDialogSubtitle = "YouTube и другие сайты, поддерживаемые yt-dlp"
    override val linkDialogNote = "Аудио скачается и конвертируется в MP3 в Music/Downbeat. " +
        "Если прямая загрузка не удалась, ссылка на источник останется доступной."
    override val pasteFromClipboard = "Вставить из буфера обмена"
    override val download = "Скачать"
    override val cancel = "Отмена"
    override val alreadySaved = "Уже сохранено в Music/Downbeat"
    override val linkCopied = "Ссылка скопирована"
    override val contentDownloadFromLink = "Скачать по ссылке"
    override val engineChecking = "проверяем…"
    override val linkedAudio = "Аудио по ссылке"

    override val msgTypeQuery = "Сначала введи название песни или исполнителя."
    override val msgNoTracks = "Ничего не найдено. Попробуй другой запрос."
    override val msgPasteFullLink = "Вставь полную ссылку http(s)."
    override val msgManualFallback = "Не удалось скачать напрямую — используй ссылку на источник."
    override val msgEngineUpdated = "Движок загрузки обновлён."
    override val msgEngineUpToDate = "Движок уже обновлён."
    override val msgEngineFinished = "Обновление движка завершено."
    override val searchFailedPrefix = "Ошибка поиска"
    override val downloadFailedPrefix = "Ошибка загрузки"
    override val engineUpdateFailedPrefix = "Не удалось обновить движок"

    override fun tracksCount(count: Int): String = "$count ${plural(count, "трек", "трека", "треков")}"
    override fun savedCount(count: Int): String = "Сохранено: $count"
    override fun inProgressCount(count: Int): String = "В работе: $count"
    override fun failedCount(count: Int): String = "Ошибок: $count"
    override fun downloadTrackDesc(title: String) = "Скачать $title"
    override fun engineValue(version: String) = "yt-dlp $version · youtubedl-android 0.18.1"
    override fun savedTo(path: String) = "Сохранено в $path"

    override fun themeName(theme: AppTheme): String = when (theme) {
        AppTheme.GRAPHITE -> "Графит"
        AppTheme.SAND -> "Песок"
        AppTheme.OCEAN -> "Океан"
    }

    override fun languageName(language: AppLanguage): String = when (language) {
        AppLanguage.SYSTEM -> languageSystem
        AppLanguage.ENGLISH -> languageEnglish
        AppLanguage.RUSSIAN -> languageRussian
    }
}

private fun plural(count: Int, one: String, few: String, many: String): String {
    val mod10 = count % 10
    val mod100 = count % 100
    return when {
        mod10 == 1 && mod100 != 11 -> one
        mod10 in 2..4 && mod100 !in 12..14 -> few
        else -> many
    }
}

fun stringsFor(language: AppLanguage): AppStrings = when (language) {
    AppLanguage.SYSTEM -> if (Locale.getDefault().language == "ru") RuStrings else EnStrings
    AppLanguage.ENGLISH -> EnStrings
    AppLanguage.RUSSIAN -> RuStrings
}

val LocalStrings = staticCompositionLocalOf<AppStrings> { EnStrings }
