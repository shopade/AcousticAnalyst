package com.example.acousticanalyst.domain.model

/**
 * Calculated ISO 16283-1 parameters for a single frequency band.
 *
 * @property frequencyHz Frequency in Hz.
 * @property l1 Measured L1 in source room (dB).
 * @property l2 Measured L2 in receiving room (dB).
 * @property b Measured background noise B (dB), optional.
 * @property correctedL2 L2' corrected for background noise according to ISO 16283-1 (dB).
 * @property hasBackgroundNoiseWarning True if L2 - B < 6 dB.
 * @property backgroundNoiseWarningMessage Descriptive warning message when L2 - B < 6 dB.
 * @property d Level difference D = L1 - L2' (dB).
 * @property dnT Standardized level difference DnT = D + 10 * log10(T / 0.5) (dB).
 * @property r Sound reduction index R = D + 10 * log10(S * T / (0.16 * V)) (dB).
 */
data class BandCalculationResult(
    val frequencyHz: Int,
    val l1: Double,
    val l2: Double,
    val b: Double?,
    val correctedL2: Double,
    val hasBackgroundNoiseWarning: Boolean,
    val backgroundNoiseWarningMessage: String? = null,
    val d: Double,
    val dnT: Double? = null,
    val r: Double? = null
)
