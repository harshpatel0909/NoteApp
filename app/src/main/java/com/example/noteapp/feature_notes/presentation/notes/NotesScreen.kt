package com.example.noteapp.feature_notes.presentation.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.noteapp.feature_notes.domain.model.Note
import com.example.noteapp.feature_notes.presentation.notes.components.AskNotesSheet
import com.example.noteapp.feature_notes.presentation.notes.components.FabMenu
import com.example.noteapp.feature_notes.presentation.notes.components.NoteCard
import com.example.noteapp.feature_notes.presentation.util.Screen
import com.example.noteapp.ui.theme.AuroraTeal
import com.example.noteapp.ui.theme.AuroraViolet
import com.example.noteapp.ui.theme.DarkFieldBorder
import com.example.noteapp.ui.theme.DarkTextFaint
import com.example.noteapp.ui.theme.DarkTextMuted
import com.example.noteapp.ui.theme.DarkTextPrimary
import com.example.noteapp.ui.theme.LightFieldBorder
import com.example.noteapp.ui.theme.LightTextFaint
import com.example.noteapp.ui.theme.LightTextMuted
import com.example.noteapp.ui.theme.LightTextPrimary
import com.example.noteapp.ui.theme.SolidDark
import com.example.noteapp.ui.theme.SolidLight
import com.example.noteapp.ui.theme.SolidTextDark
import com.example.noteapp.ui.theme.SolidTextLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    navController: NavHostController,
    viewModel: NotesViewModel
) {
    val state = viewModel.state
    val dark = state.isDarkTheme
    var askOpen by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Grab the current platform lifecycle context reference safely inside Compose
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (!state.isSelectionMode) {
                FabMenu(
                    onNewNote = { navController.navigate(Screen.AddEditNoteScreen.route) },
                    onSmartCapture = { navController.navigate(Screen.CaptureScreen.route) },
                )
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AuroraTeal.copy(alpha = if (dark) 0.14f else 0.04f),
                            AuroraViolet.copy(alpha = if (dark) 0.10f else 0.02f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(padding)
        ) {
            // Header Content Layer
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 18.dp, 20.dp, 0.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (state.isSelectionMode) {
                    IconButton(onClick = { viewModel.onEvent(NotesEvent.ClearSelection) }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel Selection")
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${state.selectedNoteIds.size} Selected",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (dark) DarkTextPrimary else LightTextPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    // --- NATIVE SYSTEM SHARE ENGINE PIPELINE ACTIVATED ---
                    IconButton(onClick = {
                        val selectedNotes = state.notes.filter { it.id in state.selectedNoteIds }
                        if (selectedNotes.isNotEmpty()) {
                            val shareStringBuilder = StringBuilder()

                            // Adjust layout design header text context automatically based on count
                            if (selectedNotes.size == 1) {
                                shareStringBuilder.append("📝 SHARED NOTE\n")
                                shareStringBuilder.append("=========================\n\n")
                            } else {
                                shareStringBuilder.append("📝 SHARED NOTES COLLECTION (${selectedNotes.size})\n")
                                shareStringBuilder.append("=========================\n\n")
                            }

                            selectedNotes.forEachIndexed { index, note ->
                                if (selectedNotes.size > 1) {
                                    shareStringBuilder.append("${index + 1}. ")
                                }
                                shareStringBuilder.append("${note.title.uppercase()}\n")
                                if (note.tag.isNotBlank() && note.tag != "All") shareStringBuilder.append(
                                    "🏷️ Tag: ${note.tag}\n"
                                )
                                if (note.folder?.isNotBlank()
                                        ?: false
                                ) shareStringBuilder.append("📁 Folder: ${note.folder}\n")

                                if (note.isChecklist && note.checklistItems.isNotEmpty()) {
                                    note.checklistItems.forEach { item ->
                                        val status = if (item.done) "☑️" else "🔲"
                                        shareStringBuilder.append("$status ${item.text}\n")
                                    }
                                } else {
                                    shareStringBuilder.append("${note.content}\n")
                                }
                                shareStringBuilder.append("\n\n")
                            }

                            val sendIntent = android.content.Intent().apply {
                                action = android.content.Intent.ACTION_SEND
                                putExtra(
                                    android.content.Intent.EXTRA_TEXT,
                                    shareStringBuilder.toString()
                                )
                                type = "text/plain"
                            }
                            val shareIntent = android.content.Intent.createChooser(
                                sendIntent,
                                "Share note components via"
                            )
                            context.startActivity(shareIntent)
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share notes structure")
                    }

                    IconButton(onClick = { viewModel.onEvent(NotesEvent.ShowBatchDeleteDialog(true)) }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Batch delete selected notes",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    Box(
                        Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AuroraTeal, AuroraViolet))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "HP",
                            color = Color(0xFF08110F),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.width(11.dp))
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.headlineSmall,
                        color = if (dark) DarkTextPrimary else LightTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.onEvent(NotesEvent.OrganizeWithAi) }) {
                        if (state.isOrganizing) CircularProgressIndicator(
                            Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        else Icon(Icons.Default.AutoAwesome, contentDescription = "Organize")
                    }
                    IconButton(onClick = { viewModel.onEvent(NotesEvent.ToggleGridView) }) {
                        Icon(
                            if (state.isGridView) Icons.Default.GridView else Icons.Default.ViewAgenda,
                            contentDescription = "Toggle view"
                        )
                    }
                    IconButton(onClick = { viewModel.onEvent(NotesEvent.ToggleTheme) }) {
                        Icon(
                            if (dark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle theme"
                        )
                    }
                }
            }

            // Ask bar
            Surface(
                onClick = { askOpen = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 16.dp, 20.dp, 0.dp),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, if (dark) DarkFieldBorder else LightFieldBorder)
            ) {
                Row(
                    Modifier.padding(15.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        null,
                        tint = if (dark) DarkTextMuted else LightTextMuted
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Ask your notes…",
                        color = if (dark) DarkTextMuted else LightTextMuted,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Surface(
                        color = AuroraTeal.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            "AI",
                            color = AuroraTeal,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(8.dp, 3.dp)
                        )
                    }
                }
            }

            // Filter chips
            val chips = listOf("All") +
                    Note.tagOptions.filter { tag -> state.notes.any { it.tag == tag } } +
                    state.aiFolders
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 14.dp, 20.dp, 0.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                chips.distinct().forEach { label ->
                    val selected = state.activeFilter == label
                    Surface(
                        onClick = { viewModel.onEvent(NotesEvent.SetFilter(label)) },
                        shape = RoundedCornerShape(50),
                        color = if (selected) (if (dark) SolidDark else SolidLight)
                        else MaterialTheme.colorScheme.surface,
                        border = if (!selected) BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant
                        ) else null
                    ) {
                        Text(
                            label,
                            color = if (selected) (if (dark) SolidTextDark else SolidTextLight)
                            else (if (dark) DarkTextMuted else LightTextMuted),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(15.dp, 8.dp)
                        )
                    }
                }
            }

            // Count
            val visible = viewModel.visibleNotes()
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 18.dp, 20.dp, 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    (if (state.activeFilter == "All") "ALL NOTES" else state.activeFilter.uppercase()),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (dark) DarkTextFaint else LightTextFaint
                )
                Text(
                    "${visible.size}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (dark) DarkTextFaint else LightTextFaint
                )
            }

            // Notes grid
            if (visible.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No notes yet — tap + to add one",
                        color = if (dark) DarkTextFaint else LightTextFaint
                    )
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(if (state.isGridView) 2 else 1),
                    contentPadding = PaddingValues(20.dp, 4.dp, 20.dp, 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(11.dp),
                    verticalItemSpacing = 11.dp,
                    modifier = Modifier.weight(1f)
                ) {
                    items(visible, key = { it.id ?: it.hashCode() }) { note ->
                        val isSelected = state.selectedNoteIds.contains(note.id)

                        val dismissState = rememberSwipeToDismissBoxState(
                            confirmValueChange = { positionalThreshold ->
                                if (positionalThreshold == SwipeToDismissBoxValue.EndToStart) {
                                    viewModel.onEvent(NotesEvent.ShowSingleDeleteDialog(note))
                                    false
                                } else false
                            }
                        )

                        SwipeToDismissBox(
                            state = dismissState,
                            enableDismissFromStartToEnd = false,
                            backgroundContent = {
                                val color by animateColorAsState(
                                    targetValue = when (dismissState.targetValue) {
                                        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                                        else -> Color.Transparent
                                    },
                                    label = "SwipeDeleteBackgroundAnimation"
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(color, shape = RoundedCornerShape(16.dp))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Swipe Delete Action Icon Layer",
                                            tint = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            },
                            content = {
                                NoteCard(
                                    note = note,
                                    dark = dark,
                                    isSelected = isSelected,
                                    isSelectionMode = state.isSelectionMode,
                                    onOpen = {
                                        if (state.isSelectionMode) {
                                            note.id?.let {
                                                viewModel.onEvent(
                                                    NotesEvent.ToggleNoteSelection(
                                                        it
                                                    )
                                                )
                                            }
                                        } else {
                                            navController.navigate(
                                                Screen.AddEditNoteScreen.route + "?noteId=${note.id}&noteColor=${note.color}"
                                            )
                                        }
                                    },
                                    onLongClick = {
                                        note.id?.let {
                                            viewModel.onEvent(
                                                NotesEvent.ToggleNoteSelection(
                                                    it
                                                )
                                            )
                                        }
                                    },
                                    onPinToggle = { viewModel.onEvent(NotesEvent.TogglePin(note)) },
                                    onChecklistToggle = { idx ->
                                        viewModel.onEvent(
                                            NotesEvent.ToggleChecklistItem(
                                                note,
                                                idx
                                            )
                                        )
                                    },
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    if (askOpen) {
        AskNotesSheet(notes = state.notes, dark = dark, onDismiss = { askOpen = false })
    }


    if (state.showSingleDeleteDialog && state.notePendingDeletion != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.onEvent(NotesEvent.ShowSingleDeleteDialog(null)) },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to permanently delete this note?") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = { viewModel.onEvent(NotesEvent.DeleteNote(state.notePendingDeletion)) }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { viewModel.onEvent(NotesEvent.ShowSingleDeleteDialog(null)) }
                ) {
                    Text("Cancel", color = if (dark) DarkTextMuted else LightTextMuted)
                }
            }
        )
    }

    if (state.showBatchDeleteDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.onEvent(NotesEvent.ShowBatchDeleteDialog(false)) },
            title = { Text("Delete Notes") },
            text = { Text("Are you sure you want to delete the ${state.selectedNoteIds.size} selected notes?") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = { viewModel.onEvent(NotesEvent.DeleteSelectedNotes) }
                ) {
                    Text("Delete All", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { viewModel.onEvent(NotesEvent.ShowBatchDeleteDialog(false)) }
                ) {
                    Text("Cancel", color = if (dark) DarkTextMuted else LightTextMuted)
                }
            }
        )
    }
}