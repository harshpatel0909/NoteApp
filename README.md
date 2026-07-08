<div align="center">

# 📝 NoteApp Aurora

### AI-Powered Notes App — Built with Jetpack Compose & Clean Architecture

A beautifully crafted Android notes application that leverages **Google's Gemini API** to supercharge your note-taking experience. Summarize content, generate smart titles, fix grammar, and expand ideas — all powered by AI, all offline-first.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Clean Architecture](https://img.shields.io/badge/Clean%20Architecture-MVVM-00C853?style=for-the-badge)](https://developer.android.com/topic/architecture)
[![Gemini AI](https://img.shields.io/badge/Gemini%20AI-8E75B2?style=for-the-badge&logo=googlegemini&logoColor=white)](https://ai.google.dev/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

</div>

---

## 📸 Screenshots

### Light Mode

<p align="center">
  <img src="https://github.com/user-attachments/assets/76454eaa-b3e1-4f94-a525-1800963bd241" width="230" style="border: 2px solid #e1e4e8; border-radius: 12px; margin: 8px;" alt="Empty Home">
  &nbsp;&nbsp;
    <img src="https://github.com/user-attachments/assets/3913e017-7a5d-4d7d-a790-9ffb7974987d" width="230" style="border: 2px solid #30363d; border-radius: 12px; margin: 8px;" alt="Home with Notes">
  &nbsp;&nbsp;

  <img src="https://github.com/user-attachments/assets/a69512cb-af0e-49ee-8173-c00f8d4466c9" width="230" style="border: 2px solid #e1e4e8; border-radius: 12px; margin: 8px;" alt="Note Editor">
</p>

### Dark Mode

<p align="center">

   <img src="https://github.com/user-attachments/assets/420cfb72-9b90-48d6-98fe-7b390b71f860" width="230" style="border: 2px solid #e1e4e8; border-radius: 12px; margin: 8px;" alt="Home with Notes">
  &nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/9e57dc78-1d18-4af9-b193-52b45e22d919" width="230" style="border: 2px solid #30363d; border-radius: 12px; margin: 8px;" alt="List View">
  &nbsp;&nbsp;
  <img src="https://github.com/user-attachments/assets/34708e58-fa25-4298-ad87-3df15813cd2d" width="230" style="border: 2px solid #30363d; border-radius: 12px; margin: 8px;" alt="Note Editor">
</p>

## ✨ Features

| Feature | Description |
|---------|-------------|
| 🤖 **AI-Powered Tools** | Tidy with AI, Summarize, Fix Grammar, Make Checklist, Change Tone & Translate via Gemini API |
| 📝 **Full CRUD** | Create, read, edit & delete notes with smooth Compose animations |
| 🔍 **Smart Search** | Real-time search and filter across all your notes |
| 🏷️ **Category Tags** | Organize notes by Work, Personal, Travel, Ideas, Books & more |
| 🎨 **Color Coding** | Assign custom colors to notes for visual organization |
| 📷 **Smart Capture** | Capture and attach images directly to notes |
| 🌙 **Dark Mode** | Seamless dark/light theme with Material 3 dynamic colors |
| 💾 **Offline-First** | All data persisted locally with Room — no internet needed for core features |
| 👆 **Swipe Actions** | Intuitive swipe-to-delete gesture support |
| ⏰ **Reminders** | Set reminders with Today, Tomorrow or Custom Date |
| 📊 **Word Count** | Live word count & estimated read time |
| 📐 **Grid & List Views** | Toggle between grid and list layout |
| ⚡ **Reactive UI** | Kotlin Coroutines + Flow for smooth, responsive state management |

---

## 🏛️ Architecture

This project follows **Clean Architecture** with clear separation of concerns across three layers, combined with **MVVM** in the presentation layer.

```
┌─────────────────────────────────────────────────────┐
│                   Presentation                       │
│         (Screens, ViewModels, Components)            │
│            Jetpack Compose + MVVM                    │
├─────────────────────────────────────────────────────┤
│                     Domain                           │
│         (Use Cases, Models, Repository               │
│          Interfaces, AI Contracts)                   │
├─────────────────────────────────────────────────────┤
│                      Data                            │
│       (Room DB, DataStore, Gemini API,               │
│        Repository Implementations)                   │
└─────────────────────────────────────────────────────┘
```

---

## 📂 Project Structure

```
com.example.noteapp
│
├── feature_notes/
│   │
│   ├── data/                          # Data Layer
│   │   ├── ai/                        # Gemini AI service implementation
│   │   ├── data_source/               # Room DAOs & local data sources
│   │   ├── preferences/               # DataStore preferences
│   │   └── repository/                # Repository implementations
│   │
│   ├── di/                            # Dependency Injection
│   │   ├── AiModule                   # Hilt module for Gemini AI
│   │   └── AppModule.kt              # Hilt module for DB, repos & use cases
│   │
│   ├── domain/                        # Domain Layer
│   │   ├── ai/                        # AI service interfaces
│   │   ├── model/                     # Domain models (Note, NoteOrder, etc.)
│   │   ├── repository/                # Repository interfaces (contracts)
│   │   ├── use_case/                  # Business logic use cases
│   │   └── util/                      # Domain utilities
│   │
│   └── presentation/                  # Presentation Layer
│       ├── add_edit_note/             # Add/Edit Note screen
│       │   ├── components/            # Screen-specific composables
│       │   ├── AddEditNoteEvent.kt    # UI events
│       │   ├── AddEditNoteScreen.kt   # Compose screen
│       │   └── AddEditNoteViewModel.kt
│       ├── capture/                   # Image capture feature
│       ├── notes/                     # Notes list screen
│       └── util/                      # Presentation utilities
│
├── ui/                                # Theme, colors, typography
├── MainActivity.kt                    # Single Activity entry point
└── NoteApp.kt                         # Application class (Hilt entry)
```

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| **Language** | Kotlin |
| **UI Framework** | Jetpack Compose + Material 3 |
| **Architecture** | Clean Architecture + MVVM |
| **Dependency Injection** | Hilt (Dagger) |
| **Local Database** | Room |
| **Preferences** | Jetpack DataStore |
| **AI Integration** | Google Gemini API (Generative AI SDK) |
| **Async/Reactive** | Kotlin Coroutines + StateFlow |
| **Navigation** | Compose Navigation |
| **Build System** | Gradle (Kotlin DSL) |

---

## 🤖 AI Features (Gemini Integration)

NoteApp Aurora integrates Google's Gemini API through a clean, domain-driven approach:

```
User taps AI action → ViewModel → Use Case → AI Repository (interface)
                                                      ↓
                                              Gemini API Service (impl)
                                                      ↓
                                              Response flows back via StateFlow
```

| AI Action | What It Does |
|-----------|-------------|
| ✨ **Tidy with AI** | Cleans up and formats messy notes |
| ✍️ **Summarize** | Condenses long notes into key points |
| ✅ **Make Checklist** | Converts note content into actionable checklist items |
| 🔤 **Fix Grammar** | Corrects spelling and grammar errors |
| 🎭 **Change Tone** | Adjusts writing tone (formal, casual, etc.) |
| 🌐 **Translate** | Translates note content to other languages |

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** Hedgehog (2023.1.1) or later
- **JDK** 17+
- **Min SDK** 26 (Android 8.0)
- **Gemini API Key** — [Get one free here](https://aistudio.google.com/apikey)

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/harshpatel0909/NoteApp.git
cd NoteApp

# 2. Add your Gemini API key to local.properties
echo "GEMINI_API_KEY=your_api_key_here" >> local.properties

# 3. Open in Android Studio → Sync Gradle → Run ▶️
```

---

## 🔮 Roadmap

- [ ] ☁️ Cloud sync with Firebase Firestore
- [ ] 🏷️ Tags & folder organization
- [ ] 📝 Rich text editor (bold, lists, headings)
- [ ] 📌 Home screen widget for quick capture
- [ ] 🔔 Note reminders & notifications
- [ ] 📤 Export notes as PDF / Markdown

---

## 📄 License

```
MIT License

Copyright (c) 2026 Harsh Patel

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software.
```

See the full [LICENSE](LICENSE) file for details.

---

<div align="center">

### Built with ❤️ by Harsh Patel

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://linkedin.com/in/harsh-patel0909)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/harshpatel0909)

⭐ **Star this repo if you found it useful!**

</div>
