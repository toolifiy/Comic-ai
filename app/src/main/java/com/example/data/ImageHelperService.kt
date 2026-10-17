package com.example.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object ImageHelperService {
    /**
     * Generates a direct URL for 100% free comic panel art preview
     */
    fun getFreePreviewImageUrl(prompt: String, width: Int = 800, height: Int = 1000): String {
        val sanitizedPrompt = "comic book style illustration, vibrant detailed comic panel, $prompt"
        val encoded = URLEncoder.encode(sanitizedPrompt, StandardCharsets.UTF_8.toString())
        return "https://image.pollinations.ai/prompt/$encoded?width=$width&height=$height&model=flux&nologo=true&seed=${prompt.hashCode()}"
    }

    /**
     * Copies prompt to device clipboard with user feedback
     */
    fun copyPromptToClipboard(context: Context, prompt: String, panelNumber: Int? = null) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val label = if (panelNumber != null) "Panel $panelNumber Flow Prompt" else "Character DNA Prompt"
        val clip = ClipData.newPlainText(label, prompt)
        clipboard.setPrimaryClip(clip)
        val msg = if (panelNumber != null) "Panel $panelNumber prompt copied! Google Flow me paste karo." else "Character DNA copied!"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * Launches Google Flow or Google AI Studio in browser/app
     */
    fun openGoogleFlow(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://labs.google/flow"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com"))
                fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(fallbackIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Browser open nahi ho saka: ${ex.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
