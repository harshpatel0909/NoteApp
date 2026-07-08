package com.example.noteapp.feature_notes.presentation.notes.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.noteapp.feature_notes.domain.model.Note
import com.example.noteapp.ui.theme.AuroraTeal
import com.example.noteapp.ui.theme.DarkSurface
import com.example.noteapp.ui.theme.LightSurface
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskNotesSheet(
    notes: List<Note>,
    dark: Boolean,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var question by remember { mutableStateOf("") }
    var answer by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (dark) DarkSurface else LightSurface,
    ) {
        Column(Modifier
            .padding(20.dp)
            .padding(bottom = 24.dp)) {
            Text("Ask your notes", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))
            Row {
                OutlinedTextField(
                    value = question,
                    onValueChange = { question = it },
                    placeholder = { Text("What do I need for Lisbon?") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(13.dp),
                    singleLine = true,
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = {
                    if (question.isBlank()) return@IconButton
                    loading = true; answer = ""
                    scope.launch {
                        answer = localAsk(question, notes)
                        loading = false
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Ask", tint = AuroraTeal)
                }
            }
            if (loading) {
                Spacer(Modifier.height(16.dp)); LinearProgressIndicator(
                    Modifier.fillMaxWidth(),
                    color = AuroraTeal
                )
            }
            if (answer.isNotBlank()) {
                Spacer(Modifier.height(16.dp)); Text(
                    answer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun localAsk(q: String, notes: List<Note>): String {
    val words = q.lowercase().split(Regex("\\W+")).filter { it.length > 3 }
    val hits = notes.filter { n ->
        val blob = (n.title + " " + n.content + " " + n.tag + " " +
                n.checklistItems.joinToString(" ") { it.text }).lowercase()
        words.any { blob.contains(it) }
    }
    if (hits.isEmpty()) return "I couldn't find anything in your notes about that."
    return "From your notes: " + hits.joinToString(". ") { h ->
        h.title + if (h.content.isNotBlank()) " — ${h.content.take(80)}" else ""
    } + "."
}