package com.ziemapp.johnzieman.mystorybook.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ziemapp.johnzieman.mystorybook.models.Story

@Database(entities = [Story::class], version = 1, exportSchema = false)
@TypeConverters(StoryTypesConverters::class)
abstract class StoryDatabase: RoomDatabase() {
    abstract fun getStoryDao(): StoryDao
}