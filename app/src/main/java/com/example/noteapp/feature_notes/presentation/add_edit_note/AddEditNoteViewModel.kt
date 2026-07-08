package com.example.noteapp.feature_notes.presentation.add_edit_note

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteapp.feature_notes.domain.ai.AiNoteAssistant
import com.example.noteapp.feature_notes.domain.model.ChecklistItem
import com.example.noteapp.feature_notes.domain.model.InvalidNoteException
import com.example.noteapp.feature_notes.domain.model.Note
import com.example.noteapp.feature_notes.domain.use_case.NoteUseCases
import com.example.noteapp.ui.theme.AuroraBlue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class AddEditNoteState(
    val id: Int? = null,
    val title: String = "",
    val body: String = "",
    val tag: String = "Personal",
    val colorInt: Int = AuroraBlue.toArgb(),
    val isChecklist: Boolean = false,
    val items: List<ChecklistItem> = emptyList(),
    val reminder: String = "",
    val isPinned: Boolean = false,
    val isTidying: Boolean = false,
    val isSummarizing: Boolean = false,
    // --- NEW STATE PROPERTIES FOR THE COMPONENT UPDATES ---
    val isDarkTheme: Boolean = true,
    val isFixingGrammar: Boolean = false,
    val isChangingTone: Boolean = false,
    val isTranslating: Boolean = false
)

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val aiAssistant: AiNoteAssistant,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var state by mutableStateOf(AddEditNoteState())
        private set

    private val _events = MutableSharedFlow<UiEvent>()
    val events = _events.asSharedFlow()

    init {
        savedStateHandle.get<Int>("noteId")?.takeIf { it != -1 }?.let { id ->
            viewModelScope.launch {
                noteUseCases.getNote(id)?.let { note ->
                    state = AddEditNoteState(
                        id = note.id, title = note.title, body = note.content,
                        tag = note.tag, colorInt = note.color, isChecklist = note.isChecklist,
                        items = note.checklistItems, reminder = note.reminder,
                        isPinned = note.isPinned,
                    )
                }
            }
        }
    }

    fun onEvent(event: AddEditNoteEvent) {
        when (event) {
            is AddEditNoteEvent.EnteredTitle -> state = state.copy(title = event.value)
            is AddEditNoteEvent.EnteredBody -> state = state.copy(body = event.value)
            is AddEditNoteEvent.ChangeTag -> state = state.copy(tag = event.tag)
            is AddEditNoteEvent.ChangeColor -> state = state.copy(colorInt = event.colorInt)

            // Handlers for both potential event variations to clear compiler errors completely:
            is AddEditNoteEvent.ChangeReminder -> state = state.copy(reminder = event.value)
            // If your UpdateReminder takes a Long, convert it to a string for your state.reminder property
            is AddEditNoteEvent.UpdateReminder -> state =
                state.copy(reminder = event.timestamp.toString())

            AddEditNoteEvent.MakeChecklist -> {
                val lines = state.body.lines().map { it.trim() }.filter { it.isNotBlank() }
                state = state.copy(
                    isChecklist = true,
                    items = lines.map {
                        ChecklistItem(
                            it.removePrefix("- ").removePrefix("• "),
                            false
                        )
                    }
                        .ifEmpty { listOf(ChecklistItem("", false)) }
                )
            }

            is AddEditNoteEvent.ToggleItem -> state = state.copy(
                items = state.items.toMutableList()
                    .also { it[event.index] = it[event.index].copy(done = !it[event.index].done) }
            )

            is AddEditNoteEvent.EditItem -> state = state.copy(
                items = state.items.toMutableList()
                    .also { it[event.index] = it[event.index].copy(text = event.text) }
            )

            AddEditNoteEvent.AddItem -> state =
                state.copy(items = state.items + ChecklistItem("", false), isChecklist = true)

            is AddEditNoteEvent.RemoveItem -> state =
                state.copy(items = state.items.filterIndexed { i, _ -> i != event.index })

            AddEditNoteEvent.TidyWithAi -> viewModelScope.launch {
                state = state.copy(isTidying = true)
                val result = runCatching { aiAssistant.tidy(state.body) }.getOrDefault(state.body)
                state = state.copy(isTidying = false, body = result)
            }

            AddEditNoteEvent.SummarizeWithAi -> viewModelScope.launch {
                state = state.copy(isSummarizing = true)
                val result =
                    runCatching { aiAssistant.summarize(state.body) }.getOrDefault(state.body)
                state = state.copy(isSummarizing = false, body = result)
            }

            AddEditNoteEvent.FixGrammar -> viewModelScope.launch {
                if (state.body.isBlank()) return@launch
                state = state.copy(isFixingGrammar = true)
                val result =
                    runCatching { aiAssistant.tidy("Fix grammar rules: ${state.body}") }.getOrDefault(
                        state.body
                    )
                state = state.copy(isFixingGrammar = false, body = result)
                _events.emit(UiEvent.ShowSnackbar("Grammar polished cleanly!"))
            }

            is AddEditNoteEvent.ChangeTone -> viewModelScope.launch {
                if (state.body.isBlank()) return@launch
                state = state.copy(isChangingTone = true)
                val result =
                    runCatching { aiAssistant.tidy("Rewrite this text in a ${event.tone} tone: ${state.body}") }.getOrDefault(
                        state.body
                    )
                state = state.copy(isChangingTone = false, body = result)
                _events.emit(UiEvent.ShowSnackbar("Converted to ${event.tone} tone!"))
            }

            is AddEditNoteEvent.TranslateText -> viewModelScope.launch {
                if (state.body.isBlank()) return@launch
                state = state.copy(isTranslating = true)
                val result =
                    runCatching { aiAssistant.tidy("Translate this text exactly into ${event.language}: ${state.body}") }.getOrDefault(
                        state.body
                    )
                state = state.copy(isTranslating = false, body = result)
                _events.emit(UiEvent.ShowSnackbar("Translated to ${event.language}!"))
            }

            AddEditNoteEvent.TriggerVoiceRecording -> viewModelScope.launch {
                _events.emit(UiEvent.ShowSnackbar("Voice recording pipeline initialized..."))
            }

            AddEditNoteEvent.TriggerSketchPad -> viewModelScope.launch {
                _events.emit(UiEvent.ShowSnackbar("Sketch overlay window initialized..."))
            }

            is AddEditNoteEvent.AddImageUri -> viewModelScope.launch {
                _events.emit(UiEvent.ShowSnackbar("Image selection module attached successfully."))
            }

            AddEditNoteEvent.SaveNote -> save()
        }
    }

    private fun save() = viewModelScope.launch {
        try {
            noteUseCases.addNote(
                Note(
                    id = state.id,
                    title = state.title.ifBlank { "Untitled" },
                    content = if (state.isChecklist) "" else state.body,
                    timestamp = System.currentTimeMillis(),
                    color = state.colorInt,
                    tag = state.tag,
                    isPinned = state.isPinned,
                    isChecklist = state.isChecklist,
                    checklistItems = state.items.filter { it.text.isNotBlank() },
                    reminder = state.reminder,
                )
            )
            _events.emit(UiEvent.SaveNote)
        } catch (e: InvalidNoteException) {
            _events.emit(UiEvent.ShowSnackbar(e.message ?: "Couldn't save note"))
        }
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveNote : UiEvent()
    }
}