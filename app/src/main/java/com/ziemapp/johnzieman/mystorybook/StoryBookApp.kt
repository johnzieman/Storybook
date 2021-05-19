package com.ziemapp.johnzieman.mystorybook

import android.app.Application

class StoryBookApp: Application() {
    override fun onCreate() {
        super.onCreate()
        StorybookRepository.initialize(this)
    }
}