package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatMessage
import com.example.data.MessageSender
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun DirectorChatScreen(
    messages: List<ChatMessage>,
    isGenerating: Boolean,
    onSendMessage: (String) -> Unit,
    onQuickPromptSelected: (String) -> Unit,
    onLoadScriptToStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val quickPrompts = listOf(
        "📊 Market Trends Scan",
        "⚡ Generate 4-Panel Script",
        "🦸 Lock Character DNA",
        "🤖 Cyberpunk Manga Idea"
    )

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ComicDarkBg)
    ) {
        // Quick Action Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                FilterChip(
                    selected = false,
                    onClick = { onQuickPromptSelected(prompt) },
                    label = {
                        Text(
                            text = prompt,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ComicYellow
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = ComicDarkSurface,
                        labelColor = ComicYellow
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = ComicCardBorder,
                        enabled = true,
                        selected = false
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("quick_chip_${prompt.take(5)}")
                )
            }
        }

        // Messages LazyColumn
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubbleItem(
                    message = msg,
                    onLoadScriptToStudio = onLoadScriptToStudio
                )
            }

            if (isGenerating) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        CircularProgressIndicator(
                            color = ComicYellow,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Gemini is analyzing market & scripting comic...",
                            color = ComicYellow,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Input Bar
        Surface(
            color = ComicDarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, ComicCardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            "Gemini ko batao kaun si comic banani hai...",
                            color = ComicTextMuted,
                            fontSize = 13.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ComicTextPrimary,
                        unfocusedTextColor = ComicTextPrimary,
                        focusedBorderColor = ComicYellow,
                        unfocusedBorderColor = ComicCardBorder,
                        focusedContainerColor = ComicDarkBg,
                        unfocusedContainerColor = ComicDarkBg
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isGenerating) {
                            val textToSend = inputText.trim()
                            inputText = ""
                            onSendMessage(textToSend)
                        }
                    },
                    enabled = inputText.isNotBlank() && !isGenerating,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank()) ComicYellow else ComicCardBorder)
                        .testTag("send_message_button")
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Send Message",
                        tint = ComicBlack
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubbleItem(
    message: ChatMessage,
    onLoadScriptToStudio: () -> Unit
) {
    val isUser = message.sender == MessageSender.USER
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(ComicCyan.copy(alpha = 0.2f))
                        .border(1.dp, ComicCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = "Gemini",
                        tint = ComicCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                color = if (isUser) ComicYellow else ComicDarkSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isUser) ComicYellow else ComicCardBorder
                ),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isUser) "Sameer (Boss)" else "Gemini Director",
                        color = if (isUser) ComicBlack else ComicYellow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = message.text,
                        color = if (isUser) ComicBlack else ComicTextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    // If message contains script or suggestions, show direct load button
                    if (!isUser && message.text.contains("Panel", ignoreCase = true)) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onLoadScriptToStudio,
                            colors = ButtonDefaults.buttonColors(containerColor = ComicCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("load_to_studio_button")
                        ) {
                            Icon(
                                Icons.Default.MovieFilter,
                                contentDescription = null,
                                tint = ComicBlack,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Story Studio me Dekhein 🎬",
                                color = ComicBlack,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
