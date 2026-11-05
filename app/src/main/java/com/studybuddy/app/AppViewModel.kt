package com.studybuddy.app

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import org.json.JSONArray
import org.json.JSONObject

data class Note(val id: Long, val title: String, val body: String)

/** Stores notes locally as JSON in SharedPreferences. */
class NoteStore(app: Application) {
    private val prefs = app.getSharedPreferences("notes", 0)

    fun load(): List<Note> {
        val arr = JSONArray(prefs.getString("data", "[]"))
        return (0 until arr.length()).map {
            val o = arr.getJSONObject(it)
            Note(o.getLong("id"), o.getString("title"), o.getString("body"))
        }
    }

    fun save(notes: List<Note>) {
        val arr = JSONArray()
        notes.forEach { arr.put(JSONObject().put("id", it.id).put("title", it.title).put("body", it.body)) }
        prefs.edit().putString("data", arr.toString()).apply()
    }
}

class AppViewModel(app: Application) : AndroidViewModel(app) {
    private val store = NoteStore(app)
    val notes = mutableStateListOf<Note>().also { it.addAll(store.load()) }

    /** Text shared between screens (e.g. a note sent to the AI Tutor). */
    var tutorInput by mutableStateOf("")
    var quizTopic by mutableStateOf("")

    fun saveNote(id: Long?, title: String, body: String) {
        val idx = notes.indexOfFirst { it.id == id }
        if (idx >= 0) notes[idx] = Note(notes[idx].id, title, body)
        else notes.add(0, Note(System.currentTimeMillis(), title, body))
        store.save(notes)
    }

    fun deleteNote(note: Note) {
        notes.remove(note)
        store.save(notes)
    }
}
