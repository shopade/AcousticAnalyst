package com.example.acousticanalyst.domain

import com.example.acousticanalyst.domain.calculator.Iso16283Calculator
import com.example.acousticanalyst.domain.model.BandMeasurement
import com.example.acousticanalyst.domain.model.RoomParameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.log10
import kotlin.math.pow

class Iso16283CalculatorTest {

    private val delta = 0.001

    @Test
    fun `correctL2 when B is null or zero should return uncorrected L2`() {
        val (correctedNull, infoNull) = Iso16283Calculator.correctL2(60.0, null)
        assertEquals(60.0, correctedNull, delta)
        assertFalse(infoNull.first)
        assertNull(infoNull.second)

        val (correctedZero, infoZero) = Iso16283Calculator.correctL2(60.0, 0.0)
        assertEquals(60.0, correctedZero, delta)
        assertFalse(infoZero.first)
        assertNull(infoZero.second)
    }

    @Test
    fun `correctL2 when L2 minus B is 10 dB or greater should return uncorrected L2`() {
        val (corrected, info) = Iso16283Calculator.correctL2(60.0, 50.0) // diff = 10 dB
        assertEquals(60.0, corrected, delta)
        assertFalse(info.first)
        assertNull(info.second)

        val (corrected15, info15) = Iso16283Calculator.correctL2(60.0, 45.0) // diff = 15 dB
        assertEquals(60.0, corrected15, delta)
        assertFalse(info15.first)
    }

    @Test
    fun `correctL2 when L2 minus B is between 6 and 10 dB should apply exact logarithmic subtraction`() {
        val l2 = 60.0
        val b = 52.0 // diff = 8 dB
        val expected = 10.0 * log10(10.0.pow(l2 / 10.0) - 10.0.pow(b / 10.0))

        val (corrected, info) = Iso16283Calculator.correctL2(l2, b)
        assertEquals(expected, corrected, delta)
        assertFalse(info.first)
        assertNull(info.second)
    }

    @Test
    fun `correctL2 when L2 minus B is less than 6 dB should apply max 1,3 dB correction and flag warning`() {
        val l2 = 60.0
        val b = 56.0 // diff = 4 dB (< 6 dB)

        val (corrected, info) = Iso16283Calculator.correctL2(l2, b)
        assertEquals(58.7, corrected, delta)
        assertTrue(info.first)
        assertNotNull(info.second)
        assertTrue(info.second!!.contains("within 6 dB"))
    }

    @Test
    fun `calculateBand calculates D correctly`() {
        val measurement = BandMeasurement(
            frequencyHz = 500,
            l1 = 90.0,
            l2 = 60.0,
            b = null
        )

        val result = Iso16283Calculator.calculateBand(measurement)
        assertEquals(500, result.frequencyHz)
        assertEquals(90.0, result.l1, delta)
        assertEquals(60.0, result.l2, delta)
        assertEquals(60.0, result.correctedL2, delta)
        assertEquals(30.0, result.d, delta)
        assertNull(result.dnT)
        assertNull(result.r)
    }

    @Test
    fun `calculateBand calculates DnT when reverberation time T is provided`() {
        val measurement = BandMeasurement(
            frequencyHz = 500,
            l1 = 90.0,
            l2 = 60.0,
            b = null,
            t = 1.0 // T = 1.0 s
        )

        val result = Iso16283Calculator.calculateBand(measurement)
        // D = 30.0 dB
        // DnT = 30.0 + 10 * log10(1.0 / 0.5) = 30.0 + 10 * log10(2.0) = 33.0103 dB
        val expectedDnT = 30.0 + 10.0 * log10(1.0 / 0.5)
        assertEquals(expectedDnT, result.dnT!!, delta)
    }

    @Test
    fun `calculateBand calculates R when room parameters S V and T are provided`() {
        val measurement = BandMeasurement(
            frequencyHz = 500,
            l1 = 90.0,
            l2 = 60.0,
            b = null,
            t = 0.5
        )
        val roomParams = RoomParameters(
            volumeV = 50.0, // V = 50 m^3
            areaS = 10.0    // S = 10 m^2
        )

        val result = Iso16283Calculator.calculateBand(measurement, roomParams)
        // D = 30.0
        // A = 0.16 * 50 / 0.5 = 16.0 m^2
        // R = 30.0 + 10 * log10(10.0 / 16.0) = 30.0 + 10 * log10(0.625) = 27.9588 dB
        val expectedR = 30.0 + 10.0 * log10((10.0 * 0.5) / (0.16 * 50.0))
        assertEquals(expectedR, result.r!!, delta)
    }
}
