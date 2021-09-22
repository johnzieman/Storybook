package com.ziemapp.johnzieman.mystorybook

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ziemapp.johnzieman.mystorybook.callbacks.EditStory
import com.ziemapp.johnzieman.mystorybook.callbacks.SelectedStory
import com.ziemapp.johnzieman.mystorybook.databinding.FragmentStoryBookViewBinding
import com.ziemapp.johnzieman.mystorybook.models.Story
import com.ziemapp.johnzieman.mystorybook.viewmodels.StorybookDetailsViewModel
import java.io.File
import java.util.*


class StoryBookViewFragment : Fragment() {
    private var _binding: FragmentStoryBookViewBinding? = null
    private val binding get() = _binding!!

    private var editStory: EditStory? = null

    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    private val args: StoryBookViewFragmentArgs by navArgs()

    private var story = Story()
    private val storybookDetailsViewModel: StorybookDetailsViewModel by lazy {
        ViewModelProvider(this).get(StorybookDetailsViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        editStory = (context as EditStory?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val storyId: UUID = args.storyId
        storybookDetailsViewModel.loadStory(storyId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoryBookViewBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storybookDetailsViewModel.storyLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { story ->
                story?.let {
                    this.story = story
                    photoFile = storybookDetailsViewModel.getPhotoFile(story)
                    photoUri = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.ziemapp.johnzieman.mystorybook.fileprovider",
                        photoFile
                    )
                    updateUI(this.story)
                }
            }
        )
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.story_book_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.menu_share -> {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    if (story.title.isBlank()) {
                        putExtra(Intent.EXTRA_SUBJECT, getString(R.string.no_subject))
                    } else {
                        putExtra(Intent.EXTRA_SUBJECT, story.title)
                    }
                    putExtra(Intent.EXTRA_TEXT, story.description)
                    type = "text/plain"
                }

                try {
                    val chooserIntent = Intent.createChooser(sendIntent, "Send your note via")
                    startActivity(chooserIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(requireContext(), "Service is unavailable", Toast.LENGTH_LONG)
                        .show()
                }
                true
            }
            R.id.menu_delete -> {
                storybookDetailsViewModel.deleteStory(story)
                (activity as MainActivity).navController.navigate(R.id.action_storyBookViewFragment_to_storybookListFragment)
                true
            }
            R.id.edit_story -> {
                editStory?.onEditSelectedStory(story.id)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    }

    private fun updateUI(story: Story) {
        binding.detailDate.text = story.date.toString()
        binding.detailTitle.text = story.title
        binding.detailDescription.text = story.description
        updatePhotoView()
    }

    private fun updatePhotoView() {
        if (photoFile.exists()) {
            val bitmap = UtilsPicture().getScaledBitmap(photoFile.path, requireActivity())
            binding.imageView.setImageBitmap(bitmap)
        } else {
            binding.imageView.setImageDrawable(null)
        }
    }

    override fun onDetach() {
        super.onDetach()
        editStory = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}