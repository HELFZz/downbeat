package com.downloader.app.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.downloader.app.data.DownloadItem
import com.downloader.app.data.DownloadStatus
import com.downloader.app.data.DownloaderViewModel
import com.downloader.app.i18n.AppStrings
import com.downloader.app.i18n.LocalStrings
import com.downloader.app.ui.components.WavyProgress

private enum class AppTab(val icon: ImageVector) {
    SEARCH(Icons.Rounded.Search),
    DOWNLOADS(Icons.Rounded.Download),
    SETTINGS(Icons.Rounded.Settings),
}

private fun AppStrings.labelFor(tab: AppTab): String = when (tab) {
    AppTab.SEARCH -> tabSearch
    AppTab.DOWNLOADS -> tabDownloads
    AppTab.SETTINGS -> tabSettings
}

@Composable
fun DownbeatApp(model: DownloaderViewModel, sharedUrl: String? = null, sharedToken: Long = 0L) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val snackbar = remember { SnackbarHostState() }
    val strings = LocalStrings.current

    val query by model.query.collectAsState()
    val results by model.results.collectAsState()
    val downloads by model.downloads.collectAsState()
    val searching by model.searching.collectAsState()
    val hasSearched by model.hasSearched.collectAsState()
    val message by model.message.collectAsState()
    val engineVersion by model.engineVersion.collectAsState()
    val updatingEngine by model.updatingEngine.collectAsState()
    val language by model.language.collectAsState()
    val theme by model.theme.collectAsState()

    var tab by rememberSaveable { mutableStateOf(AppTab.SEARCH) }
    var showLinkDialog by rememberSaveable { mutableStateOf(false) }
    var linkDraft by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(sharedUrl, sharedToken) {
        if (!sharedUrl.isNullOrBlank()) {
            linkDraft = sharedUrl.trim()
            showLinkDialog = true
        }
    }

    LaunchedEffect(message) {
        val text = message
        if (text != null) {
            model.clearMessage()
            snackbar.showSnackbar(text)
        }
    }

    val statuses = remember(downloads) {
        downloads.asReversed().associate { it.sourceUrl to it.status }
    }
    val activeDownload = downloads.firstOrNull { it.status == DownloadStatus.DOWNLOADING }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbar,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        },
        bottomBar = {
            Column {
                if (activeDownload != null) {
                    ActiveDownloadCard(activeDownload) { tab = AppTab.DOWNLOADS }
                }
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                ) {
                    AppTab.entries.forEach { entry ->
                        NavigationBarItem(
                            selected = tab == entry,
                            onClick = { tab = entry },
                            icon = { Icon(entry.icon, contentDescription = strings.labelFor(entry)) },
                            label = { Text(strings.labelFor(entry)) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            ),
                        )
                    }
                }
            }
        },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AnimatedContent(targetState = tab, label = "tab-content") { current ->
                when (current) {
                    AppTab.SEARCH -> SearchScreen(
                        query = query,
                        results = results,
                        searching = searching,
                        hasSearched = hasSearched,
                        statuses = statuses,
                        onQueryChange = model::setQuery,
                        onSearch = model::search,
                        onDownload = model::download,
                        onAlreadySaved = { model.say(strings.alreadySaved) },
                        onLink = {
                            linkDraft = ""
                            showLinkDialog = true
                        },
                        onSuggestion = { suggestion ->
                            model.setQuery(suggestion)
                            model.search()
                        },
                    )

                    AppTab.DOWNLOADS -> DownloadsScreen(
                        downloads = downloads,
                        onRemove = model::remove,
                        onRetry = model::retry,
                        onOpenLink = { openExternalUrl(context, it) },
                        onOpenFile = { openAudioFile(context, it) },
                        onCopyLink = { url ->
                            clipboard.setText(AnnotatedString(url))
                            model.say(strings.linkCopied)
                        },
                        onGoSearch = { tab = AppTab.SEARCH },
                    )

                    AppTab.SETTINGS -> SettingsScreen(
                        engineVersion = engineVersion,
                        updatingEngine = updatingEngine,
                        theme = theme,
                        language = language,
                        onUpdateEngine = model::updateEngine,
                        onThemeChange = model::setTheme,
                        onLanguageChange = model::setLanguage,
                        onLink = {
                            linkDraft = ""
                            showLinkDialog = true
                        },
                    )
                }
            }
        }
    }

    if (showLinkDialog) {
        LinkDialog(
            initialText = linkDraft,
            onDismiss = { showLinkDialog = false },
            onConfirm = { url ->
                showLinkDialog = false
                model.downloadLink(url)
                tab = AppTab.DOWNLOADS
            },
        )
    }
}

@Composable
private fun ActiveDownloadCard(item: DownloadItem, onClick: () -> Unit) {
    val strings = LocalStrings.current
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
    ) {
        Column {
            Row(
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Download,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = strings.downloading,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    text = "${(item.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }
            WavyProgress(
                progress = item.progress,
                modifier = Modifier.padding(horizontal = 10.dp),
                activeColor = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 2.dp,
                amplitude = 2.dp,
            )
        }
    }
}

@Composable
private fun LinkDialog(
    initialText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val strings = LocalStrings.current
    var text by remember(initialText) { mutableStateOf(initialText) }
    val clipboard = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        title = {
            Column {
                Text(
                    text = strings.linkDialogTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = strings.linkDialogSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("https://…") },
                    singleLine = true,
                    shape = CircleShape,
                    leadingIcon = {
                        Icon(Icons.Rounded.Link, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val pasted = clipboard.getText()?.text
                                if (!pasted.isNullOrBlank()) text = pasted
                            },
                        ) {
                            Icon(
                                Icons.Rounded.ContentPaste,
                                contentDescription = strings.pasteFromClipboard,
                            )
                        }
                    },
                )
                Spacer(Modifier.size(10.dp))
                Text(
                    text = strings.linkDialogNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank(),
                shape = CircleShape,
            ) {
                Icon(Icons.Rounded.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(strings.download)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        },
    )
}
