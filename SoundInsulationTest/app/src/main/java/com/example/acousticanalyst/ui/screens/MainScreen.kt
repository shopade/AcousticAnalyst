package com.example.acousticanalyst.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.acousticanalyst.domain.model.OctaveBandType
import com.example.acousticanalyst.ui.viewmodel.AcousticViewModel

enum class AppDestination(
    val title: String,
    val icon: ImageVector
) {
    INPUT("Input Data", Icons.Default.Edit),
    RESULTS("Results", Icons.Default.Assessment),
    CHART("Acoustic Chart", Icons.AutoMirrored.Filled.ShowChart)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: AcousticViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentDestination by remember { mutableStateOf(AppDestination.INPUT) }
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Acoustic Analyst",
                            fontWeight = FontWeight.Bold
                        )
                        val modeLabel = if (uiState.octaveBandType == OctaveBandType.OCTAVE_1_1) "1/1 Octave" else "1/3 Octave"
                        Text(
                            text = "ISO 16283-1 / ISO 717-1 ($modeLabel)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.populateSampleData() }) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Load Sample Data"
                        )
                    }
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Load Sample Data") },
                            onClick = {
                                viewModel.populateSampleData()
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear All Inputs") },
                            onClick = {
                                viewModel.clearAllInputs()
                                showMenu = false
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                AppDestination.entries.forEach { destination ->
                    val isSelected = currentDestination == destination

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            if (destination == AppDestination.RESULTS && uiState.isCalculationValid) {
                                BadgedBox(
                                    badge = { Badge() }
                                ) {
                                    Icon(
                                        imageVector = destination.icon,
                                        contentDescription = destination.title
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = destination.icon,
                                    contentDescription = destination.title
                                )
                            }
                        },
                        label = {
                            Text(
                                text = destination.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentDestination,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "ScreenTransition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { destination ->
            when (destination) {
                AppDestination.INPUT -> {
                    InputScreen(
                        uiState = uiState,
                        onOctaveBandTypeChange = { viewModel.setOctaveBandType(it) },
                        onVolumeChange = { viewModel.setVolumeV(it) },
                        onAreaChange = { viewModel.setAreaS(it) },
                        onT0Change = { viewModel.setReferenceT0(it) },
                        onL1Change = { freq, l1 -> viewModel.setBandL1(freq, l1) },
                        onL2Change = { freq, l2 -> viewModel.setBandL2(freq, l2) },
                        onTChange = { freq, t -> viewModel.setBandT(freq, t) },
                        onBChange = { freq, b -> viewModel.setBandB(freq, b) },
                        onLoadSampleData = { viewModel.populateSampleData() },
                        onClearAll = { viewModel.clearAllInputs() },
                        onNavigateToResults = { currentDestination = AppDestination.RESULTS }
                    )
                }

                AppDestination.RESULTS -> {
                    ResultsScreen(
                        uiState = uiState,
                        onLoadSampleData = {
                            viewModel.populateSampleData()
                            currentDestination = AppDestination.INPUT
                        },
                        onNavigateToChart = { currentDestination = AppDestination.CHART }
                    )
                }

                AppDestination.CHART -> {
                    ChartScreen(
                        uiState = uiState,
                        onLoadSampleData = {
                            viewModel.populateSampleData()
                            currentDestination = AppDestination.INPUT
                        }
                    )
                }
            }
        }
    }
}
