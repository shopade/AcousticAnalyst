package com.example.acousticanalyst.domain.calculator

import com.example.acousticanalyst.domain.model.AcousticCalculationResult
import com.example.acousticanalyst.domain.model.BandMeasurement
import com.example.acousticanalyst.domain.model.OctaveBandType
import com.example.acousticanalyst.domain.model.RoomParameters
import kotlin.math.log10
import kotlin.math.pow

object AcousticEngine {

    /**
     * Executes complete acoustic insulation calculation pipeline:
     * 1. ISO 16283-1 calculation per band (L2' background noise correction, D, DnT, R).
     * 2. ISO 717-1 single-number rating curve fitting (Dw, DnTw, Rw).
     */
    fun calculate(
        octaveBandType: OctaveBandType,
        measurements: List<BandMeasurement>,
        roomParameters: RoomParameters? = null
    ): AcousticCalculationResult {
        val bandResults = Iso16283Calculator.calculateAll(measurements, roomParameters)

        val dMap = bandResults.associate { it.frequencyHz to it.d }

        val dnTMap = bandResults.mapNotNull { res ->
            res.dnT?.let { res.frequencyHz to it }
        }.toMap()

        val rMap = bandResults.mapNotNull { res ->
            res.r?.let { res.frequencyHz to it }
        }.toMap()

        val dw = Iso717Calculator.calculateSingleNumberRating("Dw", octaveBandType, dMap)

        val dnTw = if (dnTMap.isNotEmpty()) {
            Iso717Calculator.calculateSingleNumberRating("DnTw", octaveBandType, dnTMap)
        } else null

        val rw = if (rMap.isNotEmpty()) {
            Iso717Calculator.calculateSingleNumberRating("Rw", octaveBandType, rMap)
        } else null

        return AcousticCalculationResult(
            octaveBandType = octaveBandType,
            bandResults = bandResults,
            rw = rw,
            dnTw = dnTw,
            dw = dw
        )
    }

    /**
     * Helper to aggregate 1/3 octave band measurements into 1/1 octave band measurements.
     */
    fun aggregate1_3To1_1(measurements1_3: List<BandMeasurement>): List<BandMeasurement> {
        val bands1_1Map = mapOf(
            125 to listOf(100, 125, 160),
            250 to listOf(200, 250, 315),
            500 to listOf(400, 500, 630),
            1000 to listOf(800, 1000, 1250),
            2000 to listOf(1600, 2000, 2500)
        )

        val measByFreq = measurements1_3.associateBy { it.frequencyHz }

        return bands1_1Map.mapNotNull { (centerFreq, subFreqs) ->
            val subMeas = subFreqs.mapNotNull { measByFreq[it] }
            if (subMeas.isEmpty()) return@mapNotNull null

            val l1Sum = 10.0 * log10(subMeas.sumOf { 10.0.pow(it.l1 / 10.0) })
            val l2Sum = 10.0 * log10(subMeas.sumOf { 10.0.pow(it.l2 / 10.0) })

            val bMeas = subMeas.mapNotNull { it.b }
            val bSum = if (bMeas.size == subMeas.size && bMeas.all { it > 0.0 }) {
                10.0 * log10(bMeas.sumOf { 10.0.pow(it / 10.0) })
            } else null

            val tMeas = subMeas.mapNotNull { it.t }
            val tAvg = if (tMeas.isNotEmpty()) {
                tMeas.average()
            } else null

            BandMeasurement(
                frequencyHz = centerFreq,
                l1 = l1Sum,
                l2 = l2Sum,
                b = bSum,
                t = tAvg
            )
        }
    }
}
