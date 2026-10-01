package com.example.acousticanalyst.domain.model

/**
 * Single-number rating result calculated according to ISO 717-1.
 *
 * @property ratingType Name of single-number rating (e.g. "Rw", "DnTw", "Dw").
 * @property ratingValue Rating value in dB (shifted reference curve value at 500 Hz).
 * @property sumUnfavorableDeviations Sum of unfavorable deviations (dB).
 * @property maxAllowedDeviations Maximum allowed sum of unfavorable deviations (10 dB for 1/1 octave, 32 dB for 1/3 octave).
 * @property shiftedReferenceCurve Map of frequency (Hz) to shifted reference curve value (dB).
 * @property measuredCurve Map of frequency (Hz) to measured spectrum value (dB).
 * @property cTerm Spectrum adaptation term C (dB).
 * @property ctrTerm Spectrum adaptation term Ctr (dB).
 */
data class SingleNumberRatingResult(
    val ratingType: String,
    val ratingValue: Int,
    val sumUnfavorableDeviations: Double,
    val maxAllowedDeviations: Double,
    val shiftedReferenceCurve: Map<Int, Double>,
    val measuredCurve: Map<Int, Double>,
    val cTerm: Int,
    val ctrTerm: Int
)
