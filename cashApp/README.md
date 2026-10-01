# Acoustic Analyst 🎵📊

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Material 3](https://img.shields.io/badge/Material-3-757575?style=flat-square&logo=materialdesign&logoColor=white)](https://m3.material.io)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

**Acoustic Analyst** is a professional-grade Android application designed for acoustic consultants, sound engineers, and architects. It streamlines complex field acoustic measurements, background noise corrections, and single-number acoustic rating evaluations in strict compliance with **ISO 16283-1** and **ISO 717-1** international standards.

---

## ✨ Key Features & Capabilities

- **ISO-Compliant Calculation Engines**: Automated processing for sound level differences ($D$), standardized level differences ($D_{nT}$), and sound reduction indices ($R$) with rigorous background noise ($L_{2}'$) and reverberation time ($T_{60}$) corrections.
- **Single-Number Ratings**: Automatic ISO 717-1 curve fitting and rating evaluation ($D_w$, $D_{nT,w}$, $R_w$) adhering to unfavorable deviation thresholds ($\le 32.0\text{ dB}$).
- **Flexible Octave Band Analysis**: Supports both **1/1 Octave Bands** (125 Hz – 2000 Hz) and **1/3 Octave Bands** (100 Hz – 3150 Hz) with built-in 1/3-to-1/1 band aggregation.
- **Interactive Data Visualization**: Custom canvas-rendered acoustic frequency response charts (`AcousticChartCanvas`) comparing measured data against ISO reference curves.
- **Robust Persistence & State**: Reactive UI architecture powered by Kotlin Coroutines, `StateFlow`, Room Database, and Jetpack DataStore.

---

## 🏛️ Architecture & Tech Stack

This project is built following modern Android engineering standards and Clean Architecture principles:

- **UI & Presentation**: 100% **Jetpack Compose** with **Material 3**, custom Canvas drawing, and adaptive layouts supporting phones and tablets.
- **Architecture**: **MVVM (Model-View-ViewModel)** with Unidirectional Data Flow (UDF) and StateFlow.
- **Domain Logic**: Pure Kotlin calculation engines completely decoupled from Android framework dependencies, ensuring high testability and modularity.
- **Persistence**: **Room Database** for local caching and **DataStore** for user preferences.
- **Testing**: Comprehensive unit test suite covering complex acoustic algorithms and ViewModel state handling.

---

## 📂 Project Structure

```text
com.example.acousticanalyst/
├── domain/
│   ├── calculator/          # Pure Kotlin ISO 16283 & ISO 717 calculation engines
│   └── model/               # Immutable data models & octave band definitions
├── ui/
│   ├── components/          # Reusable Material 3 components & custom canvas charts
│   ├── screens/             # InputScreen, ChartScreen, ResultsScreen, MainScreen
│   ├── theme/               # Material 3 Color, Typography, and Theme
│   └── viewmodel/           # AcousticViewModel & reactive UI state management
└── MainActivity.kt          # Single-activity architecture entry point
```

---

## 🧪 Quality Assurance & Testing

Acoustic Analyst features a robust testing suite verifying both algorithmic precision and UI reactivity:
- **Unit Tests**: Validates ISO calculation accuracy against standard reference datasets and edge cases.
- **ViewModel Tests**: Ensures correct state transitions, error handling, and reactive data flow.

Run tests locally via Gradle:
```bash
# Run unit tests
./gradlew testDebugUnitTest
```

---

## 🚀 Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/your-username/acoustic-analyst.git
   ```
2. **Open in Android Studio**: Open the project folder in Android Studio Ladybug or newer.
3. **Build & Run**: Sync Gradle and run the app on an emulator or physical device (`minSdk 29`, `targetSdk 37`).

---

## 📄 License

Distributed under the **MIT License**. See [LICENSE](LICENSE) for more information.
