package io.github.agimaulana.codemagicconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.agimaulana.codemagicconnect.domain.model.ThemeMode
import io.github.agimaulana.codemagicconnect.domain.usecase.settings.ObserveThemeModeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val observeThemeModeUseCase: ObserveThemeModeUseCase
) : ViewModel() {

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode = _themeMode.asStateFlow()

    fun init() {
        viewModelScope.launch {
            observeThemeModeUseCase().collect { _themeMode.value = it }
        }
    }
}
