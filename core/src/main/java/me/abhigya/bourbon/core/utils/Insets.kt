package me.abhigya.bourbon.core.utils

import android.os.Build
import android.view.View
import android.view.View.OnAttachStateChangeListener
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

val LocalInsets = staticCompositionLocalOf {
    DisplayInsets()
}

@Composable
@RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
fun ProvideDisplayInsets(
    consumeWindowInsets: Boolean = true,
    content: @Composable () -> Unit
) {
    val view = LocalView.current

    val displayInsets = remember {
        DisplayInsets().apply {
            ViewCompat.getRootWindowInsets(view)?.let {

            }
        }
    }

    DisposableEffect(view) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
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

@Stable
class DisplayInsets {

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

private fun Insets.updateFrom(windowInsets: WindowInsetsCompat, type: Int) {
    val insets = windowInsets.getInsets(type)
    left = insets.left
    top = insets.top
    right = insets.right
    bottom = insets.bottom

    isVisible = windowInsets.isVisible(type)
}