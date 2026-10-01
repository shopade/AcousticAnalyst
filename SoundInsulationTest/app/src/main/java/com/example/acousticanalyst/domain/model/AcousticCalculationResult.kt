package com.example.acousticanalyst.domain.model

/**
 * Complete acoustic insulation calculation results for a session.
 *
 * @property octaveBandType Octave band type (1/1 or 1/3 octave).
 * @property bandResults Calculated ISO 16283-1 results per frequency band.
 * @property rw Weighted sound reduction index Rw (ISO 717-1).
 * @property dnTw Weighted standardized level difference DnTw (ISO 717-1).
 * @property dw Weighted level difference Dw (ISO 717-1).
 */
data class AcousticCalculationResult(
    val octaveBandType: OctaveBandType,
    val bandResults: List<BandCalculationResult>,
    val rw: SingleNumberRatingResult? = null,
    val dnTw: SingleNumberRatingResult? = null,
    val dw: SingleNumberRatingResult? = null
)
