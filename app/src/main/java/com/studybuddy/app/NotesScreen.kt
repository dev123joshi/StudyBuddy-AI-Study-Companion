package com.studybuddy.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun NotesScreen(vm: AppViewModel, onSendToTutor: () -> Unit, onQuiz: () -> Unit) {
    var editing by remember { mutableStateOf<Note?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("My Notes", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            if (vm.notes.isEmpty()) {
                Text("No notes yet. Tap + to add your first note, then let the AI summarize it or quiz you on it.")
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vm.notes, key = { it.id }) { note ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text(note.title, style = MaterialTheme.typography.titleMedium)
                            Text(note.body, maxLines = 3, overflow = TextOverflow.Ellipsis)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = { editing = note; showDialog = true }) { Text("Edit") }
                                TextButton(onClick = {
                                    vm.tutorInput = note.body; onSendToTutor()
                                }) { Text("Ask AI") }
                                TextButton(onClick = {
                                    vm.quizTopic = note.body; onQuiz()
                                }) { Text("Quiz me") }
                                Spacer(Modifier.weight(1f))
                                IconButton(onClick = { vm.deleteNote(note) }) {
                                    Icon(Icons.Default.Delete, "Delete")
                                }
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
        FloatingActionButton(
            onClick = { editing = null; showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) { Icon(Icons.Default.Add, "Add note") }
    }

    if (showDialog) {
        var title by remember(editing) { mutableStateOf(editing?.title ?: "") }
        var body by remember(editing) { mutableStateOf(editing?.body ?: "") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editing == null) "New note" else "Edit note") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(title, { title = it }, label = { Text("Title") }, singleLine = true)
                    OutlinedTextField(body, { body = it }, label = { Text("Content") }, minLines = 4, maxLines = 8)
                }
            },
            confirmButton = {
                TextButton(
                    enabled = title.isNotBlank() && body.isNotBlank(),
                    onClick = { vm.saveNote(editing?.id, title.trim(), body.trim()); showDialog = false }
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }
}
