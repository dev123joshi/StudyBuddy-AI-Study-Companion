package com.studybuddy.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.json.JSONArray

private data class Question(val q: String, val options: List<String>, val answer: Int, val explain: String)

private fun parseQuiz(raw: String): List<Question> {
    val clean = raw.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
    val arr = JSONArray(clean)
    return (0 until arr.length()).map {
        val o = arr.getJSONObject(it)
        val opts = o.getJSONArray("options")
        Question(
            o.getString("q"),
            (0 until opts.length()).map { i -> opts.getString(i) },
            o.getInt("answer"),
            o.optString("explain")
        )
    }
}

@Composable
fun QuizScreen(vm: AppViewModel) {
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var index by remember { mutableIntStateOf(0) }
    var picked by remember { mutableStateOf<Int?>(null) }
    var score by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("AI Quiz", style = MaterialTheme.typography.headlineMedium)

        val finished = questions.isNotEmpty() && index >= questions.size

        if (questions.isEmpty() || finished) {
            if (finished) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Quiz complete!", style = MaterialTheme.typography.titleLarge)
                        Text("Score: $score / ${questions.size}", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
            OutlinedTextField(
                value = vm.quizTopic, onValueChange = { vm.quizTopic = it },
                label = { Text("Topic or paste notes") }, minLines = 3, maxLines = 8,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                enabled = vm.quizTopic.isNotBlank() && !loading,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    loading = true; error = ""
                    scope.launch {
                        GeminiClient.ask(
                            prompt = "Create 5 multiple-choice questions to test a college student on:\n${vm.quizTopic}\n\n" +
                                "Return ONLY a JSON array. Each item: {\"q\": string, \"options\": [4 strings], " +
                                "\"answer\": index 0-3 of the correct option, \"explain\": one-sentence explanation}.",
                            jsonOutput = true
                        ).mapCatching { parseQuiz(it) }
                            .onSuccess { questions = it; index = 0; score = 0; picked = null }
                            .onFailure { error = it.message ?: "Could not create the quiz. Try again." }
                        loading = false
                    }
                }
            ) { Text(if (loading) "Generating..." else if (finished) "New quiz" else "Generate quiz") }
            if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())
            if (error.isNotBlank()) Text("⚠️ $error", color = MaterialTheme.colorScheme.error)
        } else {
            val q = questions[index]
            Text("Question ${index + 1} of ${questions.size}", style = MaterialTheme.typography.labelLarge)
            Text(q.q, style = MaterialTheme.typography.titleMedium)

            q.options.forEachIndexed { i, opt ->
                val answered = picked != null
                val color = when {
                    !answered -> MaterialTheme.colorScheme.surfaceVariant
                    i == q.answer -> MaterialTheme.colorScheme.primaryContainer
                    i == picked -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                Card(
                    onClick = { if (!answered) { picked = i; if (i == q.answer) score++ } },
                    colors = CardDefaults.cardColors(containerColor = color),
                    modifier = Modifier.fillMaxWidth()
                ) { Text(opt, Modifier.padding(14.dp)) }
            }

            if (picked != null) {
                Text(if (picked == q.answer) "✅ Correct! ${q.explain}" else "❌ Not quite. ${q.explain}")
                Button(onClick = { index++; picked = null }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (index == questions.size - 1) "Finish" else "Next")
                }
            }
        }
    }
}
