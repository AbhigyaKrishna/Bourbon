package me.abhigya.bourbon.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.abhigya.bourbon.core.R
import me.abhigya.bourbon.core.utils.bouncyClick
import me.abhigya.bourbon.core.utils.statusBarsPadding

@Composable
fun AppBar(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@Composable
fun BackButton(
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .clip(CircleShape),
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back),
            tint = tint
        )
    }
}

@Composable
fun BoxScope.SemiTransparentLoadingOverlay(
    modifier: Modifier = Modifier,
    state: Boolean,
    backgroundColor: Color = Color.Gray,
    alpha: Float = 0.3f
) {
    if (state) {
        Box(
            modifier = modifier
                .matchParentSize()
                .background(color = backgroundColor.copy(alpha = alpha)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    calorieEaten: Int,
    calorieRemaining: Int,
    calorieBurned: Int,
    totalCalorie: Int,
    middleValueTextSize: TextUnit = 32.sp,
    valueTextSize: TextUnit = 24.sp,
    subTextSize: TextUnit = 16.sp,
    valueColor: Color = MaterialTheme.colorScheme.primary,
    subTextColor: Color = Color.White,
    shape: Shape = CardDefaults.shape,
    elevation: CardElevation = CardDefaults.cardElevation(
        defaultElevation = 4.dp
    ),
    colors: CardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondary
    )
) {
    Card(
        modifier = modifier,
        elevation = elevation,
        shape = shape,
        colors = colors
    ) {
        Row(
            modifier = modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        text = calorieEaten.toString(),
                        color = valueColor,
                        fontSize = valueTextSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.eaten),
                        color = subTextColor,
                        fontSize = subTextSize
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1.6f)
                    .fillMaxHeight(),
            ) {
                ArcProgressIndicator(
                    modifier = Modifier
                        .matchParentSize()
                        .align(Alignment.BottomCenter),
                    percentage = (totalCalorie - calorieRemaining) / totalCalorie.toFloat(),
                    fillColor = MaterialTheme.colorScheme.primary,
                    backgroundColor = MaterialTheme.colorScheme.tertiary,
                    strokeWidth = 8.dp,
                    offset = Offset(0f, 28f)
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        text = calorieRemaining.toString(),
                        color = valueColor,
                        fontSize = middleValueTextSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.remaining),
                        color = subTextColor,
                        fontSize = subTextSize
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 8.dp),
                        text = calorieBurned.toString(),
                        color = valueColor,
                        fontSize = valueTextSize,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.burned),
                        color = subTextColor,
                        fontSize = subTextSize
                    )
                }
            }
        }
    }
}

@Composable
fun UiButton(
    modifier: Modifier = Modifier,
    text: String,
    clickable: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(4.dp)
                )
                .bouncyClick()
                .run {
                    if (clickable) {
                        clickable(onClick = onClick)
                    } else {
                        this
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}