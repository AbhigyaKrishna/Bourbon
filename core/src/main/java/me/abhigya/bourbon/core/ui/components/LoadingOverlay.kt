package me.abhigya.bourbon.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

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