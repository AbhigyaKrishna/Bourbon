package me.abhigya.bourbon.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jeziellago.compose.markdowntext.MarkdownText
import me.abhigya.bourbon.core.R
import me.abhigya.bourbon.core.utils.animatedGradient

sealed interface OutputState {
    data object None : OutputState
    data object Loading : OutputState
    data class Data(val text: String) : OutputState
    data class Error(val error: Throwable) : OutputState
}

@Composable
fun AnimatedLoadingGradient(
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = MaterialTheme.colorScheme.secondary,
    rows: Int = 3,
    lastRowLength: Float = 0.7f
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        for (i in 0 until rows) {
            val last = i == rows - 1
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(if (last) lastRowLength else 1f)
                    .animatedGradient(
                        primaryColor = containerColor,
                        containerColor = primaryColor
                    )
            )
            if (!last) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun GeminiTypeWrite(
    modifier: Modifier = Modifier,
    text: String,
    height: Dp = 200.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .height(64.dp)
                .width(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.gemini),
                contentDescription = stringResource(R.string.ai_text),
                modifier = Modifier
                    .size(16.dp),
                tint = Color.Unspecified
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp, end = 16.dp)
                .clip(RoundedCornerShape(8.dp))
                .height(height)
                .background(MaterialTheme.colorScheme.background)
        ) {
            TypewriterTextEffect(text = text) {
                MarkdownText(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .clickable(
                            enabled = false,
                            onClick = { }
                        ),
                    markdown = it,
                    style = TextStyle(
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Composable
fun GeminiOutput(
    modifier: Modifier = Modifier,
    state: OutputState,
    height: Dp = 200.dp,
    renderNoneState: (@Composable () -> Unit)? = null
) {
    when (state) {
        OutputState.Loading -> Box(
            modifier = modifier
                .height(height)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedLoadingGradient(rows = 4)
        }
        is OutputState.Data -> GeminiTypeWrite(
            modifier = modifier,
            text = state.text
        )
        is OutputState.Error -> GeminiTypeWrite(
            modifier = modifier,
            text = stringResource(R.string.an_error_occurred, state.error.localizedMessage.orEmpty())
        )
        else -> renderNoneState?.invoke()
    }
}