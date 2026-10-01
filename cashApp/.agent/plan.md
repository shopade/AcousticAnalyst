# Project Plan

Create an Android app named Acoustic Analyst that allows users to input sound pressure levels measured at either 1/1 octave or 1/3 octave frequencies for source room values (L1) and receiver room values (L2) in dB, reverberation time (T) in seconds, and background sound level (B) in dB. Optionally accept room volume (V in m³), partition/separating element area (S in m²), and reference reverberation time (T0 = 0.5s) if required. Calculate and display Sound Reduction Index (Rw), Level Difference (D), Standardized Level Difference (DnT), and Weighted Standardized Level Difference (DnTW) based on ISO 717-1 / ISO 16283-1 standards with the click of a button. Support 1/1 octave (125Hz to 4000Hz) and 1/3 octave (100Hz to 3150Hz / 5000Hz) frequency bands and ISO 717 reference contour curve fitting for weighted rating calculation.

## Project Brief

# Project Brief: Acoustic Analyst

## Features
- **Frequency Band & Measurement Input**: Input acoustic data for Source Room Level ($L_1$), Receiver Room Level ($L_2$), Reverberation Time ($T$), and Background Noise ($B$) across 1/1 octave (125 Hz – 4000 Hz) or 1/3 octave (100 Hz – 3150 Hz / 5000 Hz) frequency bands.
- **Room & Partition Configuration**: Configure room metrics including Receiver Room Volume ($V$ in $\text{m}^3$), Partition Separating Area ($S$ in $\text{m}^2$), and Reference Reverberation Time ($T_0 = 0.5\text{ s}$).
- **ISO 16283-1 Acoustic Calculations**: Compute Sound Reduction Index ($R$), Level Difference ($D$), and Standardized Level Difference ($D_{\text{nT}}$) with automatic background sound level corrections.
- **ISO 717-1 Reference Contour Curve Fitting**: Fit standard reference curves to derive weighted single-number ratings ($R_w$, $D_{\text{nT,w}}$, $D_w$) for sound insulation evaluation.

## High-Level Technical Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material 3 Design
- **Navigation & Adaptive Strategy**: Jetpack Navigation 3 (state-driven) and Compose Material Adaptive library (`androidx.compose.material3.adaptive`)
- **Architecture & Asynchronous Processing**: Android Architecture Components (ViewModel, StateFlow) and Kotlin Coroutines
- **Calculation Engine**: Pure Kotlin domain logic implementing ISO 717-1 contour shift algorithms and ISO 16283-1 formulas

## Implementation Steps
**Total Duration:** 43h 23m 55s

### Task_1_DomainLogicAndEngine: Implement core data models for 1/1 and 1/3 octave bands and room parameters, along with calculation logic for ISO 16283-1 (R, D, DnT with background noise correction) and ISO 717-1 reference curve fitting (Rw, DnTw, Dw).
- **Status:** COMPLETED
- **Updates:** Implemented core data models (OctaveBandType, RoomParameters, BandMeasurement, BandCalculationResult, SingleNumberRatingResult), ISO 16283-1 acoustic calculations (D, DnT, R with background noise correction), ISO 717-1 contour shift algorithm for 1/1 and 1/3 octave bands (Rw, DnTw, Dw), and unit tests. All 15 unit tests pass and build succeeds.
- **Acceptance Criteria:**
  - ISO 16283-1 calculation logic correctly calculates R, D, DnT with background noise correction
  - ISO 717-1 contour shift algorithm correctly determines single-number ratings Rw, DnTw, Dw
  - unit tests pass for domain calculation logic
  - build pass
- **Duration:** 25h 44m 50s

### Task_2_ViewModelAndState: Implement ViewModel, UI state, and state management for room configuration (V, S, T0) and frequency measurement inputs (L1, L2, T, B) with live calculation triggers.
- **Status:** COMPLETED
- **Updates:** Implemented AcousticUiState and AcousticViewModel with StateFlow state management. Supports switching between 1/1 and 1/3 octave bands, room parameters (V, S, T0), measurement inputs (L1, L2, T, B), reactive calculation engine triggers, sample data population, clear inputs, and ViewModel unit tests. All 25 unit tests pass and build succeeds.
- **Acceptance Criteria:**
  - ViewModel state correctly captures octave band mode and frequency measurement inputs
  - reactive calculation updates occur on input changes
  - build pass
- **Duration:** 9m 8s

### Task_3_ComposeUIAndNavigation: Build Jetpack Compose Material 3 UI screens for room parameters, octave band measurement tables, and results display with ratings (Rw, DnTw, Dw) and curve visualization.
- **Status:** COMPLETED
- **Updates:** Implemented Jetpack Compose Material 3 UI for Acoustic Analyst: InputScreen with room parameters and octave band measurement tables, ResultsScreen displaying Rw, DnTw, Dw cards and frequency breakdown with warning badges, ChartScreen with custom Compose Canvas rendering measured acoustic curves vs shifted ISO 717 reference contours, and MainScreen navigation bar. Updated MainActivity. All unit tests pass and assembleDebug builds successfully.
- **Acceptance Criteria:**
  - Compose UI implemented for data input table and room configuration
  - Results view displays single-number ratings and acoustic curve details
  - Navigation between input and result views functions smoothly
  - build pass
  - app does not crash
- **Duration:** 6m 46s

### Task_4_PolishAndVerify: Add app icon, apply Material 3 design polish, and perform final run and verification. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** Completed Task 4: Added app icon, Material 3 theme polish, ISO 717-1 C and Ctr spectrum adaptation term calculation and display ($R'_w(C; C_{tr})$ and $D_{n,T,w}(C; C_{tr})$), verified build success and unit test suite (all 25+ unit tests passing).
- **Acceptance Criteria:**
  - app icon and Material 3 theme polish added
  - make sure all existing tests pass
  - build pass
  - app does not crash
  - critic_agent verifies application stability (no crashes), confirms alignment with user requirements, and reports critical UI issues
- **Duration:** 17h 23m 11s

