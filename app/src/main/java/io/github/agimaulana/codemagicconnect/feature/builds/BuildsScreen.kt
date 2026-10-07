package io.github.agimaulana.codemagicconnect.feature.builds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.agimaulana.codemagicconnect.R
import io.github.agimaulana.codemagicconnect.domain.model.BuildArtifact
import io.github.agimaulana.codemagicconnect.domain.model.BuildStatus
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicApplication
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicBuild
import io.github.agimaulana.codemagicconnect.ui.theme.CodemagicConnectTheme

@Composable
internal fun BuildsScreen(
    uiState: BuildsViewModel.UiState,
    onAction: (BuildsViewModel.Action) -> Unit,
    onSettingsClicked: () -> Unit,
    onAppSelectorClicked: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F6F8)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.builds_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191A1E)
            )
            Row {
                IconButton(onClick = { onAction(BuildsViewModel.Action.Refresh) }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.builds_refresh_content_desc),
                        tint = Color(0xFF191A1E)
                    )
                }
                IconButton(onClick = onSettingsClicked) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.builds_settings_content_desc),
                        tint = Color(0xFF191A1E)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.app != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                onClick = onAppSelectorClicked
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8EEFC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.app.name.first().uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E4DF5)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = uiState.app.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF191A1E)
                        )
                        Text(
                            text = stringResource(R.string.builds_default_branch, uiState.app.repositoryUrl),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5E6573)
                        )
                    }
                    IconButton(onClick = onAppSelectorClicked) {Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.builds_app_selector_content_desc),
                        tint = Color(0xFF5E6573)
                    )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = true,
                onClick = { },
                label = { Text(stringResource(R.string.builds_filter_succeeded), fontWeight = FontWeight.SemiBold) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2E4DF5),
                    selectedLabelColor = Color.White
                ),
                border = null
            )
            FilterChip(
                selected = false,
                onClick = { },
                label = { Text(stringResource(R.string.builds_filter_all_branches), fontWeight = FontWeight.SemiBold) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    labelColor = Color(0xFF191A1E)
                ),
                border = FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = Color(0xFFE2E8F0))
            )
            FilterChip(
                selected = false,
                onClick = { },
                label = { Text(stringResource(R.string.builds_filter_workflow), fontWeight = FontWeight.SemiBold) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color.White,
                    labelColor = Color(0xFF191A1E)
                ),
                border = FilterChipDefaults.filterChipBorder(enabled = true, selected = false, borderColor = Color(0xFFE2E8F0))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(uiState.builds) { item ->
                    BuildItem(build = item.build, timestamp = item.timestamp, onAction = onAction)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
    }
}

@Composable
private fun BuildItem(
    build: CodemagicBuild,
    timestamp: BuildsViewModel.BuildTimestamp,
    onAction: (BuildsViewModel.Action) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = build.workflowId,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191A1E)
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE6F4EA), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.builds_status_finished),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF137333)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .background(Color(0xFFF1F3F4), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = build.branch,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        color = Color(0xFF191A1E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = buildTimestampLabel(
                        timestamp = timestamp,
                        triggerer = build.triggerer
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5E6573),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            build.artifacts.forEach { artifact ->
                ArtifactItem(
                    build = build,
                    artifact = artifact,
                    onAction = onAction
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun buildTimestampLabel(
    timestamp: BuildsViewModel.BuildTimestamp,
    triggerer: String
): String {
    val label = when (timestamp) {
        is BuildsViewModel.BuildTimestamp.Today ->
            stringResource(R.string.builds_timestamp_today, timestamp.timeText)
        is BuildsViewModel.BuildTimestamp.Yesterday ->
            stringResource(R.string.builds_timestamp_yesterday, timestamp.timeText)
        is BuildsViewModel.BuildTimestamp.OnDate ->
            stringResource(R.string.builds_timestamp_date, timestamp.dateText, timestamp.timeText)
        is BuildsViewModel.BuildTimestamp.Unknown -> timestamp.rawValue
    }
    return if (triggerer.isBlank()) {
        label
    } else {
        "$label   $triggerer"
    }
}

@Composable
private fun ArtifactItem(
    build: CodemagicBuild,
    artifact: BuildArtifact,
    onAction: (BuildsViewModel.Action) -> Unit
) {
    val bgColor = when (artifact.downloadStatus) {
        BuildArtifact.DownloadStatus.FAILED -> Color(0xFFFCE8E6)
        BuildArtifact.DownloadStatus.DOWNLOADING -> Color(0xFFF8F9FA)
        else -> Color(0xFFF8F9FA)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = artifact.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191A1E)
                )
                
                when (artifact.downloadStatus) {
                    BuildArtifact.DownloadStatus.NOT_DOWNLOADED -> {
                        Text(
                            text = stringResource(R.string.builds_size_mb, (artifact.sizeBytes / 1000000.0).toString()),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5E6573)
                        )
                    }
                    BuildArtifact.DownloadStatus.DOWNLOADING -> {
                        Text(
                            text = stringResource(
                                R.string.builds_artifact_downloading, 
                                (artifact.downloadSizeSoFarBytes / 1000000.0).toString(), 
                                (artifact.sizeBytes / 1000000.0).toString(), 
                                (artifact.downloadProgress * 100).toInt()
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5E6573)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { artifact.downloadProgress },
                            modifier = Modifier.fillMaxWidth().height(4.dp),
                            color = Color(0xFF2E4DF5),
                            trackColor = Color(0xFFE2E8F0)
                        )
                    }
                    BuildArtifact.DownloadStatus.DOWNLOADED -> {
                        Text(
                            text = stringResource(R.string.builds_artifact_downloaded, (artifact.sizeBytes / 1000000.0).toString()),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF137333)
                        )
                    }
                    BuildArtifact.DownloadStatus.FAILED -> {
                        Text(
                            text = stringResource(R.string.builds_artifact_failed),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFC5221F)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            when (artifact.downloadStatus) {
                BuildArtifact.DownloadStatus.NOT_DOWNLOADED -> {
                    Button(
                        onClick = { onAction(BuildsViewModel.Action.DownloadArtifact(build.id, artifact.id)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E4DF5)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(R.string.builds_button_download), fontWeight = FontWeight.Bold)
                    }
                }
                BuildArtifact.DownloadStatus.DOWNLOADING -> {
                    IconButton(onClick = { /* cancel */ }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = stringResource(R.string.builds_button_cancel_content_desc), tint = Color(0xFF5E6573))
                    }
                }
                BuildArtifact.DownloadStatus.DOWNLOADED -> {
                    Button(
                        onClick = { onAction(BuildsViewModel.Action.InstallArtifact(artifact.id)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF137333)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(R.string.builds_button_install), fontWeight = FontWeight.Bold)
                    }
                }
                BuildArtifact.DownloadStatus.FAILED -> {
                    Button(
                        onClick = { onAction(BuildsViewModel.Action.RetryDownload(build.id, artifact.id)) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC5221F)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(stringResource(R.string.builds_button_retry), fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun BuildsScreenPreview() {
    CodemagicConnectTheme {
        BuildsScreen(
            uiState = BuildsViewModel.UiState(
                app = CodemagicApplication(
                    id = "app-1",
                    name = "acme-mobile",
                    repositoryUrl = "github.com/acme/mobile-app",
                    lastBuildTime = "Last build today, 14:32",
                    isFavorite = true,
                    isDefault = true,
                    teamId = "acme"
                ),
                builds = listOf(
                    BuildsViewModel.BuildListItem(
                        build = CodemagicBuild(
                            id = "build-1",
                            appId = "app-1",
                            workflowId = "android-release",
                            branch = "main",
                            status = BuildStatus.FINISHED,
                            startedAt = "2026-10-07T14:20:00Z",
                            finishedAt = "2026-10-07T14:32:00Z",
                            triggerer = "agi.maulana",
                            artifacts = listOf(
                                BuildArtifact(
                                    id = "artifact-1",
                                    name = "app-release.apk",
                                    sizeBytes = 42_800_000L
                                ),
                                BuildArtifact(
                                    id = "artifact-2",
                                    name = "app-arm64-v8a-release.apk",
                                    sizeBytes = 18_400_000L,
                                    downloadStatus = BuildArtifact.DownloadStatus.DOWNLOADING,
                                    downloadProgress = 0.62f,
                                    downloadSizeSoFarBytes = 11_400_000L
                                )
                            )
                        ),
                        timestamp = BuildsViewModel.BuildTimestamp.Today("14:20")
                    ),
                    BuildsViewModel.BuildListItem(
                        build = CodemagicBuild(
                            id = "build-2",
                            appId = "app-1",
                            workflowId = "android-debug",
                            branch = "feature/login-flow",
                            status = BuildStatus.FINISHED,
                            startedAt = "2026-10-06T09:05:00Z",
                            finishedAt = "2026-10-06T09:10:00Z",
                            triggerer = "ci-bot",
                            artifacts = listOf(
                                BuildArtifact(
                                    id = "artifact-3",
                                    name = "app-debug.apk",
                                    sizeBytes = 24_600_000L,
                                    downloadStatus = BuildArtifact.DownloadStatus.DOWNLOADED
                                )
                            )
                        ),
                        timestamp = BuildsViewModel.BuildTimestamp.Yesterday("09:05")
                    )
                )
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onAction = {},
            onSettingsClicked = {},
            onAppSelectorClicked = {}
        )
    }
}
