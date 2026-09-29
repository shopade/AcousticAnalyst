package com.example.acousticanalyst.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.acousticanalyst.ui.screens.MainScreen
import com.example.acousticanalyst.ui.theme.AcousticAnalystTheme
import com.example.acousticanalyst.ui.viewmodel.AcousticViewModel
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun mainScreen_rendersTopBarAndBottomNavigation() {
        val viewModel = AcousticViewModel()
        composeTestRule.setContent {
            AcousticAnalystTheme {
                MainScreen(viewModel = viewModel)
            }
        }

        composeTestRule.onNodeWithText("Acoustic Analyst").assertIsDisplayed()
        composeTestRule.onNodeWithText("Input Data").assertIsDisplayed()
        composeTestRule.onNodeWithText("Results").assertIsDisplayed()
        composeTestRule.onNodeWithText("Acoustic Chart").assertIsDisplayed()
    }

    @Test
    fun mainScreen_sampleDataPopulationAndNavigationToResults() {
        val viewModel = AcousticViewModel()

        composeTestRule.setContent {
            AcousticAnalystTheme {
                MainScreen(viewModel = viewModel)
            }
        }

        // Click Load Sample
        composeTestRule.onNodeWithText("Load Sample").performClick()

        // Navigate to Results Tab
        composeTestRule.onNodeWithText("Results").performClick()

        // Check single-number rating card values exist
        composeTestRule.onNodeWithText("Rw").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dn,T,w").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dw").assertIsDisplayed()
    }

    @Test
    fun mainScreen_navigateToAcousticChart() {
        val viewModel = AcousticViewModel()

        composeTestRule.setContent {
            AcousticAnalystTheme {
                MainScreen(viewModel = viewModel)
            }
        }

        // Click Load Sample Data
        composeTestRule.onNodeWithText("Load Sample").performClick()

        // Navigate to Acoustic Chart Tab
        composeTestRule.onNodeWithText("Acoustic Chart").assertIsDisplayed()
        composeTestRule.onNodeWithText("Acoustic Chart").performClick()

        // Verify chart header or curve chip is displayed
        composeTestRule.onNodeWithText("ISO 717-1 Acoustic Curves").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dn,T,w").assertIsDisplayed()
    }
}
