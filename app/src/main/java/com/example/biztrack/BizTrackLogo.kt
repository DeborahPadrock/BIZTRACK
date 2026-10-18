package com.example.biztrack

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biztrack.ui.theme.ForestGreenPrimary
import com.example.biztrack.ui.theme.MintGreenSecondary

@Composable
fun BizTrackLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    forestColor: Color = ForestGreenPrimary,
    mintColor: Color = MintGreenSecondary
) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()

        // Bar dimensions
        val barWidth = w * 0.16f
        val cornerRadius = CornerRadius(barWidth * 0.4f, barWidth * 0.4f)
        val bottomY = h * 0.92f

        // Bar 1 (Shortest - Left)
        val bar1Height = h * 0.35f
        drawRoundRect(
            color = forestColor,
            topLeft = Offset(w * 0.15f, bottomY - bar1Height),
            size = Size(barWidth, bar1Height),
            cornerRadius = cornerRadius
        )

        // Bar 2 (Medium - Middle)
        val bar2Height = h * 0.55f
        drawRoundRect(
            color = forestColor,
            topLeft = Offset(w * 0.42f, bottomY - bar2Height),
            size = Size(barWidth, bar2Height),
            cornerRadius = cornerRadius
        )

        // Bar 3 (Taller - Right)
        val bar3Height = h * 0.75f
        drawRoundRect(
            color = forestColor,
            topLeft = Offset(w * 0.69f, bottomY - bar3Height),
            size = Size(barWidth, bar3Height),
            cornerRadius = cornerRadius
        )

        // Upward Growth Arrow Stem (Deep Forest Green)
        val arrowPath = Path().apply {
            moveTo(w * 0.08f, h * 0.62f)
            cubicTo(
                w * 0.25f, h * 0.50f,
                w * 0.50f, h * 0.32f,
                w * 0.78f, h * 0.16f
            )
        }

        drawPath(
            path = arrowPath,
            color = forestColor,
            style = Stroke(width = w * 0.08f)
        )

        // Upward Arrowhead (Bright Mint Green)
        val headPath = Path().apply {
            moveTo(w * 0.92f, h * 0.06f) // Arrowhead tip
            lineTo(w * 0.68f, h * 0.08f)
            lineTo(w * 0.86f, h * 0.26f)
            close()
        }

        drawPath(
            path = headPath,
            color = mintColor
        )
    }
}

@Composable
fun BizTrackLogoHeader(
    modifier: Modifier = Modifier,
    iconSize: Dp = 64.dp,
    showSubtitle: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BizTrackLogoIcon(size = iconSize)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "BIZTRACK",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.primary
        )
        if (showSubtitle) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Inventory, Sales, Expenses & Profit",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
