package io.github.agimaulana.codemagicconnect.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.agimaulana.codemagicconnect.R
import io.github.agimaulana.codemagicconnect.ui.theme.CodemagicConnectTheme

@Composable
internal fun SettingsScreen(
    uiState: SettingsViewModel.UiState,
    onAction: (SettingsViewModel.Action) -> Unit,
    onBackClicked: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFFF5F6F8)
    ) { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
            .verticalScroll(rememberScrollState())
            .padding(innerPadding)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClicked) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.settings_back_content_desc), tint = Color(0xFF191A1E))
            }
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191A1E)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.settings_section_account),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5E6573),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_api_token_label),
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
                                text = stringResource(R.string.settings_status_connected),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF137333)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF5F6F8), RoundedCornerShape(8.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = uiState.tokenObfuscated,
                            style = MaterialTheme.typography.bodyLarge,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF191A1E)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.settings_token_added, uiState.tokenAddedDate),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5E6573)
                        )
                        Text(
                            text = uiState.tokenVerifiedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5E6573)
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFF5F6F8))

                ActionItem(
                    icon = Icons.Default.Refresh,
                    text = stringResource(R.string.settings_action_test_connection),
                    onClick = { onAction(SettingsViewModel.Action.TestConnection) },
                    enabled = !uiState.isTestingConnection,
                    isLoading = uiState.isTestingConnection
                )

                HorizontalDivider(color = Color(0xFFF5F6F8))

                ActionItem(
                    icon = Icons.Default.Edit,
                    text = stringResource(R.string.settings_action_replace_token),
                    tint = Color(0xFF2E4DF5),
                    onClick = { onAction(SettingsViewModel.Action.ReplaceToken) }
                )

                HorizontalDivider(color = Color(0xFFF5F6F8))

                ActionItem(
                    icon = Icons.Default.Delete,
                    text = stringResource(R.string.settings_action_remove_token),
                    tint = Color(0xFFC5221F),
                    onClick = { onAction(SettingsViewModel.Action.RemoveTokenAndSignOut) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.settings_token_disclaimer),
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF5E6573),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.settings_section_app),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5E6573),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                if (uiState.defaultAppName != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8EEFC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.defaultAppName.first().uppercase(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E4DF5)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.settings_default_app_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF191A1E)
                            )
                            Text(
                                text = stringResource(R.string.settings_default_app_subtitle, uiState.defaultAppName),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF5E6573)
                            )
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF5E6573))
                    }
                    HorizontalDivider(color = Color(0xFFF5F6F8))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAction(SettingsViewModel.Action.ClearDefaultApp) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_clear_default_app),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF191A1E)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_no_default_app),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF191A1E)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.settings_section_downloads),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5E6573),
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.settings_wifi_only_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF191A1E)
                        )
                        Text(
                            text = stringResource(R.string.settings_wifi_only_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5E6573)
                        )
                    }
                    Switch(
                        checked = uiState.isWifiOnly,
                        onCheckedChange = { onAction(SettingsViewModel.Action.ToggleWifiOnly(it)) },
                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF2E4DF5))
                    )
                }

                HorizontalDivider(color = Color(0xFFF5F6F8))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.settings_delete_apk_title),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF191A1E)
                        )
                        Text(
                            text = stringResource(R.string.settings_delete_apk_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5E6573)
                        )
                    }
                    Switch(
                        checked = uiState.isDeleteApkAfterInstall,
                        onCheckedChange = { onAction(SettingsViewModel.Action.ToggleDeleteApkAfterInstall(it)) },
                        colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF2E4DF5))
                    )
                }

                HorizontalDivider(color = Color(0xFFF5F6F8))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAction(SettingsViewModel.Action.ClearDownloadedFiles) }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.settings_clear_downloads_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191A1E)
                    )
                    Text(
                        text = uiState.downloadedFilesSize,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5E6573)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
    }
    }
}

@Composable
private fun ActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color = Color(0xFF191A1E),
    onClick: () -> Unit,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = tint,
            modifier = Modifier.weight(1f)
        )
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = tint
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    CodemagicConnectTheme {
        SettingsScreen(
            uiState = SettingsViewModel.UiState(
                isTokenConnected = true,
                tokenObfuscated = "cm_••••••••••••3f9a",
                tokenAddedDate = "12 Sep 2026",
                tokenVerifiedDate = "Verified today, 14:30",
                defaultAppName = "acme-mobile",
                isWifiOnly = true,
                isDeleteApkAfterInstall = false,
                downloadedFilesSize = "214 MB"
            ),
            onAction = {},
            onBackClicked = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
