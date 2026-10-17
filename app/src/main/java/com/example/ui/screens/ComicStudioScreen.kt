package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ComicPanel
import com.example.data.ComicProject
import com.example.data.ImageHelperService
import com.example.ui.components.CharacterDnaCard
import com.example.ui.components.ComicPanelItemCard
import com.example.ui.theme.*

@Composable
fun ComicStudioScreen(
    project: ComicProject,
    onRenderPanel: (ComicPanel) -> Unit,
    onAddPanel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ComicDarkBg)
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
    ) {
        // Project Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ComicDarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, ComicYellow),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("project_header_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ComicYellow,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = project.genre.uppercase(),
                                color = ComicBlack,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "${project.panels.size} Panels Ready",
                            color = ComicCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = project.title,
                        color = ComicTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = project.logline,
                        color = ComicTextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Launch Google Flow Button
                    Button(
                        onClick = { ImageHelperService.openGoogleFlow(context) },
                        colors = ButtonDefaults.buttonColors(containerColor = ComicOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_google_flow_button")
                    ) {
                        Icon(
                            Icons.Default.OpenInBrowser,
                            contentDescription = "Open Google Flow",
                            tint = ComicWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🚀 Open Google Flow (Free Generations)",
                            color = ComicWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Character DNA Section
        item {
            Column {
                Text(
                    text = "Character Consistency Lock",
                    color = ComicYellow,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                CharacterDnaCard(character = project.characterDna)
            }
        }

        // Pro Tip for Google Flow
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ComicDarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, ComicCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        Icons.Default.TipsAndUpdates,
                        contentDescription = "Pro Tip",
                        tint = ComicYellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "💡 Flow Tip: Panel 1 ka prompt copy karke Google Flow me daalo. Jo image bane, use Flow me 'Character Reference' ki tarah select karo taaki agle panels me hero ka chehra badle nahi!",
                        color = ComicTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Panels Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Comic Panels & Screenplay",
                    color = ComicTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )

                TextButton(
                    onClick = onAddPanel,
                    modifier = Modifier.testTag("add_panel_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = ComicCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Panel", color = ComicCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Panel Items
        items(project.panels, key = { it.id }) { panel ->
            ComicPanelItemCard(
                panel = panel,
                character = project.characterDna,
                onRenderRequested = onRenderPanel
            )
        }
    }
}
