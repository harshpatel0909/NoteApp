package com.example.noteapp.feature_notes.domain.ai

import com.example.noteapp.feature_notes.domain.model.ChecklistItem
import com.example.noteapp.feature_notes.domain.model.Note

data class CapturedNoteDraft(
    val title: String,
    val tag: String,
    val colorName: String,
    val isChecklist: Boolean,
    val items: List<ChecklistItem>,
    val reminder: String,
    val snippet: String,
)

data class FolderSuggestion(val name: String, val noteIds: List<Int>)

interface AiNoteAssistant {
    suspend fun tidy(text: String): String
    suspend fun summarize(text: String): String
    suspend fun structureCapture(rawText: String): CapturedNoteDraft
    suspend fun ask(question: String, notes: List<Note>): String
    suspend fun organizeIntoFolders(notes: List<Note>): List<FolderSuggestion>
}