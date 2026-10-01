package com.example.acousticanalyst.ui

import com.example.acousticanalyst.domain.model.OctaveBandDefaults
import com.example.acousticanalyst.domain.model.OctaveBandType
import com.example.acousticanalyst.ui.viewmodel.AcousticViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AcousticViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: AcousticViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AcousticViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has default values for 1-1 octave mode`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(OctaveBandType.OCTAVE_1_1, state.octaveBandType)
        assertEquals("", state.roomParametersInput.volumeVInput)
        assertEquals("", state.roomParametersInput.areaSInput)
        assertEquals("0.5", state.roomParametersInput.referenceT0Input)
        assertEquals(5, state.bandInputs.size)
        assertEquals(OctaveBandDefaults.FREQUENCIES_1_1, state.bandInputs.map { it.frequencyHz })
        assertNull(state.calculationResult)
        assertFalse(state.isCalculationValid)
        assertNotNull(state.validationErrorMessage)
    }

    @Test
    fun `changing octave band type updates band inputs list`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        viewModel.setOctaveBandType(OctaveBandType.OCTAVE_1_3)
        advanceUntilIdle()

        val state1_3 = viewModel.uiState.value
        assertEquals(OctaveBandType.OCTAVE_1_3, state1_3.octaveBandType)
        assertEquals(16, state1_3.bandInputs.size)
        assertEquals(OctaveBandDefaults.FREQUENCIES_1_3, state1_3.bandInputs.map { it.frequencyHz })

        viewModel.setOctaveBandType(OctaveBandType.OCTAVE_1_1)
        advanceUntilIdle()

        val state1_1 = viewModel.uiState.value
        assertEquals(OctaveBandType.OCTAVE_1_1, state1_1.octaveBandType)
        assertEquals(5, state1_1.bandInputs.size)
        assertEquals(OctaveBandDefaults.FREQUENCIES_1_1, state1_1.bandInputs.map { it.frequencyHz })
    }

    @Test
    fun `updating room parameters updates state correctly`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        viewModel.setVolumeV("60.0")
        viewModel.setAreaS("15.0")
        viewModel.setReferenceT0("0.8")
        advanceUntilIdle()

        val roomState = viewModel.uiState.value.roomParametersInput
        assertEquals("60.0", roomState.volumeVInput)
        assertEquals("15.0", roomState.areaSInput)
        assertEquals("0.8", roomState.referenceT0Input)

        assertEquals(60.0, roomState.volumeV)
        assertEquals(15.0, roomState.areaS)
        assertEquals(0.8, roomState.referenceT0, 0.001)
    }

    @Test
    fun `updating band measurements updates state correctly`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        viewModel.setBandL1(125, "88.5")
        viewModel.setBandL2(125, "52.0")
        viewModel.setBandT(125, "0.65")
        viewModel.setBandB(125, "30.0")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val band125 = state.bandInputs.first { it.frequencyHz == 125 }
        assertEquals("88.5", band125.l1Input)
        assertEquals("52.0", band125.l2Input)
        assertEquals("0.65", band125.tInput)
        assertEquals("30.0", band125.bInput)

        assertEquals(88.5, band125.l1)
        assertEquals(52.0, band125.l2)
        assertEquals(0.65, band125.t)
        assertEquals(30.0, band125.b)
    }

    @Test
    fun `partial band inputs perform partial calculation but calculation is not fully valid`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        viewModel.setBandL1(125, "88.0")
        viewModel.setBandL2(125, "54.0")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.calculationResult)
        assertEquals(1, state.calculationResult!!.bandResults.size)
        assertFalse(state.isCalculationValid)
        assertTrue(state.validationErrorMessage!!.contains("Incomplete frequency data"))
    }

    @Test
    fun `filling all 1-1 octave bands triggers complete calculation and calculates ratings`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        viewModel.setVolumeV("50.0")
        viewModel.setAreaS("12.0")
        viewModel.setReferenceT0("0.5")

        viewModel.setBandL1(125, "88.0"); viewModel.setBandL2(125, "54.0")
        viewModel.setBandL1(250, "90.0"); viewModel.setBandL2(250, "50.0")
        viewModel.setBandL1(500, "92.0"); viewModel.setBandL2(500, "46.0")
        viewModel.setBandL1(1000, "91.0"); viewModel.setBandL2(1000, "41.0")
        viewModel.setBandL1(2000, "89.0"); viewModel.setBandL2(2000, "37.0")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isCalculationValid)
        assertNull(state.validationErrorMessage)

        val result = state.calculationResult
        assertNotNull(result)
        assertEquals(5, result!!.bandResults.size)
        assertNotNull(result.dw)
        assertNotNull(result.dnTw)
        assertNotNull(result.rw)
    }

    @Test
    fun `populateSampleData populates valid inputs and triggers calculation in 1-1 mode`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        viewModel.populateSampleData()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("50.0", state.roomParametersInput.volumeVInput)
        assertEquals("12.0", state.roomParametersInput.areaSInput)
        assertEquals("0.5", state.roomParametersInput.referenceT0Input)

        assertEquals(5, state.bandInputs.size)
        assertTrue(state.bandInputs.all { it.hasValidLevels })

        assertTrue(state.isCalculationValid)
        assertNotNull(state.calculationResult)
        assertNotNull(state.calculationResult!!.dw)
        assertNotNull(state.calculationResult.dnTw)
        assertNotNull(state.calculationResult.rw)
    }

    @Test
    fun `populateSampleData populates valid inputs and triggers calculation in 1-3 mode`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        viewModel.setOctaveBandType(OctaveBandType.OCTAVE_1_3)
        advanceUntilIdle()

        viewModel.populateSampleData()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(OctaveBandType.OCTAVE_1_3, state.octaveBandType)
        assertEquals(16, state.bandInputs.size)
        assertTrue(state.bandInputs.all { it.hasValidLevels })

        assertTrue(state.isCalculationValid)
        assertNotNull(state.calculationResult)
        assertEquals(16, state.calculationResult!!.bandResults.size)
        assertNotNull(state.calculationResult.dw)
    }

    @Test
    fun `clearAllInputs resets state and clears calculation`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        viewModel.populateSampleData()
        advanceUntilIdle()

        viewModel.clearAllInputs()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.roomParametersInput.volumeVInput)
        assertEquals("", state.roomParametersInput.areaSInput)
        assertNull(state.calculationResult)
        assertFalse(state.isCalculationValid)
    }

    @Test
    fun `invalid string inputs parse safely to null without crashing`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect()
        }
        viewModel.setBandL1(125, "invalid_number")
        viewModel.setBandL2(125, "50.0")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        val band125 = state.bandInputs.first { it.frequencyHz == 125 }
        assertNull(band125.l1)
        assertEquals(50.0, band125.l2)
        assertFalse(band125.hasValidLevels)
    }
}
