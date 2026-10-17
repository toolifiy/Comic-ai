package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.BuildConfig
import com.example.ui.theme.*

@Composable
fun SettingsDialog(
    currentApiKey: String,
    onSaveApiKey: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var keyInput by remember { mutableStateOf(currentApiKey) }
    val context = LocalContext.current
    val hasBuildConfigKey = BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ComicDarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ComicYellow),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("settings_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Key,
                        contentDescription = "Gemini API Key",
                        tint = ComicYellow,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Gemini API Settings",
                        color = ComicTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Key Status Card
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = ComicDarkSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (hasBuildConfigKey || currentApiKey.isNotBlank()) Icons.Default.CheckCircle else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (hasBuildConfigKey || currentApiKey.isNotBlank()) ComicCyan else ComicYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasBuildConfigKey) {
                                "Active via AI Studio Secrets (Automatic) ✅"
                            } else if (currentApiKey.isNotBlank()) {
                                "Custom Key configured in App ✅"
                            } else {
                                "Demo & Smart Director mode active! Apni key daalkar full AI unlock karein."
                            },
                            color = ComicTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Custom Gemini API Key (Optional)",
                    color = ComicTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    placeholder = { Text("AIzaSy...", color = ComicTextMuted, fontSize = 13.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ComicTextPrimary,
                        unfocusedTextColor = ComicTextPrimary,
                        focusedBorderColor = ComicYellow,
                        unfocusedBorderColor = ComicCardBorder,
                        focusedContainerColor = ComicDarkBg,
                        unfocusedContainerColor = ComicDarkBg
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("api_key_input_field")
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = ComicTextSecondary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onSaveApiKey(keyInput.trim())
                            Toast.makeText(context, "API Key updated successfully!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ComicYellow),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("save_api_key_button")
                    ) {
                        Text("Save Key", color = ComicBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
