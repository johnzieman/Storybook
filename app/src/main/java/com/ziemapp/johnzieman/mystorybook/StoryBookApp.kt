package com.ziemapp.johnzieman.mystorybook

import android.app.Application
import timber.log.Timber

class StoryBookApp: Application() {
    override fun onCreate() {
        super.onCreate()
        StorybookRepository.initialize(this)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}