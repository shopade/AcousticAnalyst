package com.example.acousticanalyst.domain.calculator

import com.example.acousticanalyst.domain.model.OctaveBandDefaults
import com.example.acousticanalyst.domain.model.OctaveBandType
import com.example.acousticanalyst.domain.model.SingleNumberRatingResult
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt

object Iso717Calculator {

    private const val EPSILON = 1e-9

    /**
     * Calculates single-number rating (Rw, DnTw, Dw) according to ISO 717-1 contour shift algorithm.
     *
     * @param ratingType Name of the rating (e.g., "Rw", "DnTw", "Dw").
     * @param octaveBandType Resolution type (1/1 or 1/3 octave).
     * @param measuredValues Map of frequency in Hz to measured insulation value (dB).
     * @return [SingleNumberRatingResult] or null if required frequency bands are missing.
     */
    fun calculateSingleNumberRating(
        ratingType: String,
        octaveBandType: OctaveBandType,
        measuredValues: Map<Int, Double>
    ): SingleNumberRatingResult? {
        val (frequencies, refValues, maxAllowedDev) = when (octaveBandType) {
            OctaveBandType.OCTAVE_1_1 -> Triple(
                OctaveBandDefaults.FREQUENCIES_1_1,
                OctaveBandDefaults.REF_VALUES_1_1,
                OctaveBandDefaults.MAX_DEVIATION_1_1
            )
            OctaveBandType.OCTAVE_1_3 -> Triple(
                OctaveBandDefaults.FREQUENCIES_1_3,
                OctaveBandDefaults.REF_VALUES_1_3,
                OctaveBandDefaults.MAX_DEVIATION_1_3
            )
        }

        // Verify all required frequencies are present in measuredValues
        for (freq in frequencies) {
            if (!measuredValues.containsKey(freq) || measuredValues[freq] == null) {
                return null
            }
        }

        // Find starting shift value S
        val minDiff = frequencies.minOf { freq ->
            measuredValues.getValue(freq) - refValues.getValue(freq)
        }
        val startShift = floor(minDiff).toInt() - 10

        var bestShift = startShift
        var bestDeviationsSum = 0.0
        var currentShift = startShift

        val maxShift = startShift + 300

        while (currentShift <= maxShift) {
            var currentDevSum = 0.0
            for (freq in frequencies) {
                val refVal = refValues.getValue(freq) + currentShift
                val measVal = measuredValues.getValue(freq)
                if (measVal < refVal) {
                    currentDevSum += (refVal - measVal)
                }
            }

            if (currentDevSum <= maxAllowedDev + EPSILON) {
                bestShift = currentShift
                bestDeviationsSum = currentDevSum
                currentShift++
            } else {
                break
            }
        }

        val ref500HzBase = refValues.getValue(500)
        val ratingAt500Hz = (ref500HzBase + bestShift).roundToInt()

        val (weightsC, weightsCtr) = when (octaveBandType) {
            OctaveBandType.OCTAVE_1_1 -> Pair(
                OctaveBandDefaults.WEIGHTS_C_1_1,
                OctaveBandDefaults.WEIGHTS_CTR_1_1
            )
            OctaveBandType.OCTAVE_1_3 -> Pair(
                OctaveBandDefaults.WEIGHTS_C_1_3,
                OctaveBandDefaults.WEIGHTS_CTR_1_3
            )
        }

        val sumC = frequencies.sumOf { freq ->
            10.0.pow((measuredValues.getValue(freq) - weightsC.getValue(freq)) / 10.0)
        }
        val cTerm = (-10.0 * log10(sumC) - ratingAt500Hz).roundToInt()

        val sumCtr = frequencies.sumOf { freq ->
            10.0.pow((measuredValues.getValue(freq) - weightsCtr.getValue(freq)) / 10.0)
        }
        val ctrTerm = (-10.0 * log10(sumCtr) - ratingAt500Hz).roundToInt()

        val shiftedCurve = frequencies.associateWith { freq ->
            refValues.getValue(freq) + bestShift
        }

        val filteredMeasuredCurve = frequencies.associateWith { freq ->
            measuredValues.getValue(freq)
        }

        return SingleNumberRatingResult(
            ratingType = ratingType,
            ratingValue = ratingAt500Hz,
            sumUnfavorableDeviations = bestDeviationsSum,
            maxAllowedDeviations = maxAllowedDev,
            shiftedReferenceCurve = shiftedCurve,
            measuredCurve = filteredMeasuredCurve,
            cTerm = cTerm,
            ctrTerm = ctrTerm
        )
    }
}
