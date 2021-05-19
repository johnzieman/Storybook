package com.ziemapp.johnzieman.mystorybook

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.ziemapp.johnzieman.mystorybook.database.StoryDatabase
import com.ziemapp.johnzieman.mystorybook.models.Story
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "story_db"

class StorybookRepository private constructor(context: Context) {


    private val datebase: StoryDatabase = Room.databaseBuilder(
        context.applicationContext,
        StoryDatabase::class.java,
        DATABASE_NAME
    ).build()


    private val storyDao = datebase.getStoryDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getStories(): LiveData<List<Story>> = storyDao.getStories()
    fun getStory(id: UUID): LiveData<Story?> = storyDao.getStory(id)
    fun addStory(story: Story) {
        executor.execute {
            storyDao.addStory(story)
        }
    }
    fun updateStory(story: Story){
        executor.execute{
            storyDao.updateStory(story)
        }
    }
    fun deleteStory(story: Story){
        executor.execute{
            storyDao.deleteStory(story)
        }
    }


    companion object {
        private var INSTANCE: StorybookRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = StorybookRepository(context)
            }
        }

        fun get(): StorybookRepository =
            INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
    }
}