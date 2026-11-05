package com.studybuddy.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private enum class Mode(val label: String, val instruction: String) {
    Summarize("Summarize", "Summarize the topic or notes below in a short, clear paragraph."),
    Explain("Explain simply", "Explain the topic or notes below like I'm a first-year college student. Use simple words and one real-life example."),
    KeyPoints("Key points", "List the 5-8 most important points about the topic or notes below as a bullet list."),
    Flashcards("Flashcards", "Create 6 flashcards (Q: ... / A: ...) about the topic or notes below.")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TutorScreen(vm: AppViewModel) {
    var mode by remember { mutableStateOf(Mode.Summarize) }
    var output by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("AI Tutor", style = MaterialTheme.typography.headlineMedium)
        Text("Paste a topic or notes, pick what you need, and let Gemini do the work.")

        OutlinedTextField(
            value = vm.tutorInput, onValueChange = { vm.tutorInput = it },
            label = { Text("Your notes or question") },
            minLines = 5, maxLines = 10, modifier = Modifier.fillMaxWidth()
        )

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Mode.values().forEach {
                FilterChip(selected = mode == it, onClick = { mode = it }, label = { Text(it.label) })
            }
        }

        Button(
            enabled = vm.tutorInput.isNotBlank() && !loading,
            onClick = {
                loading = true; saved = false; output = ""
                scope.launch {
                    GeminiClient.ask(
                        prompt = "${mode.instruction}\n\nInput:\n${vm.tutorInput}",
                        system = "You are a friendly, accurate college study tutor. Keep answers concise. " +
                                "The student's input may be pasted notes OR just a short topic or question. " +
                                "If it is only a topic, use your own knowledge to teach it. " +
                                "Never ask the student to provide more text; always produce the result directly."
                    ).onSuccess { output = it }.onFailure { output = "⚠️ ${it.message}" }
                    loading = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (loading) "Thinking..." else "Generate") }

        if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())

        if (output.isNotBlank()) {
            Card(Modifier.fillMaxWidth()) {
                SelectionContainer { Text(output, Modifier.padding(14.dp)) }
            }
            if (!output.startsWith("⚠️")) {
                OutlinedButton(enabled = !saved, onClick = {
                    vm.saveNote(null, "AI ${mode.label}", output); saved = true
                }) { Text(if (saved) "Saved to Notes ✓" else "Save to Notes") }
            }
        }
    }
}