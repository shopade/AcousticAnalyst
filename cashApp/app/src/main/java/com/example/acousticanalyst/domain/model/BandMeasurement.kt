package com.example.acousticanalyst.domain.model

/**
 * Raw sound pressure level measurements for a specific frequency band.
 *
 * @property frequencyHz Center frequency of octave or 1/3 octave band in Hz.
 * @property l1 Average sound pressure level in source room L1 (dB).
 * @property l2 Average sound pressure level in receiving room L2 (dB).
 * @property b Background noise level in receiving room B (dB), optional.
 * @property t Reverberation time T in receiving room (seconds), optional.
 */
data class BandMeasurement(
    val frequencyHz: Int,
    val l1: Double,
    val l2: Double,
    val b: Double? = null,
    val t: Double? = null
)
