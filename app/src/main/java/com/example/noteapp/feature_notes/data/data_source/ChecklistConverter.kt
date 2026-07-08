package com.example.noteapp.feature_notes.data.data_source

import androidx.room.TypeConverter
import com.example.noteapp.feature_notes.domain.model.ChecklistItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
private data class ChecklistItemDto(val text: String, val done: Boolean)

class ChecklistConverter {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromList(items: List<ChecklistItem>): String =
        json.encodeToString(items.map { ChecklistItemDto(it.text, it.done) })

    @TypeConverter
    fun toList(raw: String): List<ChecklistItem> =
        if (raw.isBlank()) emptyList()
        else json.decodeFromString<List<ChecklistItemDto>>(raw)
            .map { ChecklistItem(it.text, it.done) }
}