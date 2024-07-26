package me.abhigya.bourbon.core.utils

import android.view.View
import android.view.View.OnAttachStateChangeListener
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Taken from https://goo.gle/compose-insets. Requires androidx.core:core v1.5.0-alpha02+
 */

val LocalInsets = staticCompositionLocalOf {
    DisplayInsets()
}

/**
 * Applies any [WindowInsetsCompat] values to [LocalInsets], which are then available
 * within [content].
 *
 * @param consumeWindowInsets Whether to consume any [WindowInsetsCompat]s which are dispatched to
 * the host view. Defaults to `true`.
 */
@Composable
fun ProvideDisplayInsets(
    consumeWindowInsets: Boolean = true,
    content: @Composable () -> Unit
) {
    val view = LocalView.current

    val displayInsets = remember {
        DisplayInsets().apply {
            ViewCompat.getRootWindowInsets(view)?.let {
                systemBars.updateFrom(it, WindowInsetsCompat.Type.systemBars())
                systemGestures.updateFrom(it, WindowInsetsCompat.Type.systemGestures())
                navigationBars.updateFrom(it, WindowInsetsCompat.Type.navigationBars())
                statusBars.updateFrom(it, WindowInsetsCompat.Type.statusBars())
                ime.updateFrom(it, WindowInsetsCompat.Type.ime())
            }
        }
    }

    DisposableEffect(view) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            displayInsets.systemBars.updateFrom(windowInsets, WindowInsetsCompat.Type.systemBars())
            displayInsets.systemGestures.updateFrom(windowInsets, WindowInsetsCompat.Type.systemGestures())
            displayInsets.navigationBars.updateFrom(windowInsets, WindowInsetsCompat.Type.navigationBars())
            displayInsets.statusBars.updateFrom(windowInsets, WindowInsetsCompat.Type.statusBars())
            displayInsets.ime.updateFrom(windowInsets, WindowInsetsCompat.Type.ime())

            if (consumeWindowInsets) {
                WindowInsetsCompat.CONSUMED
            } else {
                windowInsets
            }
        }

        val listener = object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) = v.requestApplyInsets()

            override fun onViewDetachedFromWindow(v: View) = Unit
        }
        view.addOnAttachStateChangeListener(listener)

        if (view.isAttachedToWindow) {
            view.requestApplyInsets()
        }

        onDispose {
            view.removeOnAttachStateChangeListener(listener)
        }
    }

    CompositionLocalProvider(LocalInsets provides displayInsets) {
        content()
    }
}

/**
 * Main holder of our inset values.
 */
@Stable
class DisplayInsets {
    /**
     * Inset values which match [WindowInsetsCompat.Type.systemBars]
     */
    val systemBars = Insets()

    /**
     * Inset values which match [WindowInsetsCompat.Type.systemGestures]
     */
    val systemGestures = Insets()

    /**
     * Inset values which match [WindowInsetsCompat.Type.navigationBars]
     */
    val navigationBars = Insets()

    /**
     * Inset values which match [WindowInsetsCompat.Type.statusBars]
     */
    val statusBars = Insets()

    /**
     * Inset values which match [WindowInsetsCompat.Type.ime]
     */
    val ime = Insets()
}

@Stable
class Insets {
    /**
     * The left dimension of these insets in pixels.
     */
    var left by mutableIntStateOf(0)
        internal set

    /**
     * The top dimension of these insets in pixels.
     */
    var top by mutableIntStateOf(0)
        internal set

    /**
     * The right dimension of these insets in pixels.
     */
    var right by mutableIntStateOf(0)
        internal set

    /**
     * The bottom dimension of these insets in pixels.
     */
    var bottom by mutableIntStateOf(0)
        internal set

    /**
     * Whether the insets are currently visible.
     */
    var isVisible by mutableStateOf(true)
        internal set
}

/**
 * Updates our mutable state backed [Insets] from an Android system insets.
 */
private fun Insets.updateFrom(windowInsets: WindowInsetsCompat, type: Int) {
    val insets = windowInsets.getInsets(type)
    left = insets.left
    top = insets.top
    right = insets.right
    bottom = insets.bottom

    isVisible = windowInsets.isVisible(type)
}


/**
 * Selectively apply additional space which matches the width/height of any system bars present
 * on the respective edges of the screen.
 *
 * @param enabled Whether to apply padding using the system bars dimensions on the respective edges.
 * Defaults to `true`.
 */
fun Modifier.systemBarsPadding(enabled: Boolean = true) = composed {
    insetsPadding(
        insets = LocalInsets.current.systemBars,
        left = enabled,
        top = enabled,
        right = enabled,
        bottom = enabled
    )
}

/**
 * Apply additional space which matches the height of the status bars height along the top edge
 * of the content.
 */
fun Modifier.statusBarsPadding() = composed {
    insetsPadding(insets = LocalInsets.current.statusBars, top = true)
}

/**
 * Apply additional space which matches the height of the navigation bars height
 * along the [bottom] edge of the content, and additional space which matches the width of
 * the navigation bars on the respective [left] and [right] edges.
 *
 * @param bottom Whether to apply padding to the bottom edge, which matches the navigation bars
 * height (if present) at the bottom edge of the screen. Defaults to `true`.
 * @param left Whether to apply padding to the left edge, which matches the navigation bars width
 * (if present) on the left edge of the screen. Defaults to `true`.
 * @param right Whether to apply padding to the right edge, which matches the navigation bars width
 * (if present) on the right edge of the screen. Defaults to `true`.
 */
fun Modifier.navigationBarsPadding(
    bottom: Boolean = true,
    left: Boolean = true,
    right: Boolean = true
) = composed {
    insetsPadding(
        insets = LocalInsets.current.navigationBars,
        left = left,
        right = right,
        bottom = bottom
    )
}

/**
 * Declare the height of the content to match the height of the status bars exactly.
 *
 * This is very handy when used with `Spacer` to push content below the status bars:
 * ```
 * Column {
 *     Spacer(Modifier.statusBarHeight())
 *
 *     // Content to be drawn below status bars (y-axis)
 * }
 * ```
 *
 * It's also useful when used to draw a scrim which matches the status bars:
 * ```
 * Spacer(
 *     Modifier.statusBarHeight()
 *         .fillMaxWidth()
 *         .drawBackground(MaterialTheme.colors.background.copy(alpha = 0.3f)
 * )
 * ```
 *
 * Internally this matches the behavior of the [Modifier.height] modifier.
 *
 * @param additional Any additional height to add to the status bars size.
 */
fun Modifier.statusBarsHeight(additional: Dp = 0.dp) = this.composed {
    InsetsSizeModifier(
        insets = LocalInsets.current.statusBars,
        heightSide = VerticalSide.Top,
        additionalHeight = additional
    )
}

/**
 * Declare the height of the content to match the height of the status bars exactly.
 *
 * This is very handy when used with `Spacer` to push content below the status bars:
 * ```
 * Column {
 *     Spacer(Modifier.statusBarHeight())
 *
 *     // Content to be drawn below status bars (y-axis)
 * }
 * ```
 *
 * It's also useful when used to draw a scrim which matches the status bars:
 * ```
 * Spacer(
 *     Modifier.statusBarHeight()
 *         .fillMaxWidth()
 *         .drawBackground(MaterialTheme.colors.background.copy(alpha = 0.3f)
 * )
 * ```
 *
 * Internally this matches the behavior of the [Modifier.height] modifier.
 */
inline fun Modifier.statusBarsHeight() = statusBarsHeightPlus(0.dp)

/**
 * Declare the height of the content to match the height of the status bars, plus some
 * additional height passed in via [additional].
 *
 * As an example, this could be used with `Spacer` to push content below the status bar
 * and app bars:
 *
 * ```
 * Column {
 *     Spacer(Modifier.statusBarHeightPlus(56.dp))
 *
 *     // Content to be drawn below status bars and app bar (y-axis)
 * }
 * ```
 *
 * Internally this matches the behavior of the [Modifier.height] modifier.
 *
 * @param additional Any additional height to add to the status bars size.
 */
fun Modifier.statusBarsHeightPlus(additional: Dp) = this.composed {
    InsetsSizeModifier(
        insets = LocalInsets.current.statusBars,
        heightSide = VerticalSide.Top,
        additionalHeight = additional
    )
}

/**
 * Declare the preferred height of the content to match the height of the navigation bars when
 * present at the bottom of the screen.
 *
 * This is very handy when used with `Spacer` to push content below the navigation bars:
 * ```
 * Column {
 *     // Content to be drawn above status bars (y-axis)
 *     Spacer(Modifier.navigationBarHeight())
 * }
 * ```
 *
 * It's also useful when used to draw a scrim which matches the navigation bars:
 * ```
 * Spacer(
 *     Modifier.navigationBarHeight()
 *         .fillMaxWidth()
 *         .drawBackground(MaterialTheme.colors.background.copy(alpha = 0.3f)
 * )
 * ```
 *
 * Internally this matches the behavior of the [Modifier.height] modifier.
 */
inline fun Modifier.navigationBarsHeight() = navigationBarsHeightPlus(0.dp)

/**
 * Declare the height of the content to match the height of the navigation bars, plus some
 * additional height passed in via [additional]
 *
 * As an example, this could be used with `Spacer` to push content above the navigation bar
 * and bottom app bars:
 *
 * ```
 * Column {
 *     // Content to be drawn above navigation bars and bottom app bar (y-axis)
 *
 *     Spacer(Modifier.statusBarHeightPlus(48.dp))
 * }
 * ```
 *
 * Internally this matches the behavior of the [Modifier.height] modifier.
 *
 * @param additional Any additional height to add to the status bars size.
 */
fun Modifier.navigationBarsHeightPlus(additional: Dp) = this.composed {
    InsetsSizeModifier(
        insets = LocalInsets.current.navigationBars,
        heightSide = VerticalSide.Bottom,
        additionalHeight = additional
    )
}

enum class HorizontalSide { Left, Right }
enum class VerticalSide { Top, Bottom }

/**
 * Declare the preferred width of the content to match the width of the navigation bars,
 * on the given [side].
 *
 * This is very handy when used with `Spacer` to push content inside from any vertical
 * navigation bars (typically when the device is in landscape):
 * ```
 * Row {
 *     Spacer(Modifier.navigationBarWidth(HorizontalSide.Left))
 *
 *     // Content to be inside the navigation bars (x-axis)
 *
 *     Spacer(Modifier.navigationBarWidth(HorizontalSide.Right))
 * }
 * ```
 *
 * It's also useful when used to draw a scrim which matches the navigation bars:
 * ```
 * Spacer(
 *     Modifier.navigationBarWidth(HorizontalSide.Left)
 *         .fillMaxHeight()
 *         .drawBackground(MaterialTheme.colors.background.copy(alpha = 0.3f)
 * )
 * ```
 *
 * Internally this matches the behavior of the [Modifier.height] modifier.
 *
 * @param side The navigation bar side to use as the source for the width.
 */
inline fun Modifier.navigationBarWidth(side: HorizontalSide) = navigationBarsWidthPlus(side, 0.dp)

/**
 * Declare the preferred width of the content to match the width of the navigation bars,
 * on the given [side], plus additional width passed in via [additional].
 *
 * Internally this matches the behavior of the [Modifier.height] modifier.
 *
 * @param side The navigation bar side to use as the source for the width.
 * @param additional Any additional width to add to the status bars size.
 */
fun Modifier.navigationBarsWidthPlus(
    side: HorizontalSide,
    additional: Dp
) = this.composed {
    InsetsSizeModifier(
        insets = LocalInsets.current.navigationBars,
        widthSide = side,
        additionalWidth = additional
    )
}

fun Modifier.imeHeight() = this.composed {
    InsetsSizeModifier(
        insets = LocalInsets.current.ime,
        heightSide = VerticalSide.Bottom,
        additionalWidth = 0.dp
    )
}

fun Modifier.imePadding() = composed {
    insetsPadding(insets = LocalInsets.current.ime, bottom = true)
}

/**
 * Returns the current insets converted into a [InnerPadding].
 *
 * @param start Whether to apply the inset on the start dimension.
 * @param top Whether to apply the inset on the top dimension.
 * @param end Whether to apply the inset on the end dimension.
 * @param bottom Whether to apply the inset on the bottom dimension.
 */
@Composable
fun Insets.toInnerPadding(
    start: Boolean = true,
    top: Boolean = true,
    end: Boolean = true,
    bottom: Boolean = true
): PaddingValues = with(LocalDensity.current) {
    val layoutDirection = LocalLayoutDirection.current
    PaddingValues(
        start = when {
            start && layoutDirection == LayoutDirection.Ltr -> this@toInnerPadding.left.toDp()
            start && layoutDirection == LayoutDirection.Rtl -> this@toInnerPadding.right.toDp()
            else -> 0.dp
        },
        top = when {
            top -> this@toInnerPadding.top.toDp()
            else -> 0.dp
        },
        end = when {
            end && layoutDirection == LayoutDirection.Ltr -> this@toInnerPadding.right.toDp()
            end && layoutDirection == LayoutDirection.Rtl -> this@toInnerPadding.left.toDp()
            else -> 0.dp
        },
        bottom = when {
            bottom -> this@toInnerPadding.bottom.toDp()
            else -> 0.dp
        }
    )
}

/**
 * Allows conditional setting of [insets] on each dimension.
 */
private inline fun Modifier.insetsPadding(
    insets: Insets,
    left: Boolean = false,
    top: Boolean = false,
    right: Boolean = false,
    bottom: Boolean = false
) = this then InsetsPaddingModifier(insets, left, top, right, bottom)

private data class InsetsPaddingModifier(
    private val insets: Insets,
    private val applyLeft: Boolean = false,
    private val applyTop: Boolean = false,
    private val applyRight: Boolean = false,
    private val applyBottom: Boolean = false
) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val left = if (applyLeft) insets.left else 0
        val top = if (applyTop) insets.top else 0
        val right = if (applyRight) insets.right else 0
        val bottom = if (applyBottom) insets.bottom else 0
        val horizontal = left + right
        val vertical = top + bottom

        val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

        val width = (placeable.width + horizontal)
            .coerceIn(constraints.minWidth, constraints.maxWidth)
        val height = (placeable.height + vertical)
            .coerceIn(constraints.minHeight, constraints.maxHeight)
        return layout(width, height) {
            placeable.place(left, top)
        }
    }
}

private data class InsetsSizeModifier(
    private val insets: Insets,
    private val widthSide: HorizontalSide? = null,
    private val additionalWidth: Dp = 0.dp,
    private val heightSide: VerticalSide? = null,
    private val additionalHeight: Dp = 0.dp
) : LayoutModifier {
    private val Density.targetConstraints: Constraints
        get() {
            val additionalWidthPx = additionalWidth.roundToPx()
            val additionalHeightPx = additionalHeight.roundToPx()
            return Constraints(
                minWidth = additionalWidthPx + when (widthSide) {
                    HorizontalSide.Left -> insets.left
                    HorizontalSide.Right -> insets.right
                    null -> 0
                },
                minHeight = additionalHeightPx + when (heightSide) {
                    VerticalSide.Top -> insets.top
                    VerticalSide.Bottom -> insets.bottom
                    null -> 0
                },
                maxWidth = when (widthSide) {
                    HorizontalSide.Left -> insets.left + additionalWidthPx
                    HorizontalSide.Right -> insets.right + additionalWidthPx
                    null -> Constraints.Infinity
                },
                maxHeight = when (heightSide) {
                    VerticalSide.Top -> insets.top + additionalHeightPx
                    VerticalSide.Bottom -> insets.bottom + additionalHeightPx
                    null -> Constraints.Infinity
                }
            )
        }

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val wrappedConstraints = targetConstraints.let { targetConstraints ->
            val resolvedMinWidth = if (widthSide != null) {
                targetConstraints.minWidth
            } else {
                constraints.minWidth.coerceAtMost(targetConstraints.maxWidth)
            }
            val resolvedMaxWidth = if (widthSide != null) {
                targetConstraints.maxWidth
            } else {
                constraints.maxWidth.coerceAtLeast(targetConstraints.minWidth)
            }
            val resolvedMinHeight = if (heightSide != null) {
                targetConstraints.minHeight
            } else {
                constraints.minHeight.coerceAtMost(targetConstraints.maxHeight)
            }
            val resolvedMaxHeight = if (heightSide != null) {
                targetConstraints.maxHeight
            } else {
                constraints.maxHeight.coerceAtLeast(targetConstraints.minHeight)
            }
            Constraints(
                resolvedMinWidth,
                resolvedMaxWidth,
                resolvedMinHeight,
                resolvedMaxHeight
            )
        }
        val placeable = measurable.measure(wrappedConstraints)
        return layout(placeable.width, placeable.height) {
            placeable.place(0, 0)
        }
    }

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.minIntrinsicWidth(height).let {
        val constraints = targetConstraints
        it.coerceIn(constraints.minWidth, constraints.maxWidth)
    }

    override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.maxIntrinsicWidth(height).let {
        val constraints = targetConstraints
        it.coerceIn(constraints.minWidth, constraints.maxWidth)
    }

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.minIntrinsicHeight(width).let {
        val constraints = targetConstraints
        it.coerceIn(constraints.minHeight, constraints.maxHeight)
    }

    override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.maxIntrinsicHeight(width).let {
        val constraints = targetConstraints
        it.coerceIn(constraints.minHeight, constraints.maxHeight)
    }
}