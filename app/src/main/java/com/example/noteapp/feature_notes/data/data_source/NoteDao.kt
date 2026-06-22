package com.example.noteapp.feature_notes.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.noteapp.feature_notes.domain.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    /*automatically refresh when a note is added or deleted*/
    @Query("SELECT * FROM note")
    fun getNotes(): Flow<List<Note>>

    /*find a specific note*/
    @Query("SELECT * FROM note WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}
