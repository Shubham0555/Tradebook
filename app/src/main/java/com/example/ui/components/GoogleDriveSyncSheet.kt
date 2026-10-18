package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AccentCyan
import com.example.ui.theme.AccentGold
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.ProfitGreen
import com.example.ui.theme.ProfitGreenBg
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GoogleDriveSyncSheet(
  accountEmail: String,
  lastSyncTime: String,
  isAutoSyncEnabled: Boolean,
  statusMessage: String?,
  onAutoSyncToggle: (Boolean) -> Unit,
  onExportToDriveClick: () -> Unit,
  onImportFromDriveClick: () -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .clip(RoundedCornerShape(16.dp))
        .border(1.dp, CardBorder, RoundedCornerShape(16.dp)),
      color = BackgroundDark
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ProfitGreenBg),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = null,
                tint = ProfitGreen,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Google Drive Cloud Sync",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
              )
              Text(
                text = "Multi-device cross-location backup",
                fontSize = 11.sp,
                color = TextMuted
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_drive_sync_dialog")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Account Info Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = CardBackground),
          border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                tint = AccentCyan,
                modifier = Modifier.size(28.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Connected Google Account",
                  fontSize = 11.sp,
                  color = TextMuted
                )
                Text(
                  text = accountEmail,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = TextPrimary
                )
              }
            }

            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(ProfitGreenBg)
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text("Connected", color = ProfitGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Status message banner if any
        if (statusMessage != null) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(SurfaceVariantDark)
              .border(1.dp, AccentGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
              .padding(10.dp)
          ) {
            Text(
              text = statusMessage,
              fontSize = 12.sp,
              color = AccentGold
            )
          }
          Spacer(modifier = Modifier.height(12.dp))
        }

        // Last sync timestamp
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Last Cloud Sync", fontSize = 12.sp, color = TextSecondary)
          Text(lastSyncTime, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Auto-Sync Toggle
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardBackground)
            .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("Auto-Sync on Changes", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text("Keeps trades synchronized across devices", fontSize = 11.sp, color = TextMuted)
          }
          Switch(
            checked = isAutoSyncEnabled,
            onCheckedChange = onAutoSyncToggle,
            colors = SwitchDefaults.colors(
              checkedThumbColor = BackgroundDark,
              checkedTrackColor = ProfitGreen
            )
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons: Backup to Drive & Restore from Drive
        Button(
          onClick = onExportToDriveClick,
          colors = ButtonDefaults.buttonColors(containerColor = ProfitGreen, contentColor = BackgroundDark),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("backup_to_drive_button")
        ) {
          Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Backup Database to Google Drive", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
          onClick = onImportFromDriveClick,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("restore_from_drive_button")
        ) {
          Icon(Icons.Default.CloudDownload, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Restore Database from Google Drive", color = AccentCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
      }
    }
  }
}
