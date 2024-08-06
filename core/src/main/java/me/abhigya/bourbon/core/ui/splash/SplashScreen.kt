package me.abhigya.bourbon.core.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import me.abhigya.bourbon.core.ui.AppScreen

abstract class SplashScreen : AppScreen {

    @Composable
    override operator fun invoke() {
        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Content()
            }
        }
    }

    @Composable
    abstract fun Content()

    @Composable
    protected fun Text(text: String) {
        androidx.compose.material3.Text(
            text = text,
            color = MaterialTheme.colorScheme.background,
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }

}