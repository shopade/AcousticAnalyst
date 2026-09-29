package com.example.acousticanalyst.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.acousticanalyst.domain.model.AcousticCalculationResult
import com.example.acousticanalyst.domain.model.BandCalculationResult
import com.example.acousticanalyst.domain.model.SingleNumberRatingResult
import com.example.acousticanalyst.ui.viewmodel.AcousticUiState
import java.util.Locale

@Composable
fun ResultsScreen(
    uiState: AcousticUiState,
    onLoadSampleData: () -> Unit,
    onNavigateToChart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val result = uiState.calculationResult

    if (result == null || result.bandResults.isEmpty()) {
        EmptyResultsView(
            message = uiState.validationErrorMessage ?: "No measurement data entered.",
            onLoadSampleData = onLoadSampleData,
            modifier = modifier
        )
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Single-Number Ratings (ISO 717-1)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Ratings Summary Cards Row
        item {
            RatingsSummaryRow(
                rw = result.rw,
                dnTw = result.dnTw,
                dw = result.dw
            )
        }

        // Action to navigate to Visual Chart
        item {
            Button(
                onClick = onNavigateToChart,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ShowChart,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "View Acoustic Curve Chart",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Text(
                text = "Frequency Band Details (ISO 16283-1)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Detailed Frequency Band Table
        item {
            BandBreakdownTable(bandResults = result.bandResults)
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EmptyResultsView(
    message: String,
    onLoadSampleData: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Assessment,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Calculation Incomplete",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedButton(
                    onClick = onLoadSampleData,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.RestartAlt,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text("Load Sample Measurement Data")
                }
            }
        }
    }
}

@Composable
private fun RatingsSummaryRow(
    rw: SingleNumberRatingResult?,
    dnTw: SingleNumberRatingResult?,
    dw: SingleNumberRatingResult?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SingleRatingCard(
                ratingName = "Rw",
                ratingFullName = "Sound Reduction Index",
                ratingResult = rw,
                modifier = Modifier.weight(1f)
            )

            SingleRatingCard(
                ratingName = "Dn,T,w",
                ratingFullName = "Standardized Difference",
                ratingResult = dnTw,
                modifier = Modifier.weight(1f)
            )
        }

        SingleRatingCard(
            ratingName = "Dw",
            ratingFullName = "Weighted Level Difference",
            ratingResult = dw,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SingleRatingCard(
    ratingName: String,
    ratingFullName: String,
    ratingResult: SingleNumberRatingResult?,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            val displayName = when (ratingName) {
                "Rw" -> "R'w (C; Ctr)"
                "DnTw" -> "Dn,T,w (C; Ctr)"
                "Dw" -> "Dw (C; Ctr)"
                else -> "$ratingName (C; Ctr)"
            }

            Text(
                text = displayName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = ratingFullName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (ratingResult != null) {
                Text(
                    text = String.format(
                        Locale.US,
                        "%d (%d; %d) dB",
                        ratingResult.ratingValue,
                        ratingResult.cTerm,
                        ratingResult.ctrTerm
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = String.format(
                        Locale.US,
                        "Deviations: %.1f dB / max %.1f dB",
                        ratingResult.sumUnfavorableDeviations,
                        ratingResult.maxAllowedDeviations
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            } else {
                Text(
                    text = "N/A",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "Requires Volume V & Area S",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun BandBreakdownTable(
    bandResults: List<BandCalculationResult>
) {
    val scrollState = rememberScrollState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .horizontalScroll(scrollState)
        ) {
            // Table Header Row
            Row(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableHeaderCell("Freq (Hz)", width = 80.dp)
                TableHeaderCell("L1 (dB)", width = 70.dp)
                TableHeaderCell("L2 (dB)", width = 70.dp)
                TableHeaderCell("L2' (dB)", width = 70.dp)
                TableHeaderCell("B (dB)", width = 70.dp)
                TableHeaderCell("D (dB)", width = 70.dp)
                TableHeaderCell("DnT (dB)", width = 75.dp)
                TableHeaderCell("R (dB)", width = 70.dp)
                TableHeaderCell("Status / Warnings", width = 180.dp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Data Rows
            bandResults.forEach { band ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCell("${band.frequencyHz}", width = 80.dp, fontWeight = FontWeight.Bold)
                    TableCell(formatDouble(band.l1), width = 70.dp)
                    TableCell(formatDouble(band.l2), width = 70.dp)
                    TableCell(formatDouble(band.correctedL2), width = 70.dp)
                    TableCell(band.b?.let { formatDouble(it) } ?: "-", width = 70.dp)
                    TableCell(formatDouble(band.d), width = 70.dp)
                    TableCell(band.dnT?.let { formatDouble(it) } ?: "-", width = 75.dp)
                    TableCell(band.r?.let { formatDouble(it) } ?: "-", width = 70.dp)

                    Box(
                        modifier = Modifier.width(180.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (band.hasBackgroundNoiseWarning) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "L2 - B < 6 dB",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "OK",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableHeaderCell(text: String, width: Dp) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        textAlign = TextAlign.Center,
        modifier = Modifier.width(width)
    )
}

@Composable
private fun TableCell(
    text: String,
    width: Dp,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = fontWeight,
        textAlign = TextAlign.Center,
        modifier = Modifier.width(width)
    )
}

private fun formatDouble(value: Double): String {
    return String.format(Locale.US, "%.1f", value)
}
