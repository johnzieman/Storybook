package com.ziemapp.johnzieman.mystorybook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.ziemapp.johnzieman.mystorybook.callbacks.EditStory
import com.ziemapp.johnzieman.mystorybook.callbacks.SavedStory
import com.ziemapp.johnzieman.mystorybook.callbacks.SelectedStory
import com.ziemapp.johnzieman.mystorybook.databinding.ActivityMainBinding
import java.util.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), SelectedStory, SavedStory, EditStory {
    lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = Navigation.findNavController(this, R.id.nav_host)
    }

    override fun onStorySelected(id: UUID) {
        val action = StorybookListFragmentDirections.actionStorybookListFragmentToStoryBookViewFragment(id)
        navController.navigate(action)
    }

    override fun onSavedStory(id: UUID) {
        val action = StorybookListFragmentDirections.actionStorybookListFragmentToStorybookFragment(id)
        navController.navigate(action)
    }

    override fun onEditSelectedStory(storyId: UUID) {
        val action = StoryBookViewFragmentDirections.actionStoryBookViewFragmentToStorybookFragment(storyId)
        navController.navigate(action)
    }


}