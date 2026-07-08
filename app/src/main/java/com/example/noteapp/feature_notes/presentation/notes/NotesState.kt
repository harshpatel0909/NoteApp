package com.example.noteapp.feature_notes.presentation.notes

import com.example.noteapp.feature_notes.domain.model.Note

data class NotesState(
    val notes: List<Note> = emptyList(),
    val activeFilter: String = "All",
    val aiFolders: List<String> = emptyList(),
    val isGridView: Boolean = true,
    val isDarkTheme: Boolean = true,
    val isOrganizing: Boolean = false,
    val selectedNoteIds: Set<Int> = emptySet(),
    val isSelectionMode: Boolean = false,
    val showSingleDeleteDialog: Boolean = false,
    val showBatchDeleteDialog: Boolean = false,
    val notePendingDeletion: Note? = null
)