package com.downloader.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.downloader.app.data.DownloadItem
import com.downloader.app.data.DownloadStatus
import com.downloader.app.i18n.AppStrings
import com.downloader.app.i18n.LocalStrings
import com.downloader.app.ui.components.WavyProgress

@Composable
fun DownloadsScreen(
    downloads: List<DownloadItem>,
    onRemove: (String) -> Unit,
    onRetry: (DownloadItem) -> Unit,
    onOpenLink: (String) -> Unit,
    onOpenFile: (String) -> Unit,
    onCopyLink: (String) -> Unit,
    onGoSearch: () -> Unit,
) {
    val strings = LocalStrings.current
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        ScreenHeader(
            title = strings.downloadsTitle,
            subtitle = strings.downloadsSubtitle,
        )
        Spacer(Modifier.height(16.dp))
        if (downloads.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 32.dp)) {
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .clip(RoundedCornerShape(32.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Download,
                            contentDescription = null,
                            modifier = Modifier.size(44.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = strings.nothingSavedTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = strings.nothingSavedBody,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(16.dp))
                    FilledTonalButton(onClick = onGoSearch, shape = CircleShape) {
                        Text(strings.findMusic)
                    }
                }
            }
        } else {
            val active = downloads.count { it.status == DownloadStatus.DOWNLOADING }
            val done = downloads.count { it.status == DownloadStatus.COMPLETED }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetaPill(text = strings.savedCount(done))
                if (active > 0) MetaPill(text = strings.inProgressCount(active))
                val failed = downloads.count { it.status == DownloadStatus.FAILED }
                if (failed > 0) {
                    MetaPill(
                        text = strings.failedCount(failed),
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                items(downloads, key = { it.id }) { item ->
                    DownloadSegment(
                        item = item,
                        onRemove = { onRemove(item.id) },
                        onRetry = { onRetry(item) },
                        onOpenLink = onOpenLink,
                        onOpenFile = onOpenFile,
                        onCopyLink = onCopyLink,
                    )
                }
            }
        }
    }
}

@Composable
private fun DownloadSegment(
    item: DownloadItem,
    onRemove: () -> Unit,
    onRetry: () -> Unit,
    onOpenLink: (String) -> Unit,
    onOpenFile: (String) -> Unit,
    onCopyLink: (String) -> Unit,
) {
    val strings = LocalStrings.current
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(Modifier.fillMaxWidth().padding(start = 14.dp, top = 14.dp, end = 14.dp, bottom = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            when (item.status) {
                                DownloadStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                                DownloadStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
                                DownloadStatus.DOWNLOADING -> MaterialTheme.colorScheme.secondaryContainer
                            }
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = when (item.status) {
                            DownloadStatus.COMPLETED -> Icons.Rounded.CheckCircle
                            DownloadStatus.FAILED -> Icons.Rounded.WarningAmber
                            DownloadStatus.DOWNLOADING -> Icons.Rounded.Download
                        },
                        contentDescription = null,
                        tint = when (item.status) {
                            DownloadStatus.COMPLETED -> MaterialTheme.colorScheme.onPrimary
                            DownloadStatus.FAILED -> MaterialTheme.colorScheme.onErrorContainer
                            DownloadStatus.DOWNLOADING -> MaterialTheme.colorScheme.onSecondaryContainer
                        },
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = when (item.status) {
                            DownloadStatus.DOWNLOADING -> strings.downloadingAudio
                            DownloadStatus.COMPLETED -> strings.savedTo(item.savedPath ?: strings.defaultFolder)
                            DownloadStatus.FAILED -> strings.directDownloadFailed
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                IconButton(onClick = onRemove, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = strings.removeFromList,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            when (item.status) {
                DownloadStatus.DOWNLOADING -> {
                    Spacer(Modifier.height(10.dp))
                    WavyProgress(progress = item.progress, modifier = Modifier.fillMaxWidth())
                    Row(Modifier.fillMaxWidth().padding(top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (item.progress >= 0.99f) strings.converting else strings.downloading,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = "${(item.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }

                DownloadStatus.FAILED -> {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = item.error ?: strings.somethingWentWrong,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = strings.downloadLink,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = item.sourceUrl,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable { onOpenLink(item.sourceUrl) },
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = onRetry, shape = CircleShape) {
                            Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(strings.retry)
                        }
                        OutlinedButton(onClick = { onOpenLink(item.sourceUrl) }, shape = CircleShape) {
                            Icon(Icons.AutoMirrored.Rounded.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(strings.openLink)
                        }
                    }
                    TextButton(onClick = { onCopyLink(item.sourceUrl) }) {
                        Icon(Icons.Rounded.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(strings.copyLink)
                    }
                }

                DownloadStatus.COMPLETED -> {
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.savedPath ?: strings.defaultFolder,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                        if (item.fileUri != null) {
                            TextButton(onClick = { onOpenFile(item.fileUri) }) {
                                Icon(Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(strings.play)
                            }
                        }
                    }
                }
            }
        }
    }
}
