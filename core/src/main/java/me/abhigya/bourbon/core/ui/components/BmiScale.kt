package me.abhigya.bourbon.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.abhigya.bourbon.domain.entities.BmiCategory

@Composable
fun BmiScale(
    value: Double,
    modifier: Modifier = Modifier,
    scaleLineColor: Color = Color.Black,
    scaleBorder: BorderStroke = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface),
    indicatorColor: Color = MaterialTheme.colorScheme.onSurface,
    cornerRadius: Dp = 24.dp,
    animationEnabled: Boolean = true,
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val indicatorPositionX = remember { Animatable(0f) }

    if (animationEnabled) {
        LaunchedEffect(Unit) {
            indicatorPositionX.animateTo(
                calculateIndicatorPositionX(
                    singlePartWidth = canvasSize.width / BmiCategory.entries.size,
                    value = value
                ),
                tween(1000)
            )
        }
    }

    Canvas(
        modifier = modifier.requiredSize(
            width = 240.dp,
            height = 16.dp
        )
    ) {
        canvasSize = size

        //Drawing background
        val singlePartWidth = size.width / BmiCategory.entries.size

        drawRoundRect(
            scaleBorder.brush,
            cornerRadius = CornerRadius(cornerRadius.toPx()),
            style = Stroke(width = scaleBorder.width.toPx())
        )

        val scaleHorizontalPadding = 24.dp.toPx()
        BmiCategory.entries.forEachIndexed { index, category ->

            val leftRadius = when (index) {
                0 -> CornerRadius(cornerRadius.toPx())
                else -> CornerRadius.Zero
            }

            val rightRadius = when (index) {
                BmiCategory.entries.size - 1 -> CornerRadius(cornerRadius.toPx())
                else -> CornerRadius.Zero
            }

            val rect = Rect(
                topLeft = Offset(singlePartWidth * index, 0f),
                bottomRight = Offset(singlePartWidth * (index + 1), size.height)
            )

            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = rect.left,
                        top = rect.top,
                        right = rect.right,
                        bottom = rect.bottom,
                        topLeftCornerRadius = leftRadius,
                        bottomLeftCornerRadius = leftRadius,
                        topRightCornerRadius = rightRadius,
                        bottomRightCornerRadius = rightRadius
                    )
                )
            }

            drawPath(path, category.color)
        }

        //Drawing scale lines

        val scaleSize = Size(
            width = size.width - (scaleHorizontalPadding * 2),
            height = size.height
        )

        val totalLines = 30
        val lineSpacing = scaleSize.width / totalLines

        for (i in 0..totalLines) {
            val number = (i + 1 * 10) / 10.0
            val isWholeNumber = number % 1 == 0.0

            val lineStartY = when (isWholeNumber) {
                true -> size.height / 3f
                else -> size.height / 1.5f
            }
            val lineStartX = scaleHorizontalPadding + (lineSpacing * i)

            drawLine(
                color = scaleLineColor,
                start = Offset(lineStartX, lineStartY),
                end = Offset(lineStartX, size.height),
                strokeWidth = 2f
            )
        }

        val xPosResolvedValue = when (animationEnabled) {
            true -> indicatorPositionX.value
            else -> calculateIndicatorPositionX(
                singlePartWidth = singlePartWidth,
                value = value
            )
        }

        val xPos = xPosResolvedValue.coerceIn(
            minimumValue = scaleHorizontalPadding,
            maximumValue = size.width - scaleHorizontalPadding
        )

        drawLine(
            color = indicatorColor,
            start = Offset(
                x = xPos,
                y = -5.dp.toPx()
            ),
            end = Offset(
                x = xPos,
                y = size.height + 5.dp.toPx()
            ),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )

    }
}

/**
 * Calculates the position of the indicator on the scale.
 *
 * Logic:
 *
 * If value is 5 with range 0..10 and scale width is 100, then indicator position will be 50 (50% of scale width).
 */
private fun calculateIndicatorPositionX(
    singlePartWidth: Float,
    value: Double,
): Float {
    val category = BmiCategory.from(value)
    val index = BmiCategory.entries.indexOf(category)

    val startX = singlePartWidth * index
    val endX = singlePartWidth * (index + 1)
    val categoryWidth = endX - startX

    val categoryStart = category.range.start
    val categoryEnd = category.range.endInclusive

    val valueRelativeToCategory = value - categoryStart
    val categoryRange = categoryEnd - categoryStart

    val categoryWidthFactor = valueRelativeToCategory / categoryRange
    val indicatorPositionX = startX + (categoryWidth * categoryWidthFactor)

    return indicatorPositionX.toFloat()
}