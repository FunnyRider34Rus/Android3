package com.example.android3

import android.app.Application
import com.google.android.material.color.DynamicColors

class App : Application() {

    override fun onCreate() {
        DynamicColors.applyToActivitiesIfAvailable(this, R.style.AppTheme_Overlay)
        super.onCreate()
    }
}