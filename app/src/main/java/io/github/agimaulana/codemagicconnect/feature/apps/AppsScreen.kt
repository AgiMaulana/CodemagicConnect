package io.github.agimaulana.codemagicconnect.feature.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.agimaulana.codemagicconnect.R
import io.github.agimaulana.codemagicconnect.domain.model.CodemagicApplication
import io.github.agimaulana.codemagicconnect.ui.theme.CodemagicConnectTheme

@Composable
internal fun AppsScreen(
    uiState: AppsViewModel.UiState,
    onAction: (AppsViewModel.Action) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6F8))
            .systemBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(R.string.apps_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF191A1E)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.apps_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF5E6573)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { onAction(AppsViewModel.Action.UpdateSearchQuery(it)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(R.string.apps_search_placeholder)) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF5E6573))
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )

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
                items(uiState.apps) { app ->
                    AppItem(
                        app = app,
                        isSelected = app.isDefault,
                        onClick = { onAction(AppsViewModel.Action.SelectApp(app)) },
                        onFavoriteClick = { onAction(AppsViewModel.Action.ToggleFavorite(app)) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F6F8))
                .padding(vertical = 16.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = uiState.openAppAutomatically,
                        onCheckedChange = { onAction(AppsViewModel.Action.ToggleOpenAutomatically(it)) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFF2E4DF5)
                        )
                    )
                    Text(
                        text = stringResource(R.string.apps_open_automatically_checkbox_label, uiState.apps.firstOrNull { it.isDefault }?.name ?: "the app"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF191A1E)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onAction(AppsViewModel.Action.ContinueToBuilds) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = uiState.apps.any { it.isDefault },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E4DF5)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.apps_continue_button),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AppItem(
    app: CodemagicApplication,
    isSelected: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color(0xFF2E4DF5) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8EEFC)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.name.first().uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E4DF5)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF191A1E)
                )
                Text(
                    text = app.repositoryUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5E6573)
                )
                Text(
                    text = app.lastBuildTime ?: stringResource(R.string.apps_no_builds_yet),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5E6573)
                )
            }

            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (app.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = stringResource(R.string.apps_favorite_content_desc),
                    tint = if (app.isFavorite) Color(0xFFF2B005) else Color(0xFF5E6573)
                )
            }
        }
    }
}

@Preview
@Composable
private fun AppsScreenPreview() {
    CodemagicConnectTheme {
        AppsScreen(
            uiState = AppsViewModel.UiState(
                apps = listOf(
                    CodemagicApplication(
                        id = "app-1",
                        name = "acme-mobile",
                        repositoryUrl = "github.com/acme/mobile-app",
                        lastBuildTime = "Last build today, 14:32",
                        isFavorite = true,
                        isDefault = true,
                        teamId = "acme"
                    ),
                    CodemagicApplication(
                        id = "app-2",
                        name = "acme-driver-app",
                        repositoryUrl = "github.com/acme/driver-app",
                        lastBuildTime = "Last build yesterday, 09:10",
                        teamId = "acme"
                    ),
                    CodemagicApplication(
                        id = "app-3",
                        name = "acme-admin-portal",
                        repositoryUrl = "github.com/acme/admin-portal",
                        lastBuildTime = null,
                        teamId = "acme"
                    )
                ),
                openAppAutomatically = true
            ),
            onAction = {}
        )
    }
}
