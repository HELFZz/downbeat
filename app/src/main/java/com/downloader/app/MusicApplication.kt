package com.downloader.app

import android.app.Application
import android.util.Log
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.CompletableDeferred

class MusicApplication : Application() {

    private val engineReady = CompletableDeferred<Unit>()

    override fun onCreate() {
        super.onCreate()
        Thread({
            runCatching { YoutubeDL.getInstance().init(this) }
                .onFailure { Log.e(TAG, "Failed to initialize yt-dlp", it) }
            runCatching { FFmpeg.getInstance().init(this) }
                .onFailure { Log.e(TAG, "Failed to initialize ffmpeg", it) }
            engineReady.complete(Unit)
        }, "downbeat-engine-init").start()
    }

    suspend fun awaitEngineReady() {
        engineReady.await()
    }

    private companion object {
        const val TAG = "Downbeat"
    }
}
