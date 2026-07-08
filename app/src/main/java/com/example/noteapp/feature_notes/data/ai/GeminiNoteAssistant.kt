package com.example.noteapp.feature_notes.data.ai

import com.example.noteapp.feature_notes.domain.ai.AiNoteAssistant
import com.example.noteapp.feature_notes.domain.ai.CapturedNoteDraft
import com.example.noteapp.feature_notes.domain.ai.FolderSuggestion
import com.example.noteapp.feature_notes.domain.model.ChecklistItem
import com.example.noteapp.feature_notes.domain.model.Note
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

class GeminiNoteAssistant @Inject constructor() : AiNoteAssistant {

    private val model by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AQ.Ab8RN6JsEgX6cjznl3mRadwfBam0Q3G9Gnbc0Q0BmRugD_qc4w",
            generationConfig = generationConfig { temperature = 0.4f }
        )
    }
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    override suspend fun tidy(text: String): String {
        val prompt =
            "Rewrite this note text so it is clear and well organized. Keep the meaning and any list structure. Return ONLY the rewritten text, no preamble, no markdown fences.\n\n$text"
        return try {
            model.generateContent(prompt).text?.trim() ?: text
        } catch (e: Exception) {
            text.lines().map { it.trim() }.filter { it.isNotBlank() }
                .joinToString("\n") { it.replaceFirstChar { c -> c.uppercase() } }
        }
    }

    override suspend fun summarize(text: String): String {
        val prompt =
            "Summarize this note in up to 3 short bullet points. Each line must start with \"• \". Return ONLY the bullets.\n\n$text"
        return try {
            model.generateContent(prompt).text?.trim() ?: text
        } catch (e: Exception) {
            text.split(Regex("(?<=[.!?])\\s+")).take(3).joinToString("\n") { "• ${it.trim()}" }
        }
    }

    override suspend fun structureCapture(rawText: String): CapturedNoteDraft {
        val prompt =
            """You convert a raw brain-dump into ONE structured note. Respond with ONLY valid JSON, no markdown fences. Schema:
{"title": string (max 6 words), "tag": one of ["Work","Personal","Travel","Ideas","Books","Health"], "color": one of ["blue","amber","green","rose","violet","teal"], "isList": boolean, "items": [{"t": string, "done": false}] (only if isList else []), "reminder": string like "Tomorrow · 9:00 AM" or "", "snippet": one-line summary if not a list else ""}

Raw text:
$rawText"""
        return try {
            val raw = model.generateContent(prompt).text.orEmpty()
            val obj = extractJsonObject(raw) ?: return fallbackCapture(rawText)
            CapturedNoteDraft(
                title = obj["title"]?.jsonPrimitive?.content ?: "Untitled",
                tag = obj["tag"]?.jsonPrimitive?.content ?: "Ideas",
                colorName = obj["color"]?.jsonPrimitive?.content ?: "blue",
                isChecklist = obj["isList"]?.jsonPrimitive?.boolean ?: false,
                items = obj["items"]?.jsonArray?.map {
                    ChecklistItem(it.jsonObject["t"]?.jsonPrimitive?.content ?: "", false)
                } ?: emptyList(),
                reminder = obj["reminder"]?.jsonPrimitive?.content ?: "",
                snippet = obj["snippet"]?.jsonPrimitive?.content ?: "",
            )
        } catch (e: Exception) {
            fallbackCapture(rawText)
        }
    }

    override suspend fun ask(question: String, notes: List<Note>): String {
        val context = notes.joinToString("\n") { n ->
            val body = if (n.isChecklist) n.checklistItems.joinToString("; ") {
                (if (it.done) "[x] " else "[ ] ") + it.text
            } else n.content
            "- (${n.tag}) ${n.title}: $body"
        }
        val prompt =
            "You are a helpful assistant answering questions about the user's personal notes. Use ONLY the notes provided. Answer in 2-3 concise, friendly, specific sentences. If nothing matches, say so.\n\nNOTES:\n$context\n\nQUESTION: $question"
        return try {
            model.generateContent(prompt).text?.trim()
                ?: "I couldn't reach the AI — check your connection."
        } catch (e: Exception) {
            val words = question.lowercase().split(Regex("\\W+")).filter { it.length > 3 }
            val hits = notes.filter { n ->
                val blob =
                    "${n.title} ${n.content} ${n.tag}".lowercase(); words.any { blob.contains(it) }
            }
            if (hits.isEmpty()) "I couldn't find anything matching that in your notes."
            else "From your notes: " + hits.joinToString(". ") {
                it.title + " — " + it.content.take(
                    80
                )
            }
        }
    }

    override suspend fun organizeIntoFolders(notes: List<Note>): List<FolderSuggestion> {
        val list = notes.map { """{"id":${it.id},"title":"${it.title}","tag":"${it.tag}"}""" }
        val prompt =
            "Group these notes into 2 to 4 thematic folders. Respond with ONLY valid JSON, no markdown. Schema: {\"folders\":[{\"name\": string (1-2 words), \"noteIds\": number[]}]}\n\nNotes: [${
                list.joinToString(",")
            }]"
        return try {
            val raw = model.generateContent(prompt).text.orEmpty()
            val obj = extractJsonObject(raw) ?: return fallbackFolders(notes)
            val folders = obj["folders"]?.jsonArray ?: return fallbackFolders(notes)
            folders.map {
                val o = it.jsonObject
                FolderSuggestion(
                    name = o["name"]?.jsonPrimitive?.content ?: "Folder",
                    noteIds = o["noteIds"]?.jsonArray?.mapNotNull { id -> id.jsonPrimitive.content.toIntOrNull() }
                        ?: emptyList()
                )
            }
        } catch (e: Exception) {
            fallbackFolders(notes)
        }
    }

    private fun extractJsonObject(raw: String): JsonObject? {
        val start = raw.indexOf('{');
        val end = raw.lastIndexOf('}')
        if (start < 0 || end < 0) return null
        return runCatching {
            json.parseToJsonElement(
                raw.substring(
                    start,
                    end + 1
                )
            ).jsonObject
        }.getOrNull()
    }

    private fun fallbackCapture(text: String): CapturedNoteDraft {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        val title = (lines.firstOrNull() ?: "New note").take(42)
        val isList =
            lines.size > 1 || Regex("(?i)\\b(buy|todo|task|call|email|book|write|post)\\b").containsMatchIn(
                text
            )
        val items = if (isList) (if (lines.size > 1) lines.drop(1) else lines).map {
            ChecklistItem(
                it.removePrefix("- ").removePrefix("• "),
                false
            )
        } else emptyList()
        val tag = when {
            Regex("(?i)work|roadmap|ship|launch|client|meeting").containsMatchIn(text) -> "Work"
            Regex("(?i)buy|grocery|store|shop").containsMatchIn(text) -> "Personal"
            Regex("(?i)flight|trip|travel|hotel").containsMatchIn(text) -> "Travel"
            Regex("(?i)read|book|novel").containsMatchIn(text) -> "Books"
            Regex("(?i)gym|workout|health").containsMatchIn(text) -> "Health"
            else -> "Ideas"
        }
        return CapturedNoteDraft(
            title,
            tag,
            "blue",
            isList,
            items,
            "",
            if (isList) "" else lines.joinToString(" ").take(100)
        )
    }

    private fun fallbackFolders(notes: List<Note>): List<FolderSuggestion> {
        val byTag = mutableMapOf<String, MutableList<Int>>()
        notes.forEach { n -> byTag.getOrPut(n.tag) { mutableListOf() }.add(n.id ?: return@forEach) }
        val names = mapOf(
            "Work" to "Focus",
            "Personal" to "Life",
            "Travel" to "Adventures",
            "Ideas" to "Spark",
            "Books" to "Library",
            "Health" to "Wellness"
        )
        return byTag.entries.take(4).map { (tag, ids) ->
            FolderSuggestion(
                name = names[tag] ?: tag,
                noteIds = ids
            )
        }
    }
}