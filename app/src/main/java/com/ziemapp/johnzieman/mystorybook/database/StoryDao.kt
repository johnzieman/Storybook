package com.ziemapp.johnzieman.mystorybook.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ziemapp.johnzieman.mystorybook.models.Story
import java.util.*

@Dao
interface StoryDao {
    @Query("SELECT * FROM story ORDER BY date DESC")
    fun getStories(): LiveData<List<Story>>
    @Query("SELECT * FROM story WHERE id=(:id)")
    fun getStory(id: UUID): LiveData<Story?>
    @Insert
    fun addStory(story: Story)
    @Update
    fun updateStory(story: Story)
    @Delete
    fun deleteStory(story: Story)
}