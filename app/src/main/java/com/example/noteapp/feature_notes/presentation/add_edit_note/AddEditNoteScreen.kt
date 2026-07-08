package com.example.noteapp.feature_notes.presentation.add_edit_note

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.noteapp.feature_notes.domain.model.Note
import com.example.noteapp.ui.theme.AuroraTeal
import com.example.noteapp.ui.theme.AuroraViolet
import com.example.noteapp.ui.theme.DarkChipBorder
import com.example.noteapp.ui.theme.DarkTextFaint
import com.example.noteapp.ui.theme.DarkTextMuted
import com.example.noteapp.ui.theme.LightChipBorder
import com.example.noteapp.ui.theme.LightTextFaint
import com.example.noteapp.ui.theme.LightTextMuted
import com.example.noteapp.ui.theme.SolidDark
import com.example.noteapp.ui.theme.SolidLight
import com.example.noteapp.ui.theme.SolidTextDark
import com.example.noteapp.ui.theme.SolidTextLight
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    navController: NavController,
    noteColor: Int,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var showDatePicker by remember { mutableStateOf(false) }
    var showToneMenu by remember { mutableStateOf(false) }
    var showLangMenu by remember { mutableStateOf(false) }

    // --- SYSTEM GALLERY LAUNCHER
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onEvent(AddEditNoteEvent.AddImageUri(it.toString()))
        }
    }

    val dark = MaterialTheme.colorScheme.background.toArgb() == 0xFF0B1412.toInt() ||
            MaterialTheme.colorScheme.background.toArgb() == Color.Black.toArgb()

    // Real-time Text Analytics Calculations
    val wordCount = remember(state.body) {
        if (state.body.isBlank()) 0 else state.body.trim().split("\\s+".toRegex()).size
    }
    val readingTimeMinutes = remember(wordCount) {
        kotlin.math.ceil(wordCount / 200.0).toInt()
    }

    LaunchedEffect(true) {
        viewModel.events.collectLatest {
            when (it) {
                is AddEditNoteViewModel.UiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(it.message)
                AddEditNoteViewModel.UiEvent.SaveNote -> navController.navigateUp()
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.id == null) "New note" else "Edit note",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    // 1. SHARE ACTION COMPONENT
                    IconButton(onClick = {
                        if (state.body.isNotBlank() || state.title.isNotBlank()) {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, "${state.title}\n\n${state.body}")
                                type = "text/plain"
                            }
                            context.startActivity(
                                Intent.createChooser(
                                    sendIntent,
                                    "Share note via"
                                )
                            )
                        }
                    }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share Text Content",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // 2. EXPORT AS PDF ACTION COMPONENT
                    IconButton(onClick = {
                        val printIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_SUBJECT, state.title)
                        }
                        context.startActivity(
                            Intent.createChooser(
                                printIntent,
                                "Export PDF Document via"
                            )
                        )
                    }) {
                        Icon(
                            Icons.Default.PictureAsPdf,
                            contentDescription = "Export PDF",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // 3. PERSIST SAVE ACTION COMPONENT
                    Button(
                        onClick = { viewModel.onEvent(AddEditNoteEvent.SaveNote) },
                        colors = ButtonDefaults.buttonColors(containerColor = AuroraTeal),
                        shape = RoundedCornerShape(11.dp),
                        modifier = Modifier.padding(end = 12.dp, start = 4.dp)
                    ) { Text("Save", color = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Title
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredTitle(it)) },
                placeholder = { Text("Title") },
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedPlaceholderColor = if (dark) DarkTextMuted else LightTextMuted,
                    focusedPlaceholderColor = if (dark) DarkTextMuted else LightTextMuted
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )

            // Tag chips
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Note.tagOptions.forEach { tag ->
                    val sel = state.tag == tag
                    Surface(
                        onClick = { viewModel.onEvent(AddEditNoteEvent.ChangeTag(tag)) },
                        shape = RoundedCornerShape(50),
                        color = if (sel) (if (dark) SolidDark else SolidLight) else Color.Transparent,
                        border = if (!sel) BorderStroke(
                            1.dp,
                            if (dark) DarkChipBorder else LightChipBorder
                        ) else null
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (sel) (if (dark) SolidTextDark else SolidTextLight) else (if (dark) DarkTextMuted else LightTextMuted),
                            modifier = Modifier.padding(13.dp, 6.dp)
                        )
                    }
                }
            }

            // Color picker
            Spacer(Modifier.height(14.dp))
            Text(
                "COLOR",
                style = MaterialTheme.typography.labelSmall,
                color = if (dark) DarkTextFaint else LightTextFaint
            )
            Row(Modifier.padding(top = 9.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Note.noteColors.forEach { color ->
                    val argb = color.toArgb()
                    Box(
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                BorderStroke(
                                    2.dp,
                                    if (state.colorInt == argb) (if (dark) Color.White else Color.Black) else Color.Transparent
                                ),
                                CircleShape
                            )
                            .clickable { viewModel.onEvent(AddEditNoteEvent.ChangeColor(argb)) }
                    )
                }
            }

            // Body Context Editor Area
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = state.body,
                onValueChange = { viewModel.onEvent(AddEditNoteEvent.EnteredBody(it)) },
                placeholder = { Text("Write something…") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    unfocusedPlaceholderColor = if (dark) DarkTextMuted else LightTextMuted,
                    focusedPlaceholderColor = if (dark) DarkTextMuted else LightTextMuted
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp),
                shape = RoundedCornerShape(14.dp),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = "Add Image Element",
                        tint = AuroraTeal
                    )
                }
                IconButton(onClick = { viewModel.onEvent(AddEditNoteEvent.TriggerVoiceRecording) }) {
                    Icon(
                        Icons.Default.Mic,
                        contentDescription = "Record Voice Data",
                        tint = AuroraTeal
                    )
                }
                IconButton(onClick = { viewModel.onEvent(AddEditNoteEvent.TriggerSketchPad) }) {
                    Icon(
                        Icons.Default.Gesture,
                        contentDescription = "Draw Custom Sketch Document",
                        tint = AuroraTeal
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(
                "AI ASSISTANT ENHANCEMENTS",
                style = MaterialTheme.typography.labelSmall,
                color = if (dark) DarkTextFaint else LightTextFaint
            )
            Row(
                Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { viewModel.onEvent(AddEditNoteEvent.TidyWithAi) },
                    label = { Text("Tidy with AI") },
                    colors = AssistChipDefaults.assistChipColors(labelColor = MaterialTheme.colorScheme.onSurface),
                    leadingIcon = {
                        if (state.isTidying) CircularProgressIndicator(
                            Modifier.size(13.dp),
                            strokeWidth = 2.dp
                        )
                        else Icon(
                            Icons.Default.AutoAwesome,
                            null,
                            Modifier.size(14.dp),
                            tint = AuroraTeal
                        )
                    }
                )
                AssistChip(
                    onClick = { viewModel.onEvent(AddEditNoteEvent.SummarizeWithAi) },
                    label = { Text("Summarize") },
                    colors = AssistChipDefaults.assistChipColors(labelColor = MaterialTheme.colorScheme.onSurface),
                    leadingIcon = {
                        if (state.isSummarizing) CircularProgressIndicator(
                            Modifier.size(13.dp),
                            strokeWidth = 2.dp
                        )
                        else Icon(
                            Icons.Default.AutoAwesome,
                            null,
                            Modifier.size(14.dp),
                            tint = AuroraViolet
                        )
                    }
                )
                AssistChip(
                    onClick = { viewModel.onEvent(AddEditNoteEvent.MakeChecklist) },
                    label = { Text("Make checklist") },
                    colors = AssistChipDefaults.assistChipColors(labelColor = MaterialTheme.colorScheme.onSurface),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Checklist,
                            null,
                            Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }

            Row(
                Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Grammar Formatting Chip Option
                AssistChip(
                    onClick = { viewModel.onEvent(AddEditNoteEvent.FixGrammar) },
                    label = { Text("Fix Grammar") },
                    leadingIcon = {
                        if (state.isFixingGrammar) CircularProgressIndicator(
                            Modifier.size(13.dp),
                            strokeWidth = 2.dp
                        )
                        else Icon(
                            Icons.Default.AutoAwesome,
                            null,
                            Modifier.size(14.dp),
                            tint = AuroraTeal
                        )
                    }
                )

                // Change Writing Tone Options Dropdown Block
                Box {
                    AssistChip(
                        onClick = { showToneMenu = true },
                        label = { Text("Change Tone") },
                        leadingIcon = {
                            if (state.isChangingTone) CircularProgressIndicator(
                                Modifier.size(13.dp),
                                strokeWidth = 2.dp
                            )
                            else Icon(
                                Icons.Default.Tune,
                                null,
                                Modifier.size(14.dp),
                                tint = AuroraViolet
                            )
                        }
                    )
                    DropdownMenu(
                        expanded = showToneMenu,
                        onDismissRequest = { showToneMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Professional") },
                            onClick = {
                                viewModel.onEvent(AddEditNoteEvent.ChangeTone("Professional"))
                                showToneMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Casual") },
                            onClick = {
                                viewModel.onEvent(AddEditNoteEvent.ChangeTone("Casual"))
                                showToneMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Witty") },
                            onClick = {
                                viewModel.onEvent(AddEditNoteEvent.ChangeTone("Witty"))
                                showToneMenu = false
                            }
                        )
                    }
                }

                // Translate Options Dropdown Block
                Box {
                    AssistChip(
                        onClick = { showLangMenu = true },
                        label = { Text("Translate") },
                        leadingIcon = {
                            if (state.isTranslating) CircularProgressIndicator(
                                Modifier.size(13.dp),
                                strokeWidth = 2.dp
                            )
                            else Icon(
                                Icons.Default.Language,
                                null,
                                Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    DropdownMenu(
                        expanded = showLangMenu,
                        onDismissRequest = { showLangMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Spanish") },
                            onClick = {
                                viewModel.onEvent(AddEditNoteEvent.TranslateText("Spanish"))
                                showLangMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("French") },
                            onClick = {
                                viewModel.onEvent(AddEditNoteEvent.TranslateText("French"))
                                showLangMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Chinese") },
                            onClick = {
                                viewModel.onEvent(AddEditNoteEvent.TranslateText("Chinese"))
                                showLangMenu = false
                            }
                        )
                    }
                }
            }

            // Checklist editor
            if (state.isChecklist) {
                Spacer(Modifier.height(16.dp))
                Text(
                    "CHECKLIST",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (dark) DarkTextFaint else LightTextFaint
                )
                state.items.forEachIndexed { index, item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = item.done,
                            onCheckedChange = { viewModel.onEvent(AddEditNoteEvent.ToggleItem(index)) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AuroraTeal,
                                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        OutlinedTextField(
                            value = item.text,
                            onValueChange = {
                                viewModel.onEvent(
                                    AddEditNoteEvent.EditItem(
                                        index,
                                        it
                                    )
                                )
                            },
                            placeholder = { Text("List item") },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedPlaceholderColor = if (dark) DarkTextMuted else LightTextMuted
                            )
                        )
                        IconButton(onClick = { viewModel.onEvent(AddEditNoteEvent.RemoveItem(index)) }) {
                            Icon(
                                Icons.Default.Close,
                                "Remove",
                                Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                TextButton(onClick = { viewModel.onEvent(AddEditNoteEvent.AddItem) }) {
                    Icon(Icons.Default.Add, null, Modifier.size(14.dp), tint = AuroraTeal)
                    Spacer(Modifier.width(4.dp))
                    Text("Add item", color = AuroraTeal)
                }
            }


            Spacer(Modifier.height(16.dp))
            Text(
                "REMINDER",
                style = MaterialTheme.typography.labelSmall,
                color = if (dark) DarkTextFaint else LightTextFaint
            )
            Row(
                Modifier.padding(top = 9.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Static Quick Selections
                listOf(
                    "" to "None",
                    "Today · 6:00 PM" to "Today",
                    "Tomorrow · 9:00 AM" to "Tomorrow"
                ).forEach { (value, label) ->
                    val sel = state.reminder == value
                    Surface(
                        onClick = { viewModel.onEvent(AddEditNoteEvent.ChangeReminder(value)) },
                        shape = RoundedCornerShape(50),
                        color = if (sel) (if (dark) SolidDark else SolidLight) else Color.Transparent,
                        border = if (!sel) BorderStroke(
                            1.dp,
                            if (dark) DarkChipBorder else LightChipBorder
                        ) else null
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (sel) (if (dark) SolidTextDark else SolidTextLight) else (if (dark) DarkTextMuted else LightTextMuted),
                            modifier = Modifier.padding(13.dp, 7.dp)
                        )
                    }
                }


                val parsedCustomDate = state.reminder.toLongOrNull()
                val isCustomSelected = parsedCustomDate != null && parsedCustomDate > 0
                Surface(
                    onClick = { showDatePicker = true },
                    shape = RoundedCornerShape(50),
                    color = if (isCustomSelected) (if (dark) SolidDark else SolidLight) else Color.Transparent,
                    border = if (!isCustomSelected) BorderStroke(
                        1.dp,
                        if (dark) DarkChipBorder else LightChipBorder
                    ) else null
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp, 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.NotificationsActive,
                            null,
                            modifier = Modifier.size(13.dp),
                            tint = AuroraTeal
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isCustomSelected) {
                                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(
                                    Date(
                                        parsedCustomDate!!
                                    )
                                )
                            } else "Custom Date",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isCustomSelected) (if (dark) SolidTextDark else SolidTextLight) else (if (dark) DarkTextMuted else LightTextMuted)
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp, horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$wordCount ${if (wordCount == 1) "word" else "words"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (dark) DarkTextFaint else LightTextFaint
                )
                Text(
                    text = "⏱️ Est: $readingTimeMinutes ${if (readingTimeMinutes == 1) "min" else "mins"} read",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (dark) DarkTextFaint else LightTextFaint
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }


    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { timestamp ->
                        viewModel.onEvent(AddEditNoteEvent.ChangeReminder(timestamp.toString()))
                    }
                    showDatePicker = false
                }) { Text("Set Reminder", color = AuroraTeal) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}