package com.example.acousticanalyst.domain

import com.example.acousticanalyst.domain.calculator.Iso717Calculator
import com.example.acousticanalyst.domain.model.OctaveBandDefaults
import com.example.acousticanalyst.domain.model.OctaveBandType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class Iso717CalculatorTest {

    private val delta = 0.001

    @Test
    fun `calculateSingleNumberRating for 1-1 octave with exact ref curve offset`() {
        // Build measured values as reference curve + 50 dB
        val refMap = OctaveBandDefaults.REF_VALUES_1_1
        val measuredMap = refMap.mapValues { it.value + 50.0 }

        val result = Iso717Calculator.calculateSingleNumberRating(
            ratingType = "Rw",
            octaveBandType = OctaveBandType.OCTAVE_1_1,
            measuredValues = measuredMap
        )

        assertNotNull(result)
        // Shift 52 dB gives unfavorable deviation = 2.0 dB * 5 bands = 10.0 dB <= 10.0 dB max allowed
        assertEquals(52, result!!.ratingValue)
        assertEquals(10.0, result.sumUnfavorableDeviations, delta)
        assertEquals(10.0, result.maxAllowedDeviations, delta)
        assertEquals(52.0, result.shiftedReferenceCurve[500]!!, delta)
    }

    @Test
    fun `calculateSingleNumberRating for 1-1 octave with flat spectrum`() {
        val flatVal = 50.0
        val measuredMap = OctaveBandDefaults.FREQUENCIES_1_1.associateWith { flatVal }

        val result = Iso717Calculator.calculateSingleNumberRating(
            ratingType = "Dw",
            octaveBandType = OctaveBandType.OCTAVE_1_1,
            measuredValues = measuredMap
        )

        assertNotNull(result)
        assertEquals(51, result!!.ratingValue)
        assertEquals(10.0, result.sumUnfavorableDeviations, delta)
        // Verify C and Ctr terms are calculated and non-zero
        assertEquals(-116, result.cTerm)
        assertEquals(-123, result.ctrTerm)
    }

    @Test
    fun `calculateSingleNumberRating for 1-3 octave with exact ref curve offset`() {
        val refMap = OctaveBandDefaults.REF_VALUES_1_3
        val measuredMap = refMap.mapValues { it.value + 50.0 }

        val result = Iso717Calculator.calculateSingleNumberRating(
            ratingType = "Rw",
            octaveBandType = OctaveBandType.OCTAVE_1_3,
            measuredValues = measuredMap
        )

        assertNotNull(result)
        // Ref at 500 Hz is -8 dB. Shift 52 dB gives shifted 500 Hz value = -8 + 52 = 44 dB.
        // Unfavorable deviation = 2 dB * 16 = 32.0 dB <= 32.0 dB.
        assertEquals(44, result!!.ratingValue)
        assertEquals(32.0, result.sumUnfavorableDeviations, delta)
        assertEquals(32.0, result.maxAllowedDeviations, delta)
        assertEquals(44.0, result.shiftedReferenceCurve[500]!!, delta)
    }

    @Test
    fun `calculateSingleNumberRating returns null when required frequencies are missing`() {
        val incompleteMap = mapOf(125 to 50.0, 250 to 50.0, 500 to 50.0) // missing 1000 and 2000 Hz

        val result = Iso717Calculator.calculateSingleNumberRating(
            ratingType = "Rw",
            octaveBandType = OctaveBandType.OCTAVE_1_1,
            measuredValues = incompleteMap
        )

        assertNull(result)
    }
}
