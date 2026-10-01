package com.example.acousticanalyst.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acousticanalyst.domain.calculator.AcousticEngine
import com.example.acousticanalyst.domain.model.BandMeasurement
import com.example.acousticanalyst.domain.model.OctaveBandDefaults
import com.example.acousticanalyst.domain.model.OctaveBandType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class AcousticViewModel : ViewModel() {

    private val _octaveBandType = MutableStateFlow(OctaveBandType.OCTAVE_1_1)
    private val _roomParametersInput = MutableStateFlow(RoomParametersInputState())
    private val _bandInputsMap = MutableStateFlow<Map<Int, BandInputState>>(
        initializeBandInputsMap()
    )

    val uiState: StateFlow<AcousticUiState> = combine(
        _octaveBandType,
        _roomParametersInput,
        _bandInputsMap
    ) { mode, roomParams, inputsMap ->
        val activeFrequencies = when (mode) {
            OctaveBandType.OCTAVE_1_1 -> OctaveBandDefaults.FREQUENCIES_1_1
            OctaveBandType.OCTAVE_1_3 -> OctaveBandDefaults.FREQUENCIES_1_3
        }

        val activeBandInputs = activeFrequencies.map { freq ->
            inputsMap[freq] ?: BandInputState(frequencyHz = freq)
        }

        val validMeasurements = activeBandInputs.mapNotNull { input ->
            val l1 = input.l1
            val l2 = input.l2
            if (l1 != null && l2 != null) {
                BandMeasurement(
                    frequencyHz = input.frequencyHz,
                    l1 = l1,
                    l2 = l2,
                    b = input.b,
                    t = input.t
                )
            } else null
        }

        val allBandsPresent = validMeasurements.size == activeFrequencies.size

        val roomParamsDomain = roomParams.toRoomParameters()

        val calculationResult = if (validMeasurements.isNotEmpty()) {
            AcousticEngine.calculate(
                octaveBandType = mode,
                measurements = validMeasurements,
                roomParameters = roomParamsDomain
            )
        } else null

        val validationErrorMessage = when {
            validMeasurements.isEmpty() -> "Enter L1 and L2 measurements to begin calculation."
            !allBandsPresent -> "Incomplete frequency data. ${validMeasurements.size}/${activeFrequencies.size} bands filled."
            else -> null
        }

        AcousticUiState(
            octaveBandType = mode,
            roomParametersInput = roomParams,
            bandInputs = activeBandInputs,
            calculationResult = calculationResult,
            isCalculationValid = allBandsPresent && calculationResult != null,
            validationErrorMessage = validationErrorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AcousticUiState(
            octaveBandType = OctaveBandType.OCTAVE_1_1,
            roomParametersInput = RoomParametersInputState(),
            bandInputs = OctaveBandDefaults.FREQUENCIES_1_1.map { BandInputState(it) },
            validationErrorMessage = "Enter L1 and L2 measurements to begin calculation."
        )
    )

    fun setOctaveBandType(type: OctaveBandType) {
        _octaveBandType.value = type
    }

    fun setVolumeV(volume: String) {
        _roomParametersInput.value = _roomParametersInput.value.copy(volumeVInput = volume)
    }

    fun setAreaS(area: String) {
        _roomParametersInput.value = _roomParametersInput.value.copy(areaSInput = area)
    }

    fun setReferenceT0(t0: String) {
        _roomParametersInput.value = _roomParametersInput.value.copy(referenceT0Input = t0)
    }

    fun setBandL1(frequencyHz: Int, l1: String) {
        updateBandInput(frequencyHz) { it.copy(l1Input = l1) }
    }

    fun setBandL2(frequencyHz: Int, l2: String) {
        updateBandInput(frequencyHz) { it.copy(l2Input = l2) }
    }

    fun setBandT(frequencyHz: Int, t: String) {
        updateBandInput(frequencyHz) { it.copy(tInput = t) }
    }

    fun setBandB(frequencyHz: Int, b: String) {
        updateBandInput(frequencyHz) { it.copy(bInput = b) }
    }

    fun populateSampleData() {
        val currentMode = _octaveBandType.value

        _roomParametersInput.value = RoomParametersInputState(
            volumeVInput = "50.0",
            areaSInput = "12.0",
            referenceT0Input = "0.5"
        )

        val updatedMap = _bandInputsMap.value.toMutableMap()

        if (currentMode == OctaveBandType.OCTAVE_1_1) {
            val sample1_1 = mapOf(
                125 to BandInputState(125, l1Input = "88.0", l2Input = "54.0", tInput = "0.60", bInput = "30.0"),
                250 to BandInputState(250, l1Input = "90.0", l2Input = "50.0", tInput = "0.55", bInput = "28.0"),
                500 to BandInputState(500, l1Input = "92.0", l2Input = "46.0", tInput = "0.50", bInput = "25.0"),
                1000 to BandInputState(1000, l1Input = "91.0", l2Input = "41.0", tInput = "0.48", bInput = "22.0"),
                2000 to BandInputState(2000, l1Input = "89.0", l2Input = "37.0", tInput = "0.45", bInput = "20.0")
            )
            updatedMap.putAll(sample1_1)
        } else {
            val sample1_3 = mapOf(
                100 to BandInputState(100, l1Input = "86.0", l2Input = "58.0", tInput = "0.65", bInput = "32.0"),
                125 to BandInputState(125, l1Input = "88.0", l2Input = "55.0", tInput = "0.62", bInput = "30.0"),
                160 to BandInputState(160, l1Input = "89.0", l2Input = "52.0", tInput = "0.60", bInput = "29.0"),
                200 to BandInputState(200, l1Input = "90.0", l2Input = "51.0", tInput = "0.58", bInput = "28.0"),
                250 to BandInputState(250, l1Input = "90.0", l2Input = "49.0", tInput = "0.55", bInput = "27.0"),
                315 to BandInputState(315, l1Input = "91.0", l2Input = "47.0", tInput = "0.53", bInput = "26.0"),
                400 to BandInputState(400, l1Input = "92.0", l2Input = "46.0", tInput = "0.51", bInput = "25.0"),
                500 to BandInputState(500, l1Input = "92.0", l2Input = "44.0", tInput = "0.50", bInput = "24.0"),
                630 to BandInputState(630, l1Input = "92.0", l2Input = "43.0", tInput = "0.49", bInput = "23.0"),
                800 to BandInputState(800, l1Input = "91.0", l2Input = "41.0", tInput = "0.48", bInput = "22.0"),
                1000 to BandInputState(1000, l1Input = "91.0", l2Input = "40.0", tInput = "0.47", bInput = "21.0"),
                1250 to BandInputState(1250, l1Input = "90.0", l2Input = "38.0", tInput = "0.46", bInput = "20.0"),
                1600 to BandInputState(1600, l1Input = "89.0", l2Input = "37.0", tInput = "0.45", bInput = "20.0"),
                2000 to BandInputState(2000, l1Input = "89.0", l2Input = "36.0", tInput = "0.44", bInput = "20.0"),
                2500 to BandInputState(2500, l1Input = "88.0", l2Input = "34.0", tInput = "0.43", bInput = "20.0"),
                3150 to BandInputState(3150, l1Input = "87.0", l2Input = "32.0", tInput = "0.42", bInput = "20.0")
            )
            updatedMap.putAll(sample1_3)
        }

        _bandInputsMap.value = updatedMap
    }

    fun clearAllInputs() {
        _roomParametersInput.value = RoomParametersInputState()
        _bandInputsMap.value = initializeBandInputsMap()
    }

    private fun updateBandInput(
        frequencyHz: Int,
        updateAction: (BandInputState) -> BandInputState
    ) {
        val currentMap = _bandInputsMap.value.toMutableMap()
        val currentInput = currentMap[frequencyHz] ?: BandInputState(frequencyHz = frequencyHz)
        currentMap[frequencyHz] = updateAction(currentInput)
        _bandInputsMap.value = currentMap
    }

    private companion object {
        fun initializeBandInputsMap(): Map<Int, BandInputState> {
            val allFrequencies = (OctaveBandDefaults.FREQUENCIES_1_1 + OctaveBandDefaults.FREQUENCIES_1_3).distinct()
            return allFrequencies.associateWith { freq ->
                BandInputState(frequencyHz = freq)
            }
        }
    }
}
