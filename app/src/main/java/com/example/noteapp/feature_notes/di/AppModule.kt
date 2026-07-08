package com.example.noteapp.feature_notes.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.noteapp.feature_notes.data.data_source.NoteDatabase
import com.example.noteapp.feature_notes.data.repository.NoteRepositoryImpl
import com.example.noteapp.feature_notes.domain.repository.NoteRepository
import com.example.noteapp.feature_notes.domain.use_case.AddNote
import com.example.noteapp.feature_notes.domain.use_case.DeleteNote
import com.example.noteapp.feature_notes.domain.use_case.GetNote
import com.example.noteapp.feature_notes.domain.use_case.GetNotes
import com.example.noteapp.feature_notes.domain.use_case.NoteUseCases
import com.example.noteapp.feature_notes.domain.use_case.ToggleChecklistItem
import com.example.noteapp.feature_notes.domain.use_case.ToggleNotePin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE note ADD COLUMN tag TEXT NOT NULL DEFAULT 'Personal'")
        db.execSQL("ALTER TABLE note ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE note ADD COLUMN isChecklist INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE note ADD COLUMN checklistItems TEXT NOT NULL DEFAULT '[]'")
        db.execSQL("ALTER TABLE note ADD COLUMN reminder TEXT NOT NULL DEFAULT ''")
        db.execSQL("ALTER TABLE note ADD COLUMN folder TEXT")
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(app, NoteDatabase::class.java, NoteDatabase.DATABASE_NAME)
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db: NoteDatabase): NoteRepository {
        return NoteRepositoryImpl(db.noteDao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository),
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository),
            toggleNotePin = ToggleNotePin(repository),
            toggleChecklistItem = ToggleChecklistItem(repository),
        )
    }
}