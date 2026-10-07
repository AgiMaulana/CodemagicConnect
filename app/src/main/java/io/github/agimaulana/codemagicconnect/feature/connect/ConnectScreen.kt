package io.github.agimaulana.codemagicconnect.feature.connect

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.agimaulana.codemagicconnect.R
import io.github.agimaulana.codemagicconnect.ui.theme.CodemagicConnectTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ConnectScreen(
    uiState: ConnectViewModel.UiState,
    onAction: (ConnectViewModel.Action) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            if (uiState.showBackButton) {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onBackClicked) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.connect_back_content_desc),
                                tint = Color(0xFF191A1E)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFF5F6F8)
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF5F6F8))
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFF2E4DF5), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.connect_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF191A1E)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.connect_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF5E6573)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.connect_api_token_label),
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF191A1E),
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.token,
                onValueChange = { onAction(ConnectViewModel.Action.UpdateToken(it)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (uiState.isTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (uiState.isTokenVisible) {
                        Icons.Outlined.Visibility
                    } else {
                        Icons.Outlined.VisibilityOff
                    }
                    IconButton(onClick = { onAction(ConnectViewModel.Action.ToggleTokenVisibility) }) {
                        Icon(
                            imageVector = image,
                            contentDescription = stringResource(R.string.connect_api_token_toggle_visibility),
                            tint = Color(0xFF5E6573)
                        )
                    }
                },
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.connect_token_encryption_notice),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF5E6573)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onAction(ConnectViewModel.Action.VerifyAndSaveToken) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading &&
                    !uiState.isCheckingStoredToken &&
                    uiState.token.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E4DF5)
                )
            ) {
                if (uiState.isLoading || uiState.isCheckingStoredToken) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = BUTTON_LOADING_INDICATOR_STROKE_WIDTH
                    )
                } else {
                    Text(
                        text = stringResource(R.string.connect_verify_and_save_button),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.connect_how_to_create_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191A1E)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.connect_how_to_create_steps),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5E6573),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.connect_open_browser),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFF2E4DF5),
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { onAction(ConnectViewModel.Action.OpenCodemagicInBrowser) }
                    )
                }
            }
        }
    }
}

private val BUTTON_LOADING_INDICATOR_STROKE_WIDTH = 2.dp

@Preview
@Composable
private fun ConnectScreenPreview() {
    CodemagicConnectTheme {
        ConnectScreen(
            uiState = ConnectViewModel.UiState(
                token = "cm_pat_2a8f1c9d4e6b7a30f5c8d1e2"
            ),
            onBackClicked = {},
            onAction = {}
        )
    }
}
