package me.abhigya.bourbon.core.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.abhigya.bourbon.core.ui.AppScreen

object OnBoardingScreen : AppScreen {

    @Composable
    override operator fun invoke() {
        Scaffold { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = "Setup",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                HeightAndWeightCard()
                GenderAndDOBCard()
            }
        }
    }

    @Composable
    internal fun TileCard(modifier: Modifier = Modifier, rows: List<(@Composable ColumnScope.() -> Unit)>) {
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
                    for ((idx, row) in rows.withIndex()) {
                        row()
                        if (idx == rows.size - 1) continue
                        TileSeparator()
                    }
                }
            }
        }
    }

    @Composable
    internal fun TileSeparator(modifier: Modifier = Modifier, thickness: Dp = 3.dp) {
        val color = MaterialTheme.colorScheme.tertiary
        Canvas(modifier = modifier
            .height(thickness)
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
    internal fun TiledRow(modifier: Modifier = Modifier, label: String? = null, elements: List<(@Composable BoxScope.() -> Unit)>) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            if (label != null) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (element in elements) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        element()
                    }
                }
            }
        }
    }

    @Composable
    internal fun HeightAndWeightCard() {
        @Composable
        fun BoxScope.DropButton(text: String) {
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp)
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = text, color = MaterialTheme.colorScheme.background)
                VerticalDivider(thickness = 4.dp)
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.background,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        TileCard(rows = listOf(
            {
                TiledRow(elements = listOf(
                    { Text(text = "Weight", color = Color.White) },
                    { DropButton(text = "Kg") }
                ))
            },
            {
                TiledRow(elements = listOf(
                    { Text(text = "Height", color = Color.White) },
                    { DropButton(text = "Cm") }
                ))
            }
        ))
    }

    @Composable
    internal fun GenderAndDOBCard() {
        @Composable
        fun BoxScope.GenderButton(content: String, selected: Boolean = false) {
            Row (
                modifier = Modifier
                    .matchParentSize()
                    .run {
                        if (selected) {
                            background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp)
                            )
                        } else {
                            this
                        }
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = content,
                    tint = if (selected) MaterialTheme.colorScheme.background else Color.White,
                    modifier = Modifier.size(16.dp)
                )
                VerticalDivider(thickness = 4.dp)
                Text(text = content, color = if (selected) MaterialTheme.colorScheme.background else Color.White)
            }
        }

        @Composable
        fun BoxScope.DOBTile(label: String) {
            Row(
                modifier = Modifier
                    .matchParentSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = label, color = Color.White)
                VerticalDivider(thickness = 4.dp)
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        TileCard(rows = listOf(
            {
                TiledRow(label = "Gender", elements = listOf(
                    { GenderButton("Male", true) },
                    { GenderButton("Female") }
                ))
            },
            {
                TiledRow(label = "Date of Birth", elements = listOf(
                    { DOBTile(label = "Day") },
                    { DOBTile(label = "Month") },
                    { DOBTile(label = "Year") }
                ))
            }
        ))
    }

}