package me.abhigya.bourbon.core.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun TileCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = modifier
            .padding(4.dp)
    ) {
        Card(
            modifier = modifier
                .width(336.dp),
            colors = CardDefaults.cardColors()
                .copy(
                    containerColor = MaterialTheme.colorScheme.secondary,
                )
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                content()
            }
        }
    }
}

@Composable
internal fun TileSeparator(modifier: Modifier = Modifier, thickness: Dp = 3.dp) {
    val color = MaterialTheme.colorScheme.tertiary
    Canvas(modifier = modifier
        .height(thickness + 8.dp)
        .padding(vertical = 4.dp)
        .fillMaxWidth()) {
        drawLine(
            color = color,
            strokeWidth = thickness.toPx(),
            start = Offset(0f, thickness.toPx() / 2),
            end = Offset(size.width, thickness.toPx() / 2),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(5.dp.toPx(), 5.dp.toPx()),
                phase = 0.dp.toPx()
            )
        )
    }
}

@Composable
internal fun Label(text: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 2.dp),
        text = text,
        color = MaterialTheme.colorScheme.tertiary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Left
    )
}

@Composable
internal fun TiledRow(modifier: Modifier = Modifier, itemsPerRow: Int = 2, elements: List<(@Composable BoxScope.() -> Unit)>) {
    val e = if (elements.size % itemsPerRow != 0) {
        elements + List(itemsPerRow - elements.size % itemsPerRow) { {} }
    } else {
        elements
    }
    val rows = e.chunked(itemsPerRow)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        for ((i, row) in rows.withIndex()) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for ((idx, element) in row.withIndex()) {
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(4.dp))
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        element()
                    }
                    if (idx != row.size - 1) {
                        VerticalDivider(thickness = (40 / itemsPerRow).dp)
                    }
                }
            }

            if (i != rows.size - 1) {
                HorizontalDivider(thickness = 16.dp)
            }
        }
    }
}