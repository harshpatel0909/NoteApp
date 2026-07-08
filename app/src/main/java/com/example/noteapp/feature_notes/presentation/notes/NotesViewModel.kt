package com.example.noteapp.feature_notes.presentation.notes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.feature_notes.data.preferences.ThemePreferences
import com.example.noteapp.feature_notes.domain.ai.AiNoteAssistant
import com.example.noteapp.feature_notes.domain.model.Note
import com.example.noteapp.feature_notes.domain.use_case.NoteUseCases
import com.example.noteapp.feature_notes.domain.util.NoteOrder
import com.example.noteapp.feature_notes.domain.util.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val aiAssistant: AiNoteAssistant,
    private val themePreferences: ThemePreferences,
) : ViewModel() {

    var state by mutableStateOf(NotesState())
        private set

    private var recentlyDeleted: Note? = null

    init {
        noteUseCases.getNotes(NoteOrder.Date(OrderType.Descending))
            .onEach { notes -> state = state.copy(notes = notes) }
            .launchIn(viewModelScope)

        themePreferences.isDarkTheme
            .onEach { dark -> state = state.copy(isDarkTheme = dark) }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.ToggleNoteSelection -> {
                val currentSelected = state.selectedNoteIds
                val newSelected = if (currentSelected.contains(event.noteId)) {
                    currentSelected - event.noteId
                } else {
                    currentSelected + event.noteId
                }
                state = state.copy(
                    selectedNoteIds = newSelected,
                    isSelectionMode = newSelected.isNotEmpty()
                )
            }

            is NotesEvent.ClearSelection -> {
                state = state.copy(
                    selectedNoteIds = emptySet(),
                    isSelectionMode = false
                )
            }

            // SPECIFIC CHANGED LINE LOCATION: Added `showBatchDeleteDialog = false` to close dialog upon deletion
            is NotesEvent.DeleteSelectedNotes -> {
                viewModelScope.launch {
                    val notesToDelete =
                        state.notes.filter { it.id != null && state.selectedNoteIds.contains(it.id) }

                    notesToDelete.forEach { note ->
                        noteUseCases.deleteNote(note)
                    }

                    state = state.copy(
                        selectedNoteIds = emptySet(),
                        isSelectionMode = false,
                        showBatchDeleteDialog = false // <-- ADDED LINE
                    )
                }
            }

            // SPECIFIC ADDED LINE LOCATION: Added handling for single deletion dialog toggles
            is NotesEvent.ShowSingleDeleteDialog -> { // <-- ADDED BLOCK
                state = state.copy(
                    showSingleDeleteDialog = event.note != null,
                    notePendingDeletion = event.note
                )
            }

            // SPECIFIC ADDED LINE LOCATION: Added handling for batch deletion dialog toggles
            is NotesEvent.ShowBatchDeleteDialog -> { // <-- ADDED BLOCK
                state = state.copy(showBatchDeleteDialog = event.show)
            }

            is NotesEvent.SetFilter -> {
                state = state.copy(activeFilter = event.filter)
            }

            // SPECIFIC CHANGED LINE LOCATION: Updated to reset state flags when a note is safely processed out
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.note)
                    recentlyDeleted = event.note
                    state = state.copy(
                        showSingleDeleteDialog = false, // <-- ADDED LINE
                        notePendingDeletion = null       // <-- ADDED LINE
                    )
                }
            }

            NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    recentlyDeleted?.let { noteUseCases.addNote(it) }
                    recentlyDeleted = null
                }
            }

            NotesEvent.ToggleGridView -> {
                state = state.copy(isGridView = !state.isGridView)
            }

            is NotesEvent.TogglePin -> {
                viewModelScope.launch {
                    noteUseCases.toggleNotePin(event.note)
                }
            }

            is NotesEvent.ToggleChecklistItem -> {
                viewModelScope.launch {
                    noteUseCases.toggleChecklistItem(event.note, event.index)
                }
            }

            NotesEvent.OrganizeWithAi -> {
                viewModelScope.launch {
                    state = state.copy(isOrganizing = true)
                    val folders = runCatching {
                        aiAssistant.organizeIntoFolders(state.notes)
                    }.getOrDefault(emptyList())

                    folders.forEach { folder ->
                        folder.noteIds.forEach { id ->
                            state.notes.find { it.id == id }?.let {
                                noteUseCases.addNote(it.copy(folder = folder.name))
                            }
                        }
                    }
                    state = state.copy(
                        isOrganizing = false,
                        aiFolders = folders.map { it.name }
                    )
                }
            }

            NotesEvent.ToggleTheme -> {
                viewModelScope.launch {
                    val newDark = !state.isDarkTheme
                    state = state.copy(isDarkTheme = newDark)
                    themePreferences.setDarkTheme(newDark)
                }
            }
        }
    }

    fun visibleNotes(): List<Note> = state.notes
        .filter {
            state.activeFilter == "All" ||
                    it.tag == state.activeFilter ||
                    it.folder == state.activeFilter
        }
        .sortedByDescending { it.isPinned }
}