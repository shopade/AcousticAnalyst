package com.example.acousticanalyst.ui.components

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun AcousticChartCanvas(
    measuredCurve: Map<Int, Double>,
    shiftedReferenceCurve: Map<Int, Double>,
    ratingType: String,
    ratingValue: Int?,
    cTerm: Int? = null,
    ctrTerm: Int? = null,
    modifier: Modifier = Modifier
) {
    val frequencies = (measuredCurve.keys + shiftedReferenceCurve.keys).distinct().sorted()

    if (frequencies.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No curve data available. Enter measurements to visualize chart.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val gridColor = MaterialTheme.colorScheme.outlineVariant
    val textColor = MaterialTheme.colorScheme.onSurface

    // Calculate Y-axis bounds
    val allValues = measuredCurve.values + shiftedReferenceCurve.values
    val rawMinY = allValues.minOrNull() ?: 0.0
    val rawMaxY = allValues.maxOrNull() ?: 60.0

    val minY = (floor((rawMinY - 5.0) / 10.0) * 10.0).coerceAtLeast(0.0)
    val maxY = (ceil((rawMaxY + 10.0) / 10.0) * 10.0).coerceAtLeast(minY + 30.0)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Chart Title & Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$ratingType Curve Visualization",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                if (ratingValue != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        val displayRatingName = when (ratingType) {
                            "Rw" -> "R'w"
                            "DnTw" -> "Dn,T,w"
                            else -> ratingType
                        }
                        val badgeText = if (cTerm != null && ctrTerm != null) {
                            "$displayRatingName ($cTerm; $ctrTerm) = $ratingValue dB"
                        } else {
                            "$ratingType = $ratingValue dB"
                        }
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(primaryColor, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Measured $ratingType",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.width(16.dp))

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(tertiaryColor, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Shifted ISO 717-1 Contour",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom Canvas Chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                val paddingLeft = 110f
                val paddingBottom = 70f
                val paddingTop = 30f
                val paddingRight = 40f

                val chartWidth = size.width - paddingLeft - paddingRight
                val chartHeight = size.height - paddingTop - paddingBottom

                val yTickCount = ((maxY - minY) / 10.0).toInt().coerceAtLeast(3)
                val yStepPixels = chartHeight / yTickCount

                val axisPaint = Paint().apply {
                    color = textColor.toArgb()
                    textSize = 26f
                    textAlign = Paint.Align.RIGHT
                    isAntiAlias = true
                }

                val xLabelPaint = Paint().apply {
                    color = textColor.toArgb()
                    textSize = 24f
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }

                // Draw Horizontal Grid Lines & Y-Axis Labels
                for (i in 0..yTickCount) {
                    val yVal = maxY - (i * 10)
                    val yPos = paddingTop + (i * yStepPixels)

                    drawLine(
                        color = gridColor,
                        start = Offset(paddingLeft, yPos),
                        end = Offset(size.width - paddingRight, yPos),
                        strokeWidth = 1f
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        "${yVal.toInt()} dB",
                        paddingLeft - 15f,
                        yPos + 8f,
                        axisPaint
                    )
                }

                // Calculate X positions for each frequency
                val xStepPixels = if (frequencies.size > 1) {
                    chartWidth / (frequencies.size - 1)
                } else {
                    chartWidth / 2f
                }

                val xCoordsMap = mutableMapOf<Int, Float>()

                // Draw Vertical Grid Lines & X-Axis Labels (Frequencies)
                frequencies.forEachIndexed { index, freq ->
                    val xPos = paddingLeft + (index * xStepPixels)
                    xCoordsMap[freq] = xPos

                    drawLine(
                        color = gridColor.copy(alpha = 0.5f),
                        start = Offset(xPos, paddingTop),
                        end = Offset(xPos, paddingTop + chartHeight),
                        strokeWidth = 1f
                    )

                    val label = if (freq >= 1000) "${freq / 1000}k" else "$freq"
                    drawContext.canvas.nativeCanvas.drawText(
                        label,
                        xPos,
                        paddingTop + chartHeight + 35f,
                        xLabelPaint
                    )
                }

                // Helper to map (freq, dB) to (x, y) coordinates on canvas
                fun mapToOffset(freq: Int, db: Double): Offset? {
                    val x = xCoordsMap[freq] ?: return null
                    val clampedDb = db.coerceIn(minY, maxY)
                    val y = paddingTop + ((maxY - clampedDb) / (maxY - minY)).toFloat() * chartHeight
                    return Offset(x, y)
                }

                // 1. Draw Shifted Reference Curve (Dashed line with square markers)
                val refPoints = frequencies.mapNotNull { freq ->
                    shiftedReferenceCurve[freq]?.let { db -> mapToOffset(freq, db) }
                }

                if (refPoints.size > 1) {
                    val refPath = Path().apply {
                        moveTo(refPoints[0].x, refPoints[0].y)
                        for (i in 1 until refPoints.size) {
                            lineTo(refPoints[i].x, refPoints[i].y)
                        }
                    }

                    drawPath(
                        path = refPath,
                        color = tertiaryColor,
                        style = Stroke(
                            width = 3.5f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    )

                    refPoints.forEach { pt ->
                        drawRect(
                            color = tertiaryColor,
                            topLeft = Offset(pt.x - 5f, pt.y - 5f),
                            size = Size(10f, 10f)
                        )
                    }
                }

                // 2. Draw Measured Curve (Solid line with circle markers)
                val measuredPoints = frequencies.mapNotNull { freq ->
                    measuredCurve[freq]?.let { db -> mapToOffset(freq, db) }
                }

                if (measuredPoints.size > 1) {
                    val measuredPath = Path().apply {
                        moveTo(measuredPoints[0].x, measuredPoints[0].y)
                        for (i in 1 until measuredPoints.size) {
                            lineTo(measuredPoints[i].x, measuredPoints[i].y)
                        }
                    }

                    drawPath(
                        path = measuredPath,
                        color = primaryColor,
                        style = Stroke(width = 4.5f)
                    )

                    measuredPoints.forEach { pt ->
                        drawCircle(
                            color = primaryColor,
                            radius = 7f,
                            center = pt
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 3.5f,
                            center = pt
                        )
                    }
                }
            }

            // Frequency unit label below X axis
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Frequency Band (Hz)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
