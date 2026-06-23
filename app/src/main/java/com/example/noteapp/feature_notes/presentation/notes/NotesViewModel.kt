package com.example.noteapp.feature_notes.presentation.notes

import androidx.lifecycle.ViewModel
import com.example.noteapp.feature_notes.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases
) : ViewModel() {

}