package com.ziemapp.johnzieman.mystorybook

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ziemapp.johnzieman.mystorybook.callbacks.SavedStory
import com.ziemapp.johnzieman.mystorybook.callbacks.SelectedStory
import com.ziemapp.johnzieman.mystorybook.databinding.FragmentStorybookListBinding
import com.ziemapp.johnzieman.mystorybook.models.Story
import com.ziemapp.johnzieman.mystorybook.viewmodels.StorybookListViewModel
import java.text.DateFormat
import java.util.*

class StorybookListFragment : Fragment() {
    private var _binding: FragmentStorybookListBinding? = null
    private val binding get() = _binding!!

    private val storybookListViewModel: StorybookListViewModel by lazy {
        ViewModelProvider(this).get(StorybookListViewModel::class.java)
    }
    private var storyAdapter: StoryAdapter? = null
    private lateinit var story: Story
    private var selectedStory: SelectedStory? = null
    private var savedStory: SavedStory? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        selectedStory = context as SelectedStory?
        savedStory = context as SavedStory?
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStorybookListBinding.inflate(inflater, container, false)
        binding.storyListRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.storyListRecyclerView.adapter = storyAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storybookListViewModel.storyListLiveData.observe(
            viewLifecycleOwner,
            Observer { stories ->
                updateUI(stories )
            }
        )
    }


    private inner class StoryAdapter(var stories: List<Story>) :
        RecyclerView.Adapter<StoryViewHolder>() {
        private var previousColor = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
            val view = layoutInflater.inflate(R.layout.story_item, parent, false)
            return StoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
            val story = stories[position]
            holder.bind(story, position)
            var color = randomColors()
            if (previousColor == color) {
                color = randomColors()
            }
            previousColor = color
            val setRandomColor = ContextCompat.getColor(context!!, color)
            holder.storyParent.setBackgroundColor(setRandomColor)
        }



        override fun getItemCount() = stories.size
    }

    private inner class StoryViewHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        private lateinit var story: Story

        val storyTitle = view.findViewById(R.id.storyTitle) as TextView
        val storyDescription = view.findViewById(R.id.storyDescription) as TextView
        val storyDate = view.findViewById(R.id.storyDate) as TextView
        val storyParent = view.findViewById(R.id.parent) as CardView

        @SuppressLint("SetTextI18n")
        fun bind(story: Story, position: Int) {
            this.story = story
            storyTitle.text = story.description
            storyDescription.text = story.description
            val currentDate = setTimeFormat(story)
            storyDate.text = currentDate
        }

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            selectedStory?.onStorySelected(story.id)
        }
    }


    private fun updateUI(stories: List<Story>) {
        storyAdapter = StoryAdapter(stories)
        binding.storyListRecyclerView.adapter = storyAdapter
    }

    private fun randomColors(): Int {
        return when ((1..15).shuffled().last()) {
            1 -> R.color.two
            2 -> R.color.three
            3 -> R.color.four
            4 -> R.color.five
            5 -> R.color.six
            6 -> R.color.seven
            7 -> R.color.eight
            8 -> R.color.nine
            9 -> R.color.ten
            10 -> R.color.eleven
            11 -> R.color.twelwe
            12 -> R.color.thirteen
            13 -> R.color.fourteen
            14 -> R.color.fifteen
            15 -> R.color.sixteen
            else -> R.color.purple_700
        }
    }

    private fun setTimeFormat(story: Story): String {
        val calendar = Calendar.getInstance()
        calendar.time = story.date
        return DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)
    }


    override fun onStart() {
        super.onStart()
        binding.addStoryItem.setOnClickListener {
            val story = Story()
            storybookListViewModel.addStory(story)
            savedStory?.onSavedStory(story.id)
        }
    }

    override fun onDetach() {
        super.onDetach()
        selectedStory = null
        savedStory = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}