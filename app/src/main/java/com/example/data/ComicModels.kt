package com.example.data

import java.util.UUID

data class CharacterDna(
    val name: String = "Karan / Cipher",
    val alias: String = "The Neon Ghost",
    val age: String = "22",
    val facialTraits: String = "Sharp jawline, faint scar over left eyebrow, piercing hazel eyes",
    val hairStyle: String = "Messy black spikes with electric blue tips",
    val signatureOutfit: String = "Oversized crimson bomber jacket with cybernetic patches, dark grey cargo pants, fingerless tech gloves",
    val colorPalette: String = "Crimson Red, Cyber Cyan, Midnight Black",
    val artStyle: String = "Modern dynamic Shonen Manga meets Western graphic novel, heavy ink lines, cinematic lighting",
    val personality: String = "Witty, defiant, fiercely loyal street hacker"
) {
    /**
     * Creates an unbreakable prompt anchor to maintain character consistency across Google Flow generations
     */
    fun toFlowPromptAnchor(): String {
        return "Character: $name, $age years old, $facialTraits, $hairStyle. Wearing $signatureOutfit. Art Style: $artStyle, palette: $colorPalette. High quality comic book illustration, crisp ink lineart."
    }
}

data class ComicPanel(
    val id: String = UUID.randomUUID().toString(),
    val panelNumber: Int = 1,
    val cameraShot: String = "Wide Cinematic Shot",
    val description: String = "Hero standing on skyscraper roof overlooking neon-lit megacity in rain.",
    val characterAction: String = "Holding an encrypted holographic drive while wind blows jacket.",
    val dialogueText: String = "Sheher so raha hai, lekin hamara mission abhi shuru hua hai!",
    val speakerName: String = "Karan",
    val soundEffect: String = "THUUUUM!",
    val flowPrompt: String = "",
    val imageUrl: String? = null,
    val isGenerating: Boolean = false
) {
    /**
     * Builds the complete prompt specifically optimized for Google Flow & Generative Image AI
     */
    fun buildFlowPrompt(characterDna: CharacterDna): String {
        return "${characterDna.toFlowPromptAnchor()} Scene: $cameraShot. $description. Action: $characterAction. Dramatic comic panel composition, cinematic angle, octane render, 8k comic artwork, masterpiece."
    }
}

data class ComicProject(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Neon Chronicles: Street Hacker",
    val genre: String = "Cyberpunk Action Manga",
    val logline: String = "A fearless street coder discovers a secret AI protocol controlling the city's power grid.",
    val targetAudience: String = "Gen-Z & Action Comic Fans",
    val characterDna: CharacterDna = CharacterDna(),
    val panels: List<ComicPanel> = emptyList()
)

enum class MessageSender {
    USER,
    GEMINI,
    SYSTEM
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestedActions: List<String> = emptyList(),
    val generatedProject: ComicProject? = null
)

data class MarketTrend(
    val genre: String,
    val popularityScore: Int,
    val viralTropes: List<String>,
    val hotKeywords: String,
    val whyItWorks: String
)
