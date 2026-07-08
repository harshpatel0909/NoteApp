package com.example.noteapp.feature_notes.presentation.add_edit_note

sealed class AddEditNoteEvent {
    data class EnteredTitle(val value: String) : AddEditNoteEvent()
    data class EnteredBody(val value: String) : AddEditNoteEvent()
    data class ChangeTag(val tag: String) : AddEditNoteEvent()
    data class ChangeColor(val colorInt: Int) : AddEditNoteEvent()
    data class ChangeReminder(val value: String) : AddEditNoteEvent()
    object MakeChecklist : AddEditNoteEvent()
    data class ToggleItem(val index: Int) : AddEditNoteEvent()
    data class EditItem(val index: Int, val text: String) : AddEditNoteEvent()
    object AddItem : AddEditNoteEvent()
    data class RemoveItem(val index: Int) : AddEditNoteEvent()
    object TidyWithAi : AddEditNoteEvent()
    object SummarizeWithAi : AddEditNoteEvent()
    object SaveNote : AddEditNoteEvent()
    object FixGrammar : AddEditNoteEvent()
    data class ChangeTone(val tone: String) : AddEditNoteEvent()
    data class TranslateText(val language: String) : AddEditNoteEvent()
    data class UpdateReminder(val timestamp: Long) : AddEditNoteEvent()
    object TriggerVoiceRecording : AddEditNoteEvent()
    object TriggerSketchPad : AddEditNoteEvent()
    data class AddImageUri(val uri: String) : AddEditNoteEvent()

}