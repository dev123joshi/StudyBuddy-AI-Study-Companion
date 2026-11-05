package com.studybuddy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { StudyBuddyApp() } }
    }
}

private enum class Page(val route: String, val label: String, val icon: ImageVector) {
    Notes("notes", "Notes", Icons.Default.EditNote),
    Tutor("tutor", "AI Tutor", Icons.Default.AutoAwesome),
    Quiz("quiz", "Quiz", Icons.Default.Quiz),
    Focus("focus", "Focus", Icons.Default.Timer)
}

@Composable
fun StudyBuddyApp(vm: AppViewModel = viewModel()) {
    val nav = rememberNavController()
    val current = nav.currentBackStackEntryAsState().value?.destination?.route

    fun go(route: String) = nav.navigate(route) {
        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                Page.values().forEach { p ->
                    NavigationBarItem(
                        selected = current == p.route,
                        onClick = { go(p.route) },
                        icon = { Icon(p.icon, p.label) },
                        label = { Text(p.label) }
                    )
                }
            }
        }
    ) { pad ->
        NavHost(nav, startDestination = Page.Notes.route, modifier = Modifier.padding(pad)) {
            composable(Page.Notes.route) {
                NotesScreen(vm, onSendToTutor = { go(Page.Tutor.route) }, onQuiz = { go(Page.Quiz.route) })
            }
            composable(Page.Tutor.route) { TutorScreen(vm) }
            composable(Page.Quiz.route) { QuizScreen(vm) }
            composable(Page.Focus.route) { FocusScreen() }
        }
    }
}
