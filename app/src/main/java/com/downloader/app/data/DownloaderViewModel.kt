package com.downloader.app.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.downloader.app.i18n.AppStrings
import com.downloader.app.i18n.stringsFor
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class DownloaderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DownloadRepository(application)
    private val history = DownloadHistory(application)
    private val settings = AppSettings(application)

    val language: StateFlow<AppLanguage> = settings.language
    val theme: StateFlow<AppTheme> = settings.theme

    private val strings: AppStrings get() = stringsFor(language.value)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<Track>>(emptyList())
    val results: StateFlow<List<Track>> = _results.asStateFlow()

    private val _downloads = MutableStateFlow<List<DownloadItem>>(emptyList())
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    private val _searching = MutableStateFlow(false)
    val searching: StateFlow<Boolean> = _searching.asStateFlow()

    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _engineVersion = MutableStateFlow(strings.engineChecking)
    val engineVersion: StateFlow<String> = _engineVersion.asStateFlow()

    private val _updatingEngine = MutableStateFlow(false)
    val updatingEngine: StateFlow<Boolean> = _updatingEngine.asStateFlow()

    private var searchJob: Job? = null
    private val downloadJobs = mutableMapOf<String, Job>()

    init {
        _downloads.value = history.load()
        viewModelScope.launch {
            _engineVersion.value = repository.engineVersion()
        }
    }

    fun setQuery(value: String) {
        _query.value = value
    }

    fun setLanguage(value: AppLanguage) {
        settings.setLanguage(value)
        _engineVersion.value = strings.engineChecking
        viewModelScope.launch {
            _engineVersion.value = repository.engineVersion()
        }
    }

    fun setTheme(value: AppTheme) {
        settings.setTheme(value)
    }

    fun search() {
        val term = _query.value.trim()
        if (term.isBlank()) {
            _message.value = strings.msgTypeQuery
            return
        }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _searching.value = true
            _message.value = null
            runCatching { repository.search(term) }
                .onSuccess { tracks ->
                    _results.value = tracks
                    _hasSearched.value = true
                    if (tracks.isEmpty()) _message.value = strings.msgNoTracks
                }
                .onFailure { error ->
                    _hasSearched.value = true
                    _results.value = emptyList()
                    _message.value = error.friendlyMessage(strings.searchFailedPrefix)
                }
            _searching.value = false
        }
    }

    fun clearResults() {
        _results.value = emptyList()
        _hasSearched.value = false
    }

    fun download(track: Track) {
        startDownload(track.webpageUrl, track.title)
    }

    fun downloadLink(rawUrl: String) {
        val url = rawUrl.trim()
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            _message.value = strings.msgPasteFullLink
            return
        }
        startDownload(url, titleFromUrl(url))
    }

    fun retry(item: DownloadItem) {
        cancelDownload(item.id)
        startDownload(item.sourceUrl, item.title)
    }

    fun remove(id: String) {
        cancelDownload(id)
        _downloads.value = _downloads.value.filterNot { it.id == id }
        persist()
    }

    private fun cancelDownload(id: String) {
        downloadJobs.remove(id)?.cancel()
        repository.cancel(id)
    }

    fun clearMessage() {
        _message.value = null
    }

    fun say(text: String) {
        _message.value = text
    }

    fun updateEngine() {
        if (_updatingEngine.value) return
        viewModelScope.launch {
            _updatingEngine.value = true
            _message.value = null
            runCatching {
                withContext(Dispatchers.IO) {
                    repository.awaitEngineReady()
                    YoutubeDL.getInstance()
                        .updateYoutubeDL(getApplication(), YoutubeDL.UpdateChannel.STABLE)
                }
            }.onSuccess { status ->
                _engineVersion.value = repository.engineVersion()
                _message.value = when (status) {
                    YoutubeDL.UpdateStatus.DONE -> strings.msgEngineUpdated
                    YoutubeDL.UpdateStatus.ALREADY_UP_TO_DATE -> strings.msgEngineUpToDate
                    else -> strings.msgEngineFinished
                }
            }.onFailure { error ->
                _message.value = error.friendlyMessage(strings.engineUpdateFailedPrefix)
            }
            _updatingEngine.value = false
        }
    }

    private fun startDownload(url: String, title: String) {
        val id = UUID.randomUUID().toString()
        val item = DownloadItem(
            id = id,
            title = title,
            sourceUrl = url,
            status = DownloadStatus.DOWNLOADING,
        )
        _downloads.value = listOf(item) + _downloads.value
        _message.value = null

        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            val outcome = runCatching {
                repository.downloadLink(url, id) { progress -> updateProgress(id, progress) }
            }
            downloadJobs.remove(id)
            if (!isActive) return@launch
            outcome
                .onSuccess { published ->
                    update(id) {
                        it.copy(
                            status = DownloadStatus.COMPLETED,
                            progress = 1f,
                            savedPath = published.displayPath,
                            fileUri = published.uri,
                        )
                    }
                    _message.value = strings.savedTo(published.displayPath)
                    persist()
                }
                .onFailure { error ->
                    update(id) {
                        it.copy(
                            status = DownloadStatus.FAILED,
                            error = error.friendlyMessage(strings.downloadFailedPrefix),
                        )
                    }
                    _message.value = strings.msgManualFallback
                }
        }
        downloadJobs[id] = job
        job.start()
    }

    private fun updateProgress(id: String, value: Float) {
        if (!value.isFinite()) return
        update(id) { it.copy(progress = value.coerceIn(0f, 1f)) }
    }

    private fun update(id: String, transform: (DownloadItem) -> DownloadItem) {
        _downloads.value = _downloads.value.map { if (it.id == id) transform(it) else it }
    }

    private fun persist() {
        history.save(_downloads.value)
    }

    private fun titleFromUrl(url: String): String =
        url.substringBefore('?').substringAfterLast('/').ifBlank { strings.linkedAudio }

    private fun Throwable.friendlyMessage(prefix: String): String {
        val detail = message?.lineSequence()?.firstOrNull { it.isNotBlank() } ?: return prefix
        return "$prefix: ${detail.take(180)}"
    }
}
