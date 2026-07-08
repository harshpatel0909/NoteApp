package com.example.noteapp.feature_notes.presentation.capture

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.noteapp.feature_notes.domain.ai.AiNoteAssistant
import com.example.noteapp.feature_notes.domain.ai.CapturedNoteDraft
import com.example.noteapp.feature_notes.domain.model.Note
import com.example.noteapp.feature_notes.domain.use_case.NoteUseCases
import com.example.noteapp.ui.theme.AuroraAmber
import com.example.noteapp.ui.theme.AuroraBlue
import com.example.noteapp.ui.theme.AuroraGreen
import com.example.noteapp.ui.theme.AuroraRose
import com.example.noteapp.ui.theme.AuroraTeal
import com.example.noteapp.ui.theme.AuroraViolet
import com.example.noteapp.ui.theme.DarkTextMuted
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

data class CaptureState(
    val text: String = "",
    val loading: Boolean = false,
    val result: CapturedNoteDraft? = null,
)

@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val aiAssistant: AiNoteAssistant,
    private val noteUseCases: NoteUseCases,
) : ViewModel() {

    var state by mutableStateOf(CaptureState())
        private set

    fun setText(text: String) {
        state = state.copy(text = text)
    }

    fun structure() = viewModelScope.launch {
        if (state.text.isBlank()) return@launch
        state = state.copy(loading = true, result = null)
        val draft = runCatching { aiAssistant.structureCapture(state.text) }.getOrNull()
        state = state.copy(loading = false, result = draft)
    }

    fun save(onSaved: () -> Unit) = viewModelScope.launch {
        val r = state.result ?: return@launch
        val colorInt = colorForName(r.colorName).toArgb()
        noteUseCases.addNote(
            Note(
                title = r.title, content = r.snippet,
                timestamp = System.currentTimeMillis(), color = colorInt,
                tag = r.tag, isChecklist = r.isChecklist,
                checklistItems = r.items, reminder = r.reminder,
            )
        )
        onSaved()
    }

    private fun colorForName(name: String) = when (name) {
        "amber" -> AuroraAmber; "green" -> AuroraGreen; "rose" -> AuroraRose
        "violet" -> AuroraViolet; "teal" -> AuroraTeal; else -> AuroraBlue
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureScreen(navController: NavController, viewModel: CaptureViewModel = hiltViewModel()) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Icon(
                            Icons.Default.AutoAwesome,
                            null,
                            tint = AuroraTeal,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Smart capture")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(20.dp)) {
            Text(
                "Dump anything — AI turns it into a structured note",
                style = MaterialTheme.typography.bodySmall,
                color = DarkTextMuted
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.text, onValueChange = viewModel::setText,
                placeholder = { Text("e.g. Plan launch — write changelog, email beta list, post Friday 3pm…") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp),
                shape = RoundedCornerShape(16.dp),
            )
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { viewModel.structure() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AuroraTeal),
            ) {
                if (state.loading) {
                    CircularProgressIndicator(
                        Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp)); Text("Structuring…", color = Color.White)
                } else {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp)); Text("Structure with AI", color = Color.White)
                }
            }

            state.result?.let { r ->
                Spacer(Modifier.height(20.dp))
                Text(
                    "AI STRUCTURED THIS",
                    style = MaterialTheme.typography.labelSmall,
                    color = AuroraTeal
                )
                Spacer(Modifier.height(8.dp))
                ElevatedCard(shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            r.tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkTextMuted
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(r.title, style = MaterialTheme.typography.titleMedium)
                        if (r.snippet.isNotBlank()) {
                            Spacer(Modifier.height(6.dp)); Text(
                                r.snippet,
                                style = MaterialTheme.typography.bodySmall,
                                color = DarkTextMuted
                            )
                        }
                        if (r.items.isNotEmpty()) {
                            Spacer(Modifier.height(8.dp)); r.items.forEach {
                                Text(
                                    "☐ ${it.text}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        if (r.reminder.isNotBlank()) {
                            Spacer(Modifier.height(8.dp)); Text(
                                "⏰ ${r.reminder}",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { viewModel.save { navController.navigateUp() } },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AuroraTeal),
                    ) { Text("Save note", color = Color.White) }
                    OutlinedButton(onClick = { navController.navigateUp() }) { Text("Edit first") }
                }
            }
        }
    }
}