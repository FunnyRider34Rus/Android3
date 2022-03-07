package com.example.android3.model

sealed class APODState {
    data class Success (val serverResponseData: APODDTO) : APODState()
    data class Error (val error: Throwable) : APODState()
    data class Loading (val progress: Int?) : APODState()
}
