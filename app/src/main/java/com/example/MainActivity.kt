package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.data.*
import com.example.ui.screens.ComicReaderScreen
import com.example.ui.screens.ComicStudioScreen
import com.example.ui.screens.DirectorChatScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class ScreenTab {
    DIRECTOR_CHAT,
    STORY_STUDIO,
    COMIC_READER
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var currentTab by remember { mutableStateOf(ScreenTab.DIRECTOR_CHAT) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var customApiKey by remember { mutableStateOf("") }
    var isGeneratingChat by remember { mutableStateOf(false) }

    // Initial default character DNA
    val initialDna = remember {
        CharacterDna(
            name = "Karan / Cipher",
            alias = "The Neon Ghost",
            age = "22",
            facialTraits = "Sharp jawline, faint scar over left eyebrow, piercing hazel eyes",
            hairStyle = "Messy black spikes with electric cyan tips",
            signatureOutfit = "Crimson red high-collar biker jacket with cybernetic shoulder patches, dark tactical cargo pants, fingerless gloves",
            colorPalette = "Crimson Red, Cyber Cyan, Midnight Black",
            artStyle = "Modern high-octane Webtoon comic art, bold dynamic ink lineart, cinematic rain lighting",
            personality = "Witty, defiant street hacker fighting corruption"
        )
    }

    // Default rich starter comic project
    var project by remember {
        mutableStateOf(
            ComicProject(
                title = "Neon Ghost: Rise of Cipher",
                genre = "Cyberpunk Action Manga",
                logline = "A young street hacker in Neo-Mumbai discovers an ancient AI talisman hidden in the city power grid.",
                characterDna = initialDna,
                panels = listOf(
                    ComicPanel(
                        panelNumber = 1,
                        cameraShot = "Wide Cinematic Dutch Angle",
                        description = "Karan stands at the edge of a skyscraper roof in torrential rain, looking down at neon holographic billboards.",
                        characterAction = "Adjusting his glowing cyber-glove while rain drips from his messy hair.",
                        dialogueText = "Sheher so raha hai, lekin hamara mission abhi shuru hua hai!",
                        speakerName = "Karan",
                        soundEffect = "THUUUUM!",
                        imageUrl = ImageHelperService.getFreePreviewImageUrl("young Indian cyberpunk hero in crimson red jacket on skyscraper roof in rain neon city night")
                    ),
                    ComicPanel(
                        panelNumber = 2,
                        cameraShot = "Extreme Close-Up",
                        description = "Holographic code reflections in Karan's eyes as an encrypted AI orb activates.",
                        characterAction = "Typing mid-air on blue holographic keyboard.",
                        dialogueText = "Security bypass code mil gaya... ab server lock todte hain!",
                        speakerName = "Karan",
                        soundEffect = "BZZZZZT!",
                        imageUrl = ImageHelperService.getFreePreviewImageUrl("close up young cyberpunk hero eyes glowing with blue holographic code interface reflections")
                    ),
                    ComicPanel(
                        panelNumber = 3,
                        cameraShot = "High Angle Tension Shot",
                        description = "Three armed security cyber-drones hover above the roof, targeting red laser sights at Karan.",
                        characterAction = "Karan smirks defyingly as red laser dots converge on his chest.",
                        dialogueText = "Late ho gaye doston, transmission complete ho chuki hai!",
                        speakerName = "Karan",
                        soundEffect = "WHRRRRR!",
                        imageUrl = ImageHelperService.getFreePreviewImageUrl("sci-fi combat drones with red laser sights aiming at lone superhero on neon rooftop")
                    ),
                    ComicPanel(
                        panelNumber = 4,
                        cameraShot = "Full Page Action Climax",
                        description = "Karan leaps forward with an electric energy blade slicing through the lead drone in a shower of sparks.",
                        characterAction = "Dynamic mid-air jump slash, sparks and blue lightning exploding everywhere.",
                        dialogueText = "Mai jahan jata hu, wahi rule banata hu!",
                        speakerName = "Karan",
                        soundEffect = "SLASHHH!",
                        imageUrl = ImageHelperService.getFreePreviewImageUrl("superhero leaping dynamic action strike cutting robot drone sparks explosion manga style")
                    )
                )
            )
        )
    }

    // Chat history
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    sender = MessageSender.GEMINI,
                    text = "BOSS! Swagat hai ComixFlow AI Studio me! 💥\n\nMai hu tumhara AI Comic Director. Mai market research se lekar character design, dialogue script aur Google Flow prompts sab sambhalunga!\n\nBolo BOSS, kis type ki comic shuru karein? Niche quick buttons tap karo ya seedha bolo!",
                    suggestedActions = listOf("Market Trends", "Generate Script", "Lock DNA")
                )
            )
        )
    }

    // Function to send message to Gemini
    fun handleSendMessage(prompt: String) {
        val userMsg = ChatMessage(sender = MessageSender.USER, text = prompt)
        chatMessages = chatMessages + userMsg
        isGeneratingChat = true

        coroutineScope.launch {
            val responseText = GeminiComicService.sendDirectorChat(
                userMessage = prompt,
                conversationHistory = chatMessages,
                customKey = customApiKey
            )
            chatMessages = chatMessages + ChatMessage(sender = MessageSender.GEMINI, text = responseText)
            isGeneratingChat = false
        }
    }

    // Function to render preview for a specific panel
    fun handleRenderPanel(targetPanel: ComicPanel) {
        // Set generating state
        project = project.copy(
            panels = project.panels.map {
                if (it.id == targetPanel.id) it.copy(isGenerating = true) else it
            }
        )

        coroutineScope.launch {
            kotlinx.coroutines.delay(1000) // smooth visual transition
            val newUrl = ImageHelperService.getFreePreviewImageUrl(
                "${project.characterDna.artStyle}, ${targetPanel.description}, ${project.characterDna.signatureOutfit}"
            )
            project = project.copy(
                panels = project.panels.map {
                    if (it.id == targetPanel.id) it.copy(imageUrl = newUrl, isGenerating = false) else it
                }
            )
        }
    }

    // Function to add a new panel
    fun handleAddPanel() {
        val nextNumber = project.panels.size + 1
        val newPanel = ComicPanel(
            panelNumber = nextNumber,
            cameraShot = "Dynamic Action Angle",
            description = "Next thrilling chapter moment for Karan in the story.",
            characterAction = "Stepping through the shattered doorway into the mainframe room.",
            dialogueText = "Ab aayega asli maza!",
            speakerName = "Karan",
            soundEffect = "CRASSHH!",
            imageUrl = null
        )
        project = project.copy(panels = project.panels + newPanel)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                            color = ComicYellow,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = "AI",
                                color = ComicBlack,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "ComixFlow Studio",
                            color = ComicTextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                },
                actions = {
                    // Quick Google Flow Button
                    IconButton(
                        onClick = { ImageHelperService.openGoogleFlow(context) },
                        modifier = Modifier.testTag("top_bar_open_flow")
                    ) {
                        Icon(
                            Icons.Default.OpenInBrowser,
                            contentDescription = "Open Google Flow",
                            tint = ComicOrange
                        )
                    }

                    // API Key Settings Button
                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("top_bar_settings_button")
                    ) {
                        Icon(
                            Icons.Default.VpnKey,
                            contentDescription = "Settings",
                            tint = ComicYellow
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ComicDarkSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = ComicDarkSurface,
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == ScreenTab.DIRECTOR_CHAT,
                    onClick = { currentTab = ScreenTab.DIRECTOR_CHAT },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "AI Director") },
                    label = { Text("AI Director", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ComicBlack,
                        selectedTextColor = ComicYellow,
                        indicatorColor = ComicYellow,
                        unselectedIconColor = ComicTextSecondary,
                        unselectedTextColor = ComicTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_director_chat")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.STORY_STUDIO,
                    onClick = { currentTab = ScreenTab.STORY_STUDIO },
                    icon = { Icon(Icons.Default.MovieFilter, contentDescription = "Story Studio") },
                    label = { Text("Story Studio", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ComicBlack,
                        selectedTextColor = ComicYellow,
                        indicatorColor = ComicYellow,
                        unselectedIconColor = ComicTextSecondary,
                        unselectedTextColor = ComicTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_story_studio")
                )

                NavigationBarItem(
                    selected = currentTab == ScreenTab.COMIC_READER,
                    onClick = { currentTab = ScreenTab.COMIC_READER },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Comic Reader") },
                    label = { Text("Reader", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ComicBlack,
                        selectedTextColor = ComicYellow,
                        indicatorColor = ComicYellow,
                        unselectedIconColor = ComicTextSecondary,
                        unselectedTextColor = ComicTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_comic_reader")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ScreenTab.DIRECTOR_CHAT -> {
                    DirectorChatScreen(
                        messages = chatMessages,
                        isGenerating = isGeneratingChat,
                        onSendMessage = { prompt -> handleSendMessage(prompt) },
                        onQuickPromptSelected = { chipText -> handleSendMessage(chipText) },
                        onLoadScriptToStudio = { currentTab = ScreenTab.STORY_STUDIO }
                    )
                }
                ScreenTab.STORY_STUDIO -> {
                    ComicStudioScreen(
                        project = project,
                        onRenderPanel = { panel -> handleRenderPanel(panel) },
                        onAddPanel = { handleAddPanel() }
                    )
                }
                ScreenTab.COMIC_READER -> {
                    ComicReaderScreen(
                        project = project
                    )
                }
            }

            if (showSettingsDialog) {
                SettingsDialog(
                    currentApiKey = customApiKey,
                    onSaveApiKey = { newKey -> customApiKey = newKey },
                    onDismiss = { showSettingsDialog = false }
                )
            }
        }
    }
}
