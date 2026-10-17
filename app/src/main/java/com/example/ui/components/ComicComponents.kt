package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.CharacterDna
import com.example.data.ComicPanel
import com.example.data.ImageHelperService
import com.example.ui.theme.*

@Composable
fun SpeechBubble(
    speaker: String,
    dialogue: String,
    modifier: Modifier = Modifier,
    isThinking: Boolean = false
) {
    Column(modifier = modifier) {
        Surface(
            shape = if (isThinking) RoundedCornerShape(20.dp) else RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 4.dp),
            color = ComicBubbleBg,
            border = androidx.compose.foundation.BorderStroke(2.dp, ComicBubbleBorder),
            shadowElevation = 4.dp,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                if (speaker.isNotBlank()) {
                    Text(
                        text = speaker.uppercase(),
                        color = ComicOrange,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "\"$dialogue\"",
                    color = ComicBubbleText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
fun SoundEffectBadge(
    sfx: String,
    modifier: Modifier = Modifier
) {
    if (sfx.isBlank()) return
    Surface(
        shape = CutCornerShape(8.dp),
        color = ComicRed,
        border = androidx.compose.foundation.BorderStroke(2.dp, ComicYellow),
        shadowElevation = 6.dp,
        modifier = modifier.rotate(-6f)
    ) {
        Text(
            text = sfx.uppercase(),
            color = ComicWhite,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun CharacterDnaCard(
    character: CharacterDna,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ComicDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, ComicCyan.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("character_dna_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ComicCyan.copy(alpha = 0.2f))
                            .border(1.5.dp, ComicCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Character DNA Locked",
                            tint = ComicCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = character.name,
                            fontWeight = FontWeight.Bold,
                            color = ComicTextPrimary,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "DNA Consistency Locked 🔒",
                            color = ComicCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(
                    onClick = {
                        ImageHelperService.copyPromptToClipboard(context, character.toFlowPromptAnchor())
                    },
                    modifier = Modifier.testTag("copy_dna_button")
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy DNA Anchor",
                        tint = ComicYellow
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Outfit: ${character.signatureOutfit}",
                fontSize = 13.sp,
                color = ComicTextSecondary,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Face: ${character.facialTraits}",
                        fontSize = 12.sp,
                        color = ComicTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hair: ${character.hairStyle}",
                        fontSize = 12.sp,
                        color = ComicTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Style: ${character.artStyle}",
                        fontSize = 12.sp,
                        color = ComicCyan
                    )
                }
            }

            TextButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = if (isExpanded) "Kam dikhao ▲" else "Poora DNA dekhein ▼",
                    color = ComicYellow,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ComicPanelItemCard(
    panel: ComicPanel,
    character: CharacterDna,
    onRenderRequested: (ComicPanel) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val flowPrompt = remember(panel, character) { panel.buildFlowPrompt(character) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ComicDarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ComicCardBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("panel_card_${panel.panelNumber}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Panel number & Shot type
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = ComicYellow,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = "PANEL ${panel.panelNumber}",
                            color = ComicBlack,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Text(
                        text = panel.cameraShot,
                        color = ComicTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                SoundEffectBadge(sfx = panel.soundEffect)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Panel Image Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ComicDarkSurfaceVariant)
                    .border(1.dp, ComicCardBorder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (panel.imageUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(panel.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Panel ${panel.panelNumber} art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Palette,
                            contentDescription = null,
                            tint = ComicTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Google Flow me prompt dalo ya niche Instant Preview tap karo!",
                            color = ComicTextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Overlay speech bubble on the image
                SpeechBubble(
                    speaker = panel.speakerName,
                    dialogue = panel.dialogueText,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                )

                if (panel.isGenerating) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ComicYellow)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Story Action
            Text(
                text = "Scene: ${panel.description}",
                color = ComicTextPrimary,
                fontSize = 13.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Copy Flow Prompt
                Button(
                    onClick = {
                        ImageHelperService.copyPromptToClipboard(context, flowPrompt, panel.panelNumber)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ComicDarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ComicCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("copy_flow_prompt_${panel.panelNumber}")
                ) {
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy Flow Prompt",
                        tint = ComicCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Copy Flow Prompt",
                        color = ComicCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Instant AI Preview
                Button(
                    onClick = { onRenderRequested(panel) },
                    colors = ButtonDefaults.buttonColors(containerColor = ComicYellow),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("render_panel_${panel.panelNumber}")
                ) {
                    Icon(
                        Icons.Default.AutoFixHigh,
                        contentDescription = "Instant AI Preview",
                        tint = ComicBlack,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Instant Preview",
                        color = ComicBlack,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
