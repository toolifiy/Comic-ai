package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.ComicProject
import com.example.ui.components.SoundEffectBadge
import com.example.ui.components.SpeechBubble
import com.example.ui.theme.*

@Composable
fun ComicReaderScreen(
    project: ComicProject,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isWebtoonMode by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ComicDarkBg)
    ) {
        // Top Reader Controls Bar
        Surface(
            color = ComicDarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, ComicCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = project.title,
                        color = ComicTextPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        maxLines = 1
                    )
                    Text(
                        text = "Issue #1 • ${project.panels.size} Pages",
                        color = ComicYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            isWebtoonMode = !isWebtoonMode
                            val modeName = if (isWebtoonMode) "Webtoon Vertical Mode" else "Graphic Novel Grid Mode"
                            Toast.makeText(context, modeName, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("toggle_read_mode_button")
                    ) {
                        Icon(
                            if (isWebtoonMode) Icons.Default.ViewStream else Icons.Default.GridView,
                            contentDescription = "Toggle Read Mode",
                            tint = ComicCyan
                        )
                    }

                    IconButton(
                        onClick = {
                            Toast.makeText(context, "Comic Book saved to local storage!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("download_comic_button")
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Download Comic",
                            tint = ComicYellow
                        )
                    }
                }
            }
        }

        // Reader Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            // Issue Cover Header
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ComicDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(2.dp, ComicCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚡ COMIXFLOW EXCLUSIVE ⚡",
                            color = ComicOrange,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = project.title.uppercase(),
                            color = ComicWhite,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Created by Sameer & Gemini AI Director",
                            color = ComicTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // The Comic Panels in Action
            itemsIndexed(project.panels, key = { _, panel -> panel.id }) { index, panel ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ComicDarkSurface),
                    border = androidx.compose.foundation.BorderStroke(2.5.dp, ComicCardBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("reader_panel_$index")
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isWebtoonMode) 320.dp else 240.dp)
                            .background(ComicDarkSurfaceVariant)
                    ) {
                        if (panel.imageUrl != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(panel.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Panel ${panel.panelNumber}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Palette,
                                        contentDescription = null,
                                        tint = ComicTextMuted,
                                        modifier = Modifier.size(40.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "[Panel ${panel.panelNumber} Art Pending]\nStory Studio me 'Instant Preview' tap karo",
                                        color = ComicTextSecondary,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        // Top-left Panel Tag
                        Surface(
                            shape = RoundedCornerShape(bottomEnd = 8.dp),
                            color = ComicYellow,
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Text(
                                text = "#${panel.panelNumber}",
                                color = ComicBlack,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        // SFX burst in the top right or center
                        if (panel.soundEffect.isNotBlank()) {
                            SoundEffectBadge(
                                sfx = panel.soundEffect,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp)
                            )
                        }

                        // Dynamic Speech Bubble
                        SpeechBubble(
                            speaker = panel.speakerName,
                            dialogue = panel.dialogueText,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(10.dp)
                        )
                    }
                }
            }
        }
    }
}
