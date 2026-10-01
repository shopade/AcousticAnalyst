package com.example.acousticanalyst.domain.calculator

import com.example.acousticanalyst.domain.model.BandCalculationResult
import com.example.acousticanalyst.domain.model.BandMeasurement
import com.example.acousticanalyst.domain.model.RoomParameters
import kotlin.math.log10
import kotlin.math.pow

object Iso16283Calculator {

    /** Standard reference reverberation time T0 = 0.5 s */
    const val T0 = 0.5

    /**
     * Corrects receiving room level L2 for background noise B according to ISO 16283-1.
     *
     * Rules:
     * - If B is null, zero, or empty/<= 0: L2' = L2 (no correction).
     * - If L2 - B >= 10 dB: L2' = L2 (no correction needed).
     * - If 6 dB <= L2 - B < 10 dB: L2' = 10 * log10(10^(L2/10) - 10^(B/10)) (exact logarithmic subtraction).
     * - If L2 - B < 6 dB: L2' = L2 - 1.3 dB (maximum correction of 1.3 dB) and sets warning flag.
     *
     * @return Pair containing corrected L2' and warning info Pair(hasWarning, warningMessage).
     */
    fun correctL2(l2: Double, b: Double?): Pair<Double, Pair<Boolean, String?>> {
        if (b == null || b <= 0.0) {
            return Pair(l2, Pair(false, null))
        }
        val diff = l2 - b
        return when {
            diff >= 10.0 -> {
                Pair(l2, Pair(false, null))
            }
            diff >= 6.0 -> {
                val corrected = 10.0 * log10(10.0.pow(l2 / 10.0) - 10.0.pow(b / 10.0))
                Pair(corrected, Pair(false, null))
            }
            else -> {
                val corrected = l2 - 1.3
                val warningMsg = "Background noise B ($b dB) is within 6 dB of signal level L2 ($l2 dB), L2 - B = ${"%.1f".format(diff)} dB. Applied maximum 1.3 dB correction."
                Pair(corrected, Pair(true, warningMsg))
            }
        }
    }

    /**
     * Calculates ISO 16283-1 parameters (D, DnT, R) for a single frequency band.
     */
    fun calculateBand(
        measurement: BandMeasurement,
        roomParameters: RoomParameters? = null
    ): BandCalculationResult {
        val (correctedL2, warningInfo) = correctL2(measurement.l2, measurement.b)
        val (hasWarning, warningMessage) = warningInfo

        val d = measurement.l1 - correctedL2

        val t = measurement.t
            ?: roomParameters?.getReverberationTimeForFrequency(measurement.frequencyHz)

        val dnT = if (t != null && t > 0.0) {
            d + 10.0 * log10(t / T0)
        } else null

        val r = if (t != null && t > 0.0 &&
            roomParameters?.areaS != null && roomParameters.areaS > 0.0 &&
            roomParameters.volumeV != null && roomParameters.volumeV > 0.0
        ) {
            val s = roomParameters.areaS
            val v = roomParameters.volumeV
            // Equivalent absorption area A = 0.16 * V / T
            // Sound Reduction Index R = D + 10 * log10(S / A) = D + 10 * log10(S * T / (0.16 * V))
            d + 10.0 * log10((s * t) / (0.16 * v))
        } else null

        return BandCalculationResult(
            frequencyHz = measurement.frequencyHz,
            l1 = measurement.l1,
            l2 = measurement.l2,
            b = measurement.b,
            correctedL2 = correctedL2,
            hasBackgroundNoiseWarning = hasWarning,
            backgroundNoiseWarningMessage = warningMessage,
            d = d,
            dnT = dnT,
            r = r
        )
    }

    /**
     * Calculates ISO 16283-1 parameters for all given band measurements.
     */
    fun calculateAll(
        measurements: List<BandMeasurement>,
        roomParameters: RoomParameters? = null
    ): List<BandCalculationResult> {
        return measurements.map { calculateBand(it, roomParameters) }
    }
}
