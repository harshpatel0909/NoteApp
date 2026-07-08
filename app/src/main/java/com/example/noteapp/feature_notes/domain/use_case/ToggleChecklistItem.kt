package com.example.noteapp.feature_notes.domain.use_case

import com.example.noteapp.feature_notes.domain.model.Note
import com.example.noteapp.feature_notes.domain.repository.NoteRepository

class ToggleChecklistItem(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note, itemIndex: Int) {
        val updated = note.checklistItems.toMutableList().also { list ->
            val item = list[itemIndex]
            list[itemIndex] = item.copy(done = !item.done)
        }
        repository.insertNote(note.copy(checklistItems = updated))
    }
}