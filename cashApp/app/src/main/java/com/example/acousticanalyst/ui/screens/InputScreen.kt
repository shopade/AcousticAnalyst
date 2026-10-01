package com.example.acousticanalyst.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.acousticanalyst.domain.model.OctaveBandType
import com.example.acousticanalyst.ui.viewmodel.AcousticUiState
import com.example.acousticanalyst.ui.viewmodel.BandInputState

@Composable
fun InputScreen(
    uiState: AcousticUiState,
    onOctaveBandTypeChange: (OctaveBandType) -> Unit,
    onVolumeChange: (String) -> Unit,
    onAreaChange: (String) -> Unit,
    onT0Change: (String) -> Unit,
    onL1Change: (Int, String) -> Unit,
    onL2Change: (Int, String) -> Unit,
    onTChange: (Int, String) -> Unit,
    onBChange: (Int, String) -> Unit,
    onLoadSampleData: () -> Unit,
    onClearAll: () -> Unit,
    onNavigateToResults: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Mode Selector: 1/1 Octave vs 1/3 Octave
            OctaveBandSelectorCard(
                selectedType = uiState.octaveBandType,
                onTypeSelected = onOctaveBandTypeChange
            )
        }

        item {
            // Room Configuration Parameters Card
            RoomParametersCard(
                volumeInput = uiState.roomParametersInput.volumeVInput,
                areaInput = uiState.roomParametersInput.areaSInput,
                t0Input = uiState.roomParametersInput.referenceT0Input,
                onVolumeChange = onVolumeChange,
                onAreaChange = onAreaChange,
                onT0Change = onT0Change
            )
        }

        item {
            // Table Header Card
            Text(
                text = "Frequency Band Measurements",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Measurement Table Header Labels
        item {
            MeasurementTableHeader()
        }

        // Frequency Band Rows
        items(
            items = uiState.bandInputs,
            key = { it.frequencyHz }
        ) { bandInput ->
            MeasurementRowItem(
                bandInput = bandInput,
                onL1Change = { onL1Change(bandInput.frequencyHz, it) },
                onL2Change = { onL2Change(bandInput.frequencyHz, it) },
                onTChange = { onTChange(bandInput.frequencyHz, it) },
                onBChange = { onBChange(bandInput.frequencyHz, it) }
            )
        }

        // Action Buttons Row
        item {
            ActionButtonsRow(
                isCalculationValid = uiState.isCalculationValid,
                validationMessage = uiState.validationErrorMessage,
                onLoadSampleData = onLoadSampleData,
                onClearAll = onClearAll,
                onCalculate = onNavigateToResults
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OctaveBandSelectorCard(
    selectedType: OctaveBandType,
    onTypeSelected: (OctaveBandType) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Measurement Resolution",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            val selectedIndex = if (selectedType == OctaveBandType.OCTAVE_1_1) 0 else 1
            PrimaryTabRow(
                selectedTabIndex = selectedIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedIndex == 0,
                    onClick = { onTypeSelected(OctaveBandType.OCTAVE_1_1) },
                    text = {
                        Text(
                            text = "1/1 Octave Band",
                            fontWeight = if (selectedIndex == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedIndex == 1,
                    onClick = { onTypeSelected(OctaveBandType.OCTAVE_1_3) },
                    text = {
                        Text(
                            text = "1/3 Octave Band",
                            fontWeight = if (selectedIndex == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun RoomParametersCard(
    volumeInput: String,
    areaInput: String,
    t0Input: String,
    onVolumeChange: (String) -> Unit,
    onAreaChange: (String) -> Unit,
    onT0Change: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Room & Partition Parameters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = volumeInput,
                    onValueChange = onVolumeChange,
                    label = { Text("Volume V (m³)") },
                    placeholder = { Text("e.g. 50") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = areaInput,
                    onValueChange = onAreaChange,
                    label = { Text("Area S (m²)") },
                    placeholder = { Text("e.g. 12") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = t0Input,
                    onValueChange = onT0Change,
                    label = { Text("Ref T60,0 (s)") },
                    placeholder = { Text("0.5") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MeasurementTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(vertical = 10.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Freq (Hz)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1.1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "L1 (dB)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "L2 (dB)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "T60 (s)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
        Text(
            text = "B (dB)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MeasurementRowItem(
    bandInput: BandInputState,
    onL1Change: (String) -> Unit,
    onL2Change: (String) -> Unit,
    onTChange: (String) -> Unit,
    onBChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Frequency Label
            Text(
                text = "${bandInput.frequencyHz}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1.1f)
            )

            // L1 Input
            OutlinedTextField(
                value = bandInput.l1Input,
                onValueChange = onL1Change,
                placeholder = { Text("0.0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            // L2 Input
            OutlinedTextField(
                value = bandInput.l2Input,
                onValueChange = onL2Change,
                placeholder = { Text("0.0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            // T Input
            OutlinedTextField(
                value = bandInput.tInput,
                onValueChange = onTChange,
                placeholder = { Text("0.5") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            // B Input
            OutlinedTextField(
                value = bandInput.bInput,
                onValueChange = onBChange,
                placeholder = { Text("0.0") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ActionButtonsRow(
    isCalculationValid: Boolean,
    validationMessage: String?,
    onLoadSampleData: () -> Unit,
    onClearAll: () -> Unit,
    onCalculate: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (validationMessage != null && !isCalculationValid) {
            Text(
                text = validationMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onLoadSampleData,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text("Load Sample")
            }

            TextButton(
                onClick = onClearAll,
                modifier = Modifier.weight(0.9f)
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text("Clear All")
            }

            Button(
                onClick = onCalculate,
                modifier = Modifier.weight(1.2f)
            ) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text("Calculate")
            }
        }
    }
}
