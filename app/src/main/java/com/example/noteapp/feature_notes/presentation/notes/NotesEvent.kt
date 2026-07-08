package com.example.noteapp.feature_notes.presentation.notes

import com.example.noteapp.feature_notes.domain.model.Note

sealed class NotesEvent {
    data class SetFilter(val filter: String) : NotesEvent()
    data class DeleteNote(val note: Note) : NotesEvent()
    object RestoreNote : NotesEvent()
    object ToggleGridView : NotesEvent()
    data class TogglePin(val note: Note) : NotesEvent()
    data class ToggleChecklistItem(val note: Note, val index: Int) : NotesEvent()
    object OrganizeWithAi : NotesEvent()
    object ToggleTheme : NotesEvent()
    data class ToggleNoteSelection(val noteId: Int) : NotesEvent()
    object ClearSelection : NotesEvent()
    object DeleteSelectedNotes : NotesEvent()
    data class ShowSingleDeleteDialog(val note: Note?) : NotesEvent()
    data class ShowBatchDeleteDialog(val show: Boolean) : NotesEvent()
}