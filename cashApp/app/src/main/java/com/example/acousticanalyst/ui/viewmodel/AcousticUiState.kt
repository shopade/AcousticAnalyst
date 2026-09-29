package com.example.acousticanalyst.ui.viewmodel

import com.example.acousticanalyst.domain.model.AcousticCalculationResult
import com.example.acousticanalyst.domain.model.OctaveBandType
import com.example.acousticanalyst.domain.model.RoomParameters

/**
 * UI State for raw text inputs of a single frequency band.
 *
 * @property frequencyHz Frequency in Hz.
 * @property l1Input Raw input string for L1 (dB).
 * @property l2Input Raw input string for L2 (dB).
 * @property tInput Raw input string for reverberation time T (s), optional.
 * @property bInput Raw input string for background noise B (dB), optional.
 */
data class BandInputState(
    val frequencyHz: Int,
    val l1Input: String = "",
    val l2Input: String = "",
    val tInput: String = "",
    val bInput: String = ""
) {
    val l1: Double? get() = l1Input.toDoubleOrNull()
    val l2: Double? get() = l2Input.toDoubleOrNull()
    val t: Double? get() = tInput.toDoubleOrNull()
    val b: Double? get() = bInput.toDoubleOrNull()

    val hasValidLevels: Boolean get() = l1 != null && l2 != null
}

/**
 * UI State for room configuration parameters.
 *
 * @property volumeVInput Raw input string for Receiver Room Volume V (m³).
 * @property areaSInput Raw input string for Separating Partition Area S (m²).
 * @property referenceT0Input Raw input string for Reference Reverberation Time T0 (s, default 0.5s).
 */
data class RoomParametersInputState(
    val volumeVInput: String = "",
    val areaSInput: String = "",
    val referenceT0Input: String = "0.5"
) {
    val volumeV: Double? get() = volumeVInput.toDoubleOrNull()
    val areaS: Double? get() = areaSInput.toDoubleOrNull()
    val referenceT0: Double get() = referenceT0Input.toDoubleOrNull() ?: 0.5

    fun toRoomParameters(): RoomParameters {
        return RoomParameters(
            volumeV = volumeV,
            areaS = areaS,
            defaultReverberationTimeT = referenceT0
        )
    }
}

/**
 * Overall UI State for acoustic measurement and analysis.
 *
 * @property octaveBandType Selected resolution (1/1 Octave vs 1/3 Octave).
 * @property roomParametersInput Room parameters state (V, S, T0).
 * @property bandInputs Active list of frequency band inputs for current octave mode.
 * @property calculationResult Result of ISO 16283-1 and ISO 717-1 calculation if valid inputs exist.
 * @property isCalculationValid True if all required frequency bands have valid L1 and L2 levels.
 * @property validationErrorMessage Message explaining why calculation is incomplete or invalid.
 */
data class AcousticUiState(
    val octaveBandType: OctaveBandType = OctaveBandType.OCTAVE_1_1,
    val roomParametersInput: RoomParametersInputState = RoomParametersInputState(),
    val bandInputs: List<BandInputState> = emptyList(),
    val calculationResult: AcousticCalculationResult? = null,
    val isCalculationValid: Boolean = false,
    val validationErrorMessage: String? = null
)
