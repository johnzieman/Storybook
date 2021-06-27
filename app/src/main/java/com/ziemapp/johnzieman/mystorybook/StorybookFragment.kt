package com.ziemapp.johnzieman.mystorybook

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.ziemapp.johnzieman.mystorybook.databinding.FragmentStorybookBinding
import com.ziemapp.johnzieman.mystorybook.models.Story
import com.ziemapp.johnzieman.mystorybook.viewmodels.StorybookDetailsViewModel
import java.io.File
import java.text.DateFormat
import java.util.*

private val DIALOG_NAME = "DialogDate"
private const val REQUEST_DATE = 0
private const val REQUEST_PHOTO = 2

class StorybookFragment : Fragment(), DataPickerFragment.SelectedStoryDate {
    private var _binding: FragmentStorybookBinding? = null
    private val binding get() = _binding!!
    private var story = Story()
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private val args: StorybookFragmentArgs by navArgs()

    private val storybookDetailsViewModel: StorybookDetailsViewModel by lazy {
        ViewModelProvider(this).get(StorybookDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val storyId: UUID = args.storyId
        storybookDetailsViewModel.loadStory(storyId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStorybookBinding.inflate(inflater, container, false)
        binding.titleText.setText(story.title)
        binding.descriptionText.setText(story.description)
        binding.btnDate.text = story.date.toString()
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
                    updateUI(story)
                }
            }
        )
    }

    private fun updateUI(story: Story) {
        binding.titleText.setText(story.title)
        binding.descriptionText.setText(story.description)
        val currentDate = setTimeFormat(story)
        binding.btnDate.text = currentDate
        updatePhotoView()
    }

    private fun updatePhotoView() {
        if (photoFile.exists()) {
            val bitmap = UtilsPicture().getScaledBitmap(photoFile.path, requireActivity())
            binding.storyPhoto.setImageBitmap(bitmap)
        }
    }

    private fun setTimeFormat(story: Story): String {
        val calendar = Calendar.getInstance()
        calendar.time = story.date
        return DateFormat.getDateInstance(DateFormat.FULL).format(calendar.time)
    }

    private fun saveStory(story: Story) {
        storybookDetailsViewModel.saveStory(story)
    }


    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                story.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        binding.titleText.addTextChangedListener(titleWatcher)

        val descriptionWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                story.description = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.descriptionText.addTextChangedListener(descriptionWatcher)

        binding.btnDate.setOnClickListener {
            DataPickerFragment.newInstance(story.date).apply {
                setTargetFragment(this@StorybookFragment, REQUEST_DATE)
                show(this@StorybookFragment.parentFragmentManager, DIALOG_NAME)
            }

        }

        binding.btnSave.setOnClickListener {
            if (story.title.isBlank()) {
                val setWarningColor = ContextCompat.getColor(requireContext(), R.color.red)
                binding.titleText.setHintTextColor(setWarningColor)
                Toast.makeText(
                    context,
                    "To save your note add text to the title",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                saveStory(story)
                val action =
                    StorybookFragmentDirections.actionStorybookFragmentToStorybookListFragment()
                it.findNavController().navigate(action)
                Toast.makeText(context, "Swipe left or right to delete a note", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        binding.btnAddPhoto.apply {
            val packageManager: PackageManager = requireActivity().packageManager

            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }

            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                val cameraActivities: List<ResolveInfo> =
                    packageManager.queryIntentActivities(
                        captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY
                    )
                for (cameraActivity in cameraActivities) {
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(
                    photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                updatePhotoView()
            }
        }
    }

    override fun onDateSelected(date: Date) {
        story.date = date
        updateUI(story)
    }

    override fun onStop() {
        super.onStop()
        if (story.title.isBlank()) {
            storybookDetailsViewModel.deleteStory(this.story)
            Toast.makeText(context, "Not saved. The text fields were empty", Toast.LENGTH_SHORT)
                .show()
        } else {
            storybookDetailsViewModel.saveStory(story)
        }
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(
            photoUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}