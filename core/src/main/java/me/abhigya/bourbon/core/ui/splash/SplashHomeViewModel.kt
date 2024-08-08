package me.abhigya.bourbon.core.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashHomeViewModel : ViewModel() {

    private val _splashState = MutableStateFlow(true)
    val splashState = _splashState.asStateFlow()

    init {
        viewModelScope.launch {
            delay(10)
            _splashState.value = false
        }
    }

}