package com.studybuddy.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.URL

/** Thin wrapper around the Google Gemini REST API (no extra libraries needed). */
object GeminiClient {
    // Tried in order. If one is missing/busy/slow, the next is used automatically.
    private val MODELS = listOf(
        "gemini-2.5-flash",
        "gemini-flash-latest",
        "gemini-2.5-flash-lite",
        "gemini-2.0-flash",
        "gemini-2.0-flash-lite",
        "gemini-flash-lite-latest"
    )

    suspend fun ask(
        prompt: String,
        system: String? = null,
        jsonOutput: Boolean = false
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val key = BuildConfig.GEMINI_API_KEY.trim()
            require(key.isNotBlank()) { "Missing API key. Add GEMINI_API_KEY to local.properties and rebuild." }

            fun parts(t: String) = JSONArray().put(JSONObject().put("text", t))
            val body = JSONObject()
                .put("contents", JSONArray().put(JSONObject().put("parts", parts(prompt))))
            if (system != null) body.put("systemInstruction", JSONObject().put("parts", parts(system)))
            if (jsonOutput) body.put("generationConfig", JSONObject().put("responseMimeType", "application/json"))

            var lastError = "Unknown error"
            for (attempt in 0..3) {
                if (attempt > 0) delay(attempt * 2500L) // wait 2.5s, 5s, 7.5s between passes
                for (model in MODELS) {
                    try {
                        val conn = URL("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$key")
                            .openConnection() as HttpURLConnection
                        conn.requestMethod = "POST"
                        conn.setRequestProperty("Content-Type", "application/json")
                        conn.connectTimeout = 15_000
                        conn.readTimeout = 40_000
                        conn.doOutput = true
                        conn.outputStream.use { it.write(body.toString().toByteArray()) }

                        val code = conn.responseCode
                        val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                        val text = stream?.bufferedReader()?.use { it.readText() } ?: ""

                        if (code in 200..299) {
                            return@runCatching JSONObject(text).getJSONArray("candidates").getJSONObject(0)
                                .getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")
                        }
                        val msg = runCatching { JSONObject(text).getJSONObject("error").getString("message") }
                            .getOrDefault(text.take(150))
                        lastError = "Error $code ($model): $msg"
                        if (code !in listOf(404, 429, 500, 503)) error(lastError)
                    } catch (e: UnknownHostException) {
                        // No internet / DNS problem: switching models won't help
                        error("No internet on this device. Check Wi-Fi/data (on the emulator: cold boot it or restart it).")
                    } catch (e: SocketTimeoutException) {
                        lastError = "Timed out waiting for $model. Trying another model..."
                    } catch (e: IOException) {
                        lastError = "Network problem (${e.javaClass.simpleName}). Check your connection."
                    }
                }
            }
            if (lastError.contains("503") || lastError.contains("429")) {
                error("Google's AI servers are busy right now. Please wait a minute and tap the button again.")
            }
            error(lastError)
        }
    }
}