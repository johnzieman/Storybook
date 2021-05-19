package com.ziemapp.johnzieman.mystorybook.callbacks

import android.content.Context
import java.util.*

interface SelectedStory {
    fun onStorySelected(id: UUID)
}