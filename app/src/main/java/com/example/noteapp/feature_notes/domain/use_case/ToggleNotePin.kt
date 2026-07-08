package com.example.noteapp.feature_notes.domain.use_case

import com.example.noteapp.feature_notes.domain.model.Note
import com.example.noteapp.feature_notes.domain.repository.NoteRepository

class ToggleNotePin(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) {
        repository.insertNote(note.copy(isPinned = !note.isPinned))
    }
}