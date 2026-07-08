package com.example.noteapp.feature_notes.di

import com.example.noteapp.feature_notes.data.ai.GeminiNoteAssistant
import com.example.noteapp.feature_notes.domain.ai.AiNoteAssistant
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {
    @Binds
    @Singleton
    abstract fun bindAiNoteAssistant(impl: GeminiNoteAssistant): AiNoteAssistant
}