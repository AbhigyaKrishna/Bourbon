package me.abhigya.bourbon.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * A composable function that displays a text with a typewriter-like effect, revealing characters in chunks.
 *
 * @param text The input text to be displayed with the typewriter effect.
 * @param minDelayInMillis The minimum delay in milliseconds between revealing character chunks, defaults to 10ms.
 * @param maxDelayInMillis The maximum delay in milliseconds between revealing character chunks, defaults to 50ms.
 * @param minCharacterChunk The minimum number of characters to reveal at once, defaults to 1.
 * @param maxCharacterChunk The maximum number of characters to reveal at once, defaults to 5.
 * @param onEffectCompleted A callback function invoked when the entire text has been revealed.
 * @param displayTextComposable A composable function that receives the text to display with the typewriter effect.
 *
 * @throws IllegalArgumentException if [minDelayInMillis] is greater than [maxDelayInMillis].
 * @throws IllegalArgumentException if [minCharacterChunk] is greater than [maxCharacterChunk].
 */
@Composable
fun TypewriterTextEffect(
    text: String,
    minDelayInMillis: Long = 10,
    maxDelayInMillis: Long = 50,
    minCharacterChunk: Int = 1,
    maxCharacterChunk: Int = 5,
    onEffectCompleted: () -> Unit = {},
    displayTextComposable: @Composable (displayedText: String) -> Unit
) {
    require(minDelayInMillis <= maxDelayInMillis) {
        "TypewriterTextEffect: Invalid delay range. minDelayInMillis ($minDelayInMillis) must be less than or equal to maxDelayInMillis ($maxDelayInMillis)."
    }

    require(minCharacterChunk <= maxCharacterChunk) {
        "TypewriterTextEffect: Invalid character chunk range. minCharacterChunk ($minCharacterChunk) must be less than or equal to maxCharacterChunk ($maxCharacterChunk)."
    }

    var displayedText by remember { mutableStateOf("") }

    displayTextComposable(displayedText)

    LaunchedEffect(text) {
        val textLength = text.length
        var endIndex = 0

        while (endIndex < textLength) {
            endIndex = minOf(
                endIndex + Random.nextInt(minCharacterChunk, maxCharacterChunk + 1),
                textLength
            )
            displayedText = text.substring(startIndex = 0, endIndex = endIndex)
            delay(Random.nextLong(minDelayInMillis, maxDelayInMillis))
        }
        onEffectCompleted()
    }
}