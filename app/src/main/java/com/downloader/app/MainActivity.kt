package com.downloader.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.downloader.app.data.DownloaderViewModel
import com.downloader.app.i18n.LocalStrings
import com.downloader.app.i18n.stringsFor
import com.downloader.app.ui.DownbeatApp
import com.downloader.app.ui.theme.DownbeatTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {

    private val sharedText = MutableStateFlow<Pair<String, Long>?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestLegacyStoragePermission()
        sharedText.value = intent.shareText()?.let { it to System.nanoTime() }
        setContent {
            val model: DownloaderViewModel = viewModel()
            val language by model.language.collectAsState()
            val theme by model.theme.collectAsState()
            val shared by sharedText.collectAsState()
            val strings = remember(language) { stringsFor(language) }
            CompositionLocalProvider(LocalStrings provides strings) {
                DownbeatTheme(theme = theme) {
                    DownbeatApp(
                        model = model,
                        sharedUrl = shared?.first,
                        sharedToken = shared?.second ?: 0L,
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        sharedText.value = intent.shareText()?.let { it to System.nanoTime() }
    }

    private fun requestLegacyStoragePermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) return
        val granted = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_STORAGE,
            )
        }
    }

    private fun Intent.shareText(): String? {
        if (action != Intent.ACTION_SEND) return null
        val extra = getStringExtra(Intent.EXTRA_TEXT)
        val clipped = clipData?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.text?.toString()
        return (extra ?: clipped)?.trim()?.takeIf { it.isNotBlank() }
    }

    private companion object {
        const val REQUEST_STORAGE = 1001
    }
}
