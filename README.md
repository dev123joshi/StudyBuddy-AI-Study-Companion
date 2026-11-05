
# StudyBuddy – AI Study Companion (Android App)

**Name:** Dev Joshi  **USN:** 25MCAR0091  **Class** MCA 

---

## Aim
To design and develop an Android application, **StudyBuddy**, that helps students
manage their notes and study with the help of Artificial Intelligence using the
Google Gemini API.

---

## Objectives
1. To understand the structure of an Android application built with Kotlin and Jetpack Compose.
2. To create a multi-screen app with bottom navigation (Notes, AI Tutor, Quiz, Focus Timer).
3. To integrate an AI model (Google Gemini API) into a mobile application.
4. To store and manage data locally on the device (notes).
5. To build, test and install the app as an APK on a real Android phone.

---

## Introduction
StudyBuddy is an Android application that works as a personal study assistant.
Students can save their notes, ask the AI to summarize or explain any topic,
generate a quiz to test themselves, and use a Pomodoro focus timer to stay
productive. The app is written in **Kotlin** using **Jetpack Compose** for the user
interface and follows the **MVVM** architecture. Its AI features use the
**Google Gemini API**, which is called over HTTPS from the app.

### Tools and Technologies
| Item | Details |
|---|---|
| IDE | Android Studio (Koala or newer) |
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Architecture | MVVM with a shared ViewModel |
| Storage | SharedPreferences (JSON) |
| AI | Google Gemini API |
| Minimum Android version | 7.0 (API 24) |

### Project Structure
```
StudyBuddy/
├── app/src/main/java/com/studybuddy/app/
│   ├── MainActivity.kt     (entry point and bottom navigation)
│   ├── AppViewModel.kt     (notes data and shared state)
│   ├── GeminiClient.kt     (Gemini API calls, fallback and retries)
│   ├── NotesScreen.kt
│   ├── TutorScreen.kt
│   ├── QuizScreen.kt
│   └── FocusScreen.kt
├── build.gradle.kts
└── local.properties.example
```

---

## Task / Steps

### 1. Set up the project
- Install Android Studio and open the project folder.
- Wait for the Gradle sync to finish.

### 2. Add the Gemini API key
- Get a free key from https://aistudio.google.com/apikey
- Copy `local.properties.example` to `local.properties`.
- Add the line `GEMINI_API_KEY=your_api_key_here`.
- Rebuild the project (Build → Rebuild Project).

### 3. Run the application
- Select an emulator or a connected phone.
- Click the Run ▶ button.

**Output:**

### 4. Notes Screen
- Tap **+** to add a note with a title and content.
- Use **Edit** to change a note and the bin icon to delete it.
- Use **Ask AI** or **Quiz me** to send a note to the AI screens.

**Output:**
<img width="1882" height="982" alt="image" src="https://github.com/user-attachments/assets/0c4c37e5-5e8c-44a2-8f19-df48405ccda1" />


### 5. AI Tutor Screen
- Type a topic or paste notes.
- Choose a mode:
  - Summarize
  - Explain simply
  - Key points
  - Flashcards
- Tap **Generate** to get the AI response.
- Tap **Save to Notes** to keep the result.

**Output:**
<img width="757" height="1600" alt="WhatsApp Image 2026-11-05 at 14 57 50" src="https://github.com/user-attachments/assets/4a3df4e3-5f57-496f-af5f-12c3ab77e355" />

### 6. AI Quiz Screen
- Enter a topic or paste notes.
- Tap **Generate quiz** to get 5 multiple-choice questions.
- Select an answer to see whether it is correct, with an explanation.
- View the final score at the end.

**Output:**
<img width="1882" height="982" alt="image" src="https://github.com/user-attachments/assets/a9c4fc9f-a42d-435a-849b-0b44307ebc90" />
<img width="760" height="1600" alt="WhatsApp Image 2026-11-05 at 14 57 51" src="https://github.com/user-attachments/assets/cd77b890-4af3-46fb-80c3-c77ec017b627" />





### 7. Focus Timer Screen
- Tap **Start** to begin a 25-minute session.
- Use **Pause** and **Reset** as needed.
- Tap **Get an AI study tip** for a short tip from the AI.

**Output:**
<img width="759" height="1600" alt="WhatsApp Image 2026-11-05 at 14 57 51 (1)" src="https://github.com/user-attachments/assets/f348998c-bc92-4fcb-9645-9525c1312f34" />




### 8. Build the APK
- Go to Build → Build Bundle(s) / APK(s) → Build APK(s).
- The file is created at `app/build/outputs/apk/debug/app-debug.apk`.
- Copy the APK to a phone and install it.

**Output:**
<img width="760" height="1600" alt="WhatsApp Image 2026-11-05 at 14 57 51" src="https://github.com/user-attachments/assets/cd77b890-4af3-46fb-80c3-c77ec017b627" />

---

## How the AI Works
| Feature | AI Task |
|---|---|
| AI Tutor | Sends a prompt (summary, key points, etc.) to Gemini and displays the reply |
| AI Quiz | Asks Gemini for JSON, then parses it into questions, options and answers |
| Study tip | Requests a short study tip based on completed sessions |

If a model is busy or unavailable (HTTP 404, 429 or 503), the app tries another
model automatically.

---

## Result
The StudyBuddy Android application was successfully developed and tested. The
experiment helped in understanding Kotlin and Jetpack Compose, multi-screen
navigation, local data storage and the integration of an AI model through the
Google Gemini API in a mobile application.

---

## Conclusion
StudyBuddy was successfully developed as an AI-powered Android study companion.
The project gave practical experience in Kotlin, Jetpack Compose, multi-screen
navigation, local storage and integrating the Google Gemini API into a mobile
app. The app can be extended in future with a chat tutor, cloud sync and dark mode.
