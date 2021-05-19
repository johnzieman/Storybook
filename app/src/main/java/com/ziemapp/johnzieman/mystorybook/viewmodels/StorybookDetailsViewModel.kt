package com.ziemapp.johnzieman.mystorybook.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.ziemapp.johnzieman.mystorybook.StorybookRepository
import com.ziemapp.johnzieman.mystorybook.models.Story
import java.util.*

class StorybookDetailsViewModel: ViewModel() {
    private val storybookRepository = StorybookRepository.get()
    private val storyIdLiveDate = MutableLiveData<UUID>()

    val storyLiveData: LiveData<Story?> = Transformations.switchMap(storyIdLiveDate){
        storyId-> storybookRepository.getStory(storyId)
    }

    fun loadStory(storyId: UUID){
        storyIdLiveDate.value = storyId
    }

    fun saveStory(story: Story){
        storybookRepository.updateStory(story)
    }
    fun deleteStory(story: Story){
        storybookRepository.deleteStory(story)
    }
}