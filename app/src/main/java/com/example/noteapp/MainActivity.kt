package com.example.noteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.noteapp.feature_notes.presentation.add_edit_note.AddEditNoteScreen
import com.example.noteapp.feature_notes.presentation.capture.CaptureScreen
import com.example.noteapp.feature_notes.presentation.notes.NotesScreen
import com.example.noteapp.feature_notes.presentation.notes.NotesViewModel
import com.example.noteapp.feature_notes.presentation.util.Screen
import com.example.noteapp.ui.theme.NoteAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NotesViewModel = hiltViewModel()
            val state = viewModel.state
            val isDarkTheme = state.isDarkTheme

            NoteAppTheme(darkTheme = isDarkTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.NotesScreen.route
                    ) {
                        composable(Screen.NotesScreen.route) {
                            NotesScreen(navController = navController, viewModel = viewModel)
                        }
                        composable(
                            route = Screen.AddEditNoteScreen.route + "?noteId={noteId}&noteColor={noteColor}",
                            arguments = listOf(
                                navArgument("noteId") { type = NavType.IntType; defaultValue = -1 },
                                navArgument("noteColor") {
                                    type = NavType.IntType; defaultValue = -1
                                },
                            )
                        ) { backStackEntry ->
                            val color = backStackEntry.arguments?.getInt("noteColor") ?: -1
                            AddEditNoteScreen(navController = navController, noteColor = color)
                        }
                        composable(Screen.CaptureScreen.route) {
                            CaptureScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}