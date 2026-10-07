package io.github.agimaulana.codemagicconnect.feature.builds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.agimaulana.codemagicconnect.R
import io.github.agimaulana.codemagicconnect.domain.model.BuildArtifact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DownloadCompleteBottomSheet(
    artifact: BuildArtifact,
    onDismiss: () -> Unit,
    onInstall: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFE6F4EA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF137333)
                    )
                }
                
                Spacer(modifier = Modifier.size(16.dp))
                
                Column {
                    Text(
                        text = stringResource(R.string.bottom_sheet_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191A1E)
                    )
                    Text(
                        text = stringResource(R.string.bottom_sheet_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5E6573)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F6F8), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = artifact.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF191A1E)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.bottom_sheet_size_label), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF5E6573), modifier = Modifier.weight(1f))
                        Text(stringResource(R.string.builds_size_mb, (artifact.sizeBytes / 1000000.0).toString()), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF191A1E))
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.bottom_sheet_workflow_label), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF5E6573), modifier = Modifier.weight(1f))
                        Text("android-debug", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF191A1E), fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(stringResource(R.string.bottom_sheet_branch_label), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF5E6573), modifier = Modifier.weight(1f))
                        Text("feature/login-flow", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF191A1E), fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = stringResource(R.string.bottom_sheet_install_notice),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF5E6573)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onInstall,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E4DF5))
            ) {
                Text(stringResource(R.string.bottom_sheet_install_button), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.bottom_sheet_dismiss_button), color = Color(0xFF5E6573), style = MaterialTheme.typography.titleMedium)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
