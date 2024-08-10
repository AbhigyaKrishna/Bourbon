package me.abhigya.bourbon.core.ui.splash

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object SplashHomeViewModel : ViewModel() {

    private val _splashState = MutableStateFlow(true)
    val splashState = _splashState.asStateFlow()

    fun finish() {
        _splashState.value = false
    }

}