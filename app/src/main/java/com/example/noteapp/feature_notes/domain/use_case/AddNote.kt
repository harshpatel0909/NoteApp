package com.example.noteapp.feature_notes.domain.use_case

import com.example.noteapp.feature_notes.domain.model.InvalidNoteException
import com.example.noteapp.feature_notes.domain.model.Note
import com.example.noteapp.feature_notes.domain.repository.NoteRepository

class AddNote(private val noteRepository: NoteRepository) {
    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(note: Note) {
        if (note.title.isBlank()) throw InvalidNoteException("The title of the note can't be empty.")
        if (note.content.isBlank() && !note.isChecklist) throw InvalidNoteException("The content of the note can't be empty.")
        noteRepository.insertNote(note)
    }
}