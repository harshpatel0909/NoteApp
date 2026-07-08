package com.example.noteapp.feature_notes.presentation.notes.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.noteapp.ui.theme.AuroraTeal
import com.example.noteapp.ui.theme.AuroraViolet

@Composable
fun FabMenu(onNewNote: () -> Unit, onSmartCapture: () -> Unit) {
    var open by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.End) {
        AnimatedVisibility(
            visible = open,
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ExtendedFloatingActionButton(
                    onClick = { open = false; onSmartCapture() },
                    icon = { Icon(Icons.Default.AutoAwesome, null) },
                    text = { Text("Smart capture") },
                    containerColor = AuroraTeal, contentColor = Color.White,
                )
                ExtendedFloatingActionButton(
                    onClick = { open = false; onNewNote() },
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("New note") },
                    containerColor = AuroraViolet, contentColor = Color.White,
                )
                Spacer(Modifier.height(2.dp))
            }
        }
        FloatingActionButton(
            onClick = { open = !open },
            shape = RoundedCornerShape(21.dp),
            containerColor = Color.Transparent,
            modifier = Modifier.background(
                Brush.linearGradient(listOf(AuroraTeal, AuroraViolet)),
                RoundedCornerShape(21.dp)
            )
        ) {
            Icon(if (open) Icons.Default.Close else Icons.Default.Add, "Menu", tint = Color.White)
        }
    }
}