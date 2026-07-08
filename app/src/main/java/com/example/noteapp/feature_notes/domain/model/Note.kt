package com.example.noteapp.feature_notes.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.noteapp.feature_notes.data.data_source.ChecklistConverter
import com.example.noteapp.ui.theme.*

@Entity(tableName = "note")
@TypeConverters(ChecklistConverter::class)
data class Note(
    val title: String,
    val content: String,
    val timestamp: Long,
    val color: Int,
    val tag: String = "Personal",
    val isPinned: Boolean = false,
    val isChecklist: Boolean = false,
    val checklistItems: List<ChecklistItem> = emptyList(),
    val reminder: String = "",
    val folder: String? = null,
    @PrimaryKey val id: Int? = null
) {
    companion object {
        val noteColors =
            listOf(AuroraBlue, AuroraAmber, AuroraGreen, AuroraRose, AuroraViolet, AuroraTeal)
        val tagOptions = listOf("Work", "Personal", "Travel", "Ideas", "Books", "Health")
    }
}

data class ChecklistItem(val text: String, val done: Boolean = false)

class InvalidNoteException(message: String) : Exception(message)