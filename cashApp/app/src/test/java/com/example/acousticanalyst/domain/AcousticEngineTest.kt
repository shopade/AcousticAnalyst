package com.example.acousticanalyst.domain

import com.example.acousticanalyst.domain.calculator.AcousticEngine
import com.example.acousticanalyst.domain.model.BandMeasurement
import com.example.acousticanalyst.domain.model.OctaveBandDefaults
import com.example.acousticanalyst.domain.model.OctaveBandType
import com.example.acousticanalyst.domain.model.RoomParameters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class AcousticEngineTest {

    private val delta = 0.001

    @Test
    fun `calculate end to end for 1-1 octave band measurements`() {
        val freqs = OctaveBandDefaults.FREQUENCIES_1_1
        val refValues = OctaveBandDefaults.REF_VALUES_1_1

        val measurements = freqs.map { freq ->
            val ref = refValues.getValue(freq)
            BandMeasurement(
                frequencyHz = freq,
                l1 = 90.0,
                l2 = 90.0 - (ref + 50.0), // D = ref + 50.0
                b = null,
                t = 0.5
            )
        }

        val roomParams = RoomParameters(
            volumeV = 50.0,
            areaS = 10.0
        )

        val result = AcousticEngine.calculate(
            octaveBandType = OctaveBandType.OCTAVE_1_1,
            measurements = measurements,
            roomParameters = roomParams
        )

        assertEquals(OctaveBandType.OCTAVE_1_1, result.octaveBandType)
        assertEquals(5, result.bandResults.size)

        assertNotNull(result.dw)
        assertEquals(52, result.dw!!.ratingValue)

        assertNotNull(result.dnTw)
        assertEquals(52, result.dnTw!!.ratingValue)

        assertNotNull(result.rw)
    }

    @Test
    fun `calculate end to end for 1-3 octave band measurements`() {
        val freqs = OctaveBandDefaults.FREQUENCIES_1_3
        val refValues = OctaveBandDefaults.REF_VALUES_1_3

        val measurements = freqs.map { freq ->
            val ref = refValues.getValue(freq)
            BandMeasurement(
                frequencyHz = freq,
                l1 = 90.0,
                l2 = 90.0 - (ref + 50.0), // D = ref + 50.0
                b = 40.0, // background noise 40 dB (well below L2, so no correction needed)
                t = 0.5
            )
        }

        val result = AcousticEngine.calculate(
            octaveBandType = OctaveBandType.OCTAVE_1_3,
            measurements = measurements,
            roomParameters = null
        )

        assertEquals(OctaveBandType.OCTAVE_1_3, result.octaveBandType)
        assertEquals(16, result.bandResults.size)

        assertNotNull(result.dw)
        assertEquals(44, result.dw!!.ratingValue)

        assertNotNull(result.dnTw)
        assertEquals(44, result.dnTw!!.ratingValue)
    }

    @Test
    fun `aggregate1_3To1_1 correctly combines 1-3 octave bands into 1-1 octave bands`() {
        // Build 1/3 octave measurements
        val freqs1_3 = OctaveBandDefaults.FREQUENCIES_1_3
        val measurements1_3 = freqs1_3.map { freq ->
            BandMeasurement(
                frequencyHz = freq,
                l1 = 70.0,
                l2 = 50.0,
                b = 30.0,
                t = 0.6
            )
        }

        val aggregated = AcousticEngine.aggregate1_3To1_1(measurements1_3)
        assertEquals(5, aggregated.size)

        // For each 1/1 band,Energetic sum of 3 bands of 70 dB = 10 * log10(3 * 10^7) = 74.771 dB
        val expectedL1 = 10.0 * Math.log10(3.0 * Math.pow(10.0, 7.0))
        assertEquals(expectedL1, aggregated[0].l1, delta)
        assertEquals(0.6, aggregated[0].t!!, delta)
    }
}
