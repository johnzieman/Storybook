package com.ziemapp.johnzieman.mystorybook.viewmodels

import androidx.lifecycle.ViewModel
import com.ziemapp.johnzieman.mystorybook.StorybookRepository
import com.ziemapp.johnzieman.mystorybook.models.Story

class StorybookListViewModel: ViewModel() {
    private val storybookRepository: StorybookRepository = StorybookRepository.get()
    val storyListLiveData = storybookRepository.getStories()
    fun addStory(story: Story){
        storybookRepository.addStory(story)
    }

    fun delete(story: Story){
        storybookRepository.deleteStory(story)
    }
}