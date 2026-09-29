package com.example.acousticanalyst.domain.model

object OctaveBandDefaults {

    /**
     * Standard 1/1 octave frequencies (Hz) for ISO 717-1 rating.
     */
    val FREQUENCIES_1_1 = listOf(125, 250, 500, 1000, 2000)

    /**
     * ISO 717-1 reference values for 1/1 octave bands (dB relative to 500 Hz value).
     */
    val REF_VALUES_1_1 = mapOf(
        125 to -16.0,
        250 to -7.0,
        500 to 0.0,
        1000 to 3.0,
        2000 to 4.0
    )

    /**
     * Maximum allowed sum of unfavorable deviations for 1/1 octave bands (5 bands).
     */
    const val MAX_DEVIATION_1_1 = 10.0

    /**
     * Standard 1/3 octave frequencies (Hz) for ISO 717-1 rating (16 bands from 100 Hz to 3150 Hz).
     */
    val FREQUENCIES_1_3 = listOf(
        100, 125, 160, 200, 250, 315, 400, 500,
        630, 800, 1000, 1250, 1600, 2000, 2500, 3150
    )

    /**
     * ISO 717-1 reference values for 1/3 octave bands (dB).
     */
    val REF_VALUES_1_3 = mapOf(
        100 to -29.0,
        125 to -26.0,
        160 to -23.0,
        200 to -20.0,
        250 to -17.0,
        315 to -14.0,
        400 to -11.0,
        500 to -8.0,
        630 to -7.0,
        800 to -6.0,
        1000 to -5.0,
        1250 to -4.0,
        1600 to -3.0,
        2000 to -3.0,
        2500 to -3.0,
        3150 to -3.0
    )

    /**
     * Maximum allowed sum of unfavorable deviations for 1/3 octave bands (16 bands).
     */
    const val MAX_DEVIATION_1_3 = 32.0

    /**
     * ISO 717-1 spectrum adaptation weights A_Ci for C (1/1 octave).
     */
    val WEIGHTS_C_1_1 = mapOf(
        125 to -14.0,
        250 to -5.0,
        500 to 0.0,
        1000 to 0.0,
        2000 to 0.0
    )

    /**
     * ISO 717-1 spectrum adaptation weights A_Ctri for Ctr (1/1 octave).
     */
    val WEIGHTS_CTR_1_1 = mapOf(
        125 to -5.0,
        250 to -8.0,
        500 to -12.0,
        1000 to -16.0,
        2000 to -20.0
    )

    /**
     * ISO 717-1 spectrum adaptation weights A_Ci for C (1/3 octave, 100 Hz to 3150 Hz).
     */
    val WEIGHTS_C_1_3 = mapOf(
        100 to -19.0,
        125 to -16.0,
        160 to -13.0,
        200 to -10.0,
        250 to -7.0,
        315 to -5.0,
        400 to -3.0,
        500 to -2.0,
        630 to -1.0,
        800 to 0.0,
        1000 to 0.0,
        1250 to 0.0,
        1600 to 0.0,
        2000 to 0.0,
        2500 to 0.0,
        3150 to 0.0
    )

    /**
     * ISO 717-1 spectrum adaptation weights A_Ctri for Ctr (1/3 octave, 100 Hz to 3150 Hz).
     */
    val WEIGHTS_CTR_1_3 = mapOf(
        100 to -5.0,
        125 to -6.0,
        160 to -7.0,
        200 to -8.0,
        250 to -9.0,
        315 to -11.0,
        400 to -12.0,
        500 to -14.0,
        630 to -15.0,
        800 to -16.0,
        1000 to -18.0,
        1250 to -19.0,
        1600 to -20.0,
        2000 to -22.0,
        2500 to -23.0,
        3150 to -25.0
    )
}
