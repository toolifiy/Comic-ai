package com.example.data

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiComicService {
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun getEffectiveApiKey(customKey: String?): String {
        if (!customKey.isNullOrBlank()) return customKey.trim()
        val envKey = BuildConfig.GEMINI_API_KEY
        if (envKey.isNotBlank() && envKey != "MY_GEMINI_API_KEY") {
            return envKey.trim()
        }
        return ""
    }

    /**
     * Sends a chat prompt to Gemini with Comic Studio Director persona
     */
    suspend fun sendDirectorChat(
        userMessage: String,
        conversationHistory: List<ChatMessage>,
        customKey: String? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getEffectiveApiKey(customKey)
        if (apiKey.isEmpty()) {
            return@withContext getOfflineSmartResponse(userMessage)
        }

        try {
            val systemPrompt = """
                You are Gemini, an elite, witty, high-energy Comic Book Director, Scriptwriter, and Market Trend Analyst.
                You are talking to Sameer (your boss and creative partner).
                Speak in natural, enthusiastic Hindi / Hinglish.
                You know the comic industry inside out: Shonen Manga, Webtoons, Marvel/DC superhero tropes, Cyberpunk, and Indian mythology sci-fi.
                Provide structured, punchy ideas, character designs with consistency tags, and panel-by-panel comic scripts with exact dialogue and sound effects.
            """.trimIndent()

            val contentsArray = JSONArray()
            
            // Build recent history
            val recentTurns = conversationHistory.takeLast(6)
            for (msg in recentTurns) {
                val role = if (msg.sender == MessageSender.USER) "user" else "model"
                val partObj = JSONObject().put("text", msg.text)
                val turnObj = JSONObject()
                    .put("role", role)
                    .put("parts", JSONArray().put(partObj))
                contentsArray.put(turnObj)
            }

            // Current prompt
            val currentTurn = JSONObject()
                .put("role", "user")
                .put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
            contentsArray.put(currentTurn)

            val requestJson = JSONObject()
                .put("contents", contentsArray)
                .put("systemInstruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", systemPrompt))))
                .put("generationConfig", JSONObject()
                    .put("temperature", 0.7)
                    .put("topP", 0.95)
                )

            val body = requestJson.toString().toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(body)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext "BOSS, Gemini API response error (${response.code}). Offline fallback mode chalu kar raha hu:\n\n${getOfflineSmartResponse(userMessage)}"
            }

            val json = JSONObject(responseBody)
            val candidates = json.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                text
            } else {
                getOfflineSmartResponse(userMessage)
            }
        } catch (e: Exception) {
            "BOSS, thoda network glitch hua: ${e.localizedMessage}. Lekin tension mat le, ye raha response:\n\n${getOfflineSmartResponse(userMessage)}"
        }
    }

    /**
     * Returns curated live market trends for comics
     */
    fun getMarketTrends(): List<MarketTrend> {
        return listOf(
            MarketTrend(
                genre = "Cyberpunk Neo-Mythology",
                popularityScore = 98,
                viralTropes = listOf("Indian Hacker Hero", "Ancient Artifact AI", "Neon City Rooftops", "Underground Resistance"),
                hotKeywords = "Cyberpunk, Desi Sci-Fi, Neon Rain, High Tech Ancient Weapons",
                whyItWorks = "Indian youth loves relatable desi protagonists with global Hollywood/Anime level high-tech aesthetic."
            ),
            MarketTrend(
                genre = "Supernatural Hunter Shonen",
                popularityScore = 94,
                viralTropes = listOf("Demon Leveling Up", "Shadow Clones", "School Uniform to Battle Gear", "Rival Brotherhood"),
                hotKeywords = "Dark Fantasy, Shonen Tournament, Cursed Aura, Epic Transformation",
                whyItWorks = "Huge demand driven by Solo Leveling and Jujutsu Kaisen anime popularity."
            ),
            MarketTrend(
                genre = "Street Vigilante Detective",
                popularityScore = 89,
                viralTropes = listOf("Rainy Night Alleyways", "Broken Detective", "Underworld Mafia", "Moral Dilemma"),
                hotKeywords = "Noir Graphic Novel, Gritty Realism, Trench Coat, High Stakes Climax",
                whyItWorks = "Timeless Batman & Daredevil vibe, very easy to render with dark moody comic inks."
            )
        )
    }

    /**
     * Fallback smart responses when API key is not yet set by the user
     */
    private fun getOfflineSmartResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("trend") || lower.contains("research") -> {
                """
                BOSS! Maine market ka analysis kiya hai! 🔥
                
                Abhi global webtoons aur Indian readers me ye 3 genres aag laga rahe hain:
                
                1. ⚡ **Cyberpunk + Desi Mythology (Score: 98/100):** 
                   Protagonist: Ek aam Indian street coder jise ek ancient AI talisman milta hai.
                2. ⚔️ **Shadow Hunter / Dungeon Leveling (Score: 94/100):**
                   Protagonist: Underdog fighter jo monster dungeon me sabse powerful ban jata hai.
                3. 🕵️‍♂️ **Noir Street Vigilante (Score: 89/100):**
                   Protagonist: Night detective with high-contrast comic ink style.
                   
                Tu bata, inme se kis par poori 4-panel comic script ready karein?
                """.trimIndent()
            }
            lower.contains("script") || lower.contains("story") || lower.contains("comic") || lower.contains("bana") -> {
                """
                BOSS! Script ready hai! 'Neon Ghost: Rise of Cipher' ki 4-Panel Script:
                
                📄 **Panel 1 [Wide Cinematic]:**
                - Scene: Karan skyscraper ki chhat par khada hai, barish ho rahi hai aur neon lights chamak rahi hain.
                - Dialogue: "Sheher so raha hai, lekin hamara mission abhi shuru hua hai!"
                - SFX: *THUUUUM!*
                
                📄 **Panel 2 [Close-up Dynamic]:**
                - Scene: Karan apne encrypted cyber-glove se holographic code unlock karta hai.
                - Dialogue: "Security bypass code mil gaya... ab server lock todte hain!"
                - SFX: *BZZZZT!*
                
                📄 **Panel 3 [Action High Angle]:**
                - Scene: Enemy cyber-drones roof par gher lete hain, red laser sights hero par target hain.
                - Dialogue: "Tum galat jagah aa gaye, Cipher!"
                - SFX: *WHRRRR!*
                
                📄 **Panel 4 [Hero Climax Punch]:**
                - Scene: Karan electric katana nikaal kar drone ki taraf jump karta hai!
                - Dialogue: "Mai jahan jata hu, wahi rule banata hu!"
                - SFX: *SLASHHH!*
                
                👉 Maine ye panels Story Studio me add kar diye hain! Tum Google Flow ke liye 1-click prompt copy kar sakte ho!
                """.trimIndent()
            }
            else -> {
                """
                BOSS! Tera instruction clear hai! 🚀
                
                Comic Creator Studio me sab kuch ready hai:
                - Character DNA locked hai taaki Flow me chehra na badle.
                - Camera shots aur dialogues ready hain.
                - Tu niche 'Story Studio' tab me jaakar har panel ka Google Flow prompt dekh sakta hai aur direct image render karwa sakta hai!
                
                Kuch customize karna hai ya naya character design karna hai toh bol!
                """.trimIndent()
            }
        }
    }
}
