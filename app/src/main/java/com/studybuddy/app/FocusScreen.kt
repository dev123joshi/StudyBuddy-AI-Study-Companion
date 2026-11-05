package com.studybuddy.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val FOCUS_SECONDS = 25 * 60

@Composable
fun FocusScreen() {
    var secondsLeft by rememberSaveable { mutableIntStateOf(FOCUS_SECONDS) }
    var running by rememberSaveable { mutableStateOf(false) }
    var sessions by rememberSaveable { mutableIntStateOf(0) }
    var tip by remember { mutableStateOf("") }
    var tipLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(running) {
        while (running && secondsLeft > 0) { delay(1000); secondsLeft-- }
        if (running && secondsLeft == 0) {
            running = false; sessions++; secondsLeft = FOCUS_SECONDS
        }
    }

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text("Focus Timer", style = MaterialTheme.typography.headlineMedium)
        Text("%02d:%02d".format(secondsLeft / 60, secondsLeft % 60), fontSize = 72.sp)
        LinearProgressIndicator(
            progress = { 1f - secondsLeft / FOCUS_SECONDS.toFloat() },
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { running = !running }) { Text(if (running) "Pause" else "Start") }
            OutlinedButton(onClick = { running = false; secondsLeft = FOCUS_SECONDS }) { Text("Reset") }
        }
        Text("Sessions completed today: $sessions", style = MaterialTheme.typography.titleMedium)

        OutlinedButton(enabled = !tipLoading, onClick = {
            tipLoading = true
            scope.launch {
                GeminiClient.ask(
                    "Give one short, practical study tip (max 2 sentences) for a student who just " +
                        "finished $sessions Pomodoro sessions."
                ).onSuccess { tip = it }.onFailure { tip = "⚠️ ${it.message}" }
                tipLoading = false
            }
        }) { Text(if (tipLoading) "Asking AI..." else "Get an AI study tip") }
        if (tip.isNotBlank()) Card(Modifier.fillMaxWidth()) { Text(tip, Modifier.padding(14.dp)) }
    }
}
