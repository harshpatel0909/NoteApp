package com.example.noteapp.feature_notes.presentation.notes.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.noteapp.feature_notes.domain.model.ChecklistItem
import com.example.noteapp.feature_notes.domain.model.Note
import com.example.noteapp.ui.theme.AuroraTeal
import com.example.noteapp.ui.theme.AuroraViolet
import com.example.noteapp.ui.theme.DarkCardBorder
import com.example.noteapp.ui.theme.DarkCard
import com.example.noteapp.ui.theme.DarkChipBorder
import com.example.noteapp.ui.theme.DarkTextFaint
import com.example.noteapp.ui.theme.DarkTextMuted
import com.example.noteapp.ui.theme.DarkTextPrimary
import com.example.noteapp.ui.theme.LightCard
import com.example.noteapp.ui.theme.LightCardBorder
import com.example.noteapp.ui.theme.LightChipBorder
import com.example.noteapp.ui.theme.LightTextFaint
import com.example.noteapp.ui.theme.LightTextMuted
import com.example.noteapp.ui.theme.LightTextPrimary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Note,
    dark: Boolean,
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onOpen: () -> Unit,
    onLongClick: () -> Unit,
    onPinToggle: () -> Unit,
    onChecklistToggle: (Int) -> Unit,
) {
    // Dynamic container color calculation for active item selection states
    val baseCardColor = if (dark) DarkCard else LightCard
    val containerColor = if (isSelected) {
        // Blends a soft primary container highlight over your existing theme
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
    } else {
        baseCardColor
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = containerColor,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else (if (dark) DarkCardBorder else LightCardBorder)
        ),
        tonalElevation = 0.dp,
        modifier = Modifier
            .combinedClickable(
                onClick = onOpen,
                onLongClick = onLongClick
            )
    ) {
        Column(Modifier.padding(13.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(note.color)))
                Spacer(Modifier.width(7.dp))
                Text(
                    note.tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (dark) DarkTextMuted else LightTextMuted,
                    modifier = Modifier.weight(1f)
                )

                // Show standard Pin view logic only when not cleaning multi-select configurations
                if (isSelectionMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onOpen() },
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    IconButton(onClick = onPinToggle, modifier = Modifier.size(22.dp)) {
                        Icon(
                            imageVector = if (note.isPinned) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Pin",
                            tint = if (note.isPinned) AuroraTeal else (if (dark) DarkTextFaint else LightTextFaint),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (dark) DarkTextPrimary else LightTextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (note.isChecklist && note.checklistItems.isNotEmpty()) {
                Spacer(Modifier.height(9.dp))
                note.checklistItems.take(4).forEachIndexed { index, item ->
                    ChecklistRow(item = item, dark = dark) { onChecklistToggle(index) }
                }
            } else if (note.content.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(
                    note.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (dark) DarkTextMuted else LightTextMuted,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (note.reminder.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (dark) DarkChipBorder else LightChipBorder
                ) {
                    Row(
                        Modifier.padding(9.dp, 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Schedule,
                            null,
                            Modifier.size(13.dp),
                            tint = if (dark) DarkTextMuted else LightTextMuted
                        )
                        Spacer(Modifier.width(6.dp))

                        // --- UPDATED FOR TIMESHIFT BUG ---
                        val parsedCustomDate = note.reminder.toLongOrNull()
                        val formattedReminderText =
                            if (parsedCustomDate != null && parsedCustomDate > 0) {
                                java.text.SimpleDateFormat(
                                    "MMM dd, yyyy",
                                    java.util.Locale.getDefault()
                                ).apply {
                                    timeZone =
                                        java.util.TimeZone.getTimeZone("UTC") // Enforces perfect UTC alignment
                                }.format(java.util.Date(parsedCustomDate))
                            } else {
                                note.reminder
                            }

                        Text(
                            text = formattedReminderText,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (dark) DarkTextMuted else LightTextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChecklistRow(item: ChecklistItem, dark: Boolean, onToggle: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onToggle
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(17.dp)
                .clip(RoundedCornerShape(6.dp))
                .then(
                    if (item.done) Modifier.background(
                        Brush.linearGradient(
                            listOf(
                                AuroraTeal,
                                AuroraViolet
                            )
                        )
                    )
                    else Modifier.border(
                        1.5.dp,
                        if (dark) DarkChipBorder else LightChipBorder,
                        RoundedCornerShape(6.dp)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (item.done) Text(
                "✓",
                color = Color(0xFF08110F),
                style = MaterialTheme.typography.labelSmall
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            item.text,
            style = MaterialTheme.typography.bodySmall,
            color = if (item.done) (if (dark) DarkTextFaint else LightTextFaint) else (if (dark) DarkTextPrimary else LightTextPrimary),
            textDecoration = if (item.done) TextDecoration.LineThrough else null,
        )
    }
}