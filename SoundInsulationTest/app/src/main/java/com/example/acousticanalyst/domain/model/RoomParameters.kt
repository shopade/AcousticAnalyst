package com.example.acousticanalyst.domain.model

/**
 * Physical room parameters for acoustic insulation calculations.
 *
 * @property volumeV Receiving room volume V in cubic meters (m³).
 * @property areaS Separating partition area S in square meters (m²).
 * @property defaultReverberationTimeT Default reverberation time T in seconds if band-specific value is absent.
 * @property reverberationTimeMap Map of frequency in Hz to reverberation time T in seconds.
 */
data class RoomParameters(
    val volumeV: Double? = null,
    val areaS: Double? = null,
    val defaultReverberationTimeT: Double? = null,
    val reverberationTimeMap: Map<Int, Double> = emptyMap()
) {
    /**
     * Retrieves reverberation time T for a given frequency, falling back to defaultReverberationTimeT.
     */
    fun getReverberationTimeForFrequency(freqHz: Int): Double? {
        return reverberationTimeMap[freqHz] ?: defaultReverberationTimeT
    }
}
