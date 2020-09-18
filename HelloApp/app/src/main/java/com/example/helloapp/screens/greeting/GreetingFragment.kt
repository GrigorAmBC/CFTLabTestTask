package com.example.helloapp.screens.greeting

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.helloapp.R
import com.example.helloapp.databinding.FragmentGreetingBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class GreetingFragment : Fragment() {
    private lateinit var binding: FragmentGreetingBinding
    private lateinit var greetingViewModel: GreetingViewModel
    private lateinit var greetingViewModelFactory: GreetingViewModelFactory
    private lateinit var photoUri: Uri
    private val CAPTURE_PHOTO_REQUEST = 105
    private val PICK_PHOTO_REQUEST = 106
    private val WRITE_PERMISSION_REQUEST = 108

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.greeting_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == WRITE_PERMISSION_REQUEST) {
            if (grantResults.size == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                dispatchPickPictureIntent()
            } else {
                Snackbar.make(
                    this.requireView(),
                    R.string.write_permission_not_granted,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.log_out -> {
                greetingViewModel.clearUserData(arguments)
                true
            }
            R.id.choose_avatar -> {
                selectImage()
                true
            }
            else -> false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_greeting,
            container,
            false
        )

        binding.lifecycleOwner = this
        binding.helloButton.setOnClickListener {
            onHello(it)
        }

        val sharedPref = this.requireActivity().getPreferences(Context.MODE_PRIVATE)
        greetingViewModelFactory = GreetingViewModelFactory(sharedPref, requireContext())
        greetingViewModel = ViewModelProvider(this, greetingViewModelFactory)
            .get(GreetingViewModel::class.java)

        greetingViewModel.setUserNameFromBundle(arguments)

        greetingViewModel.userName.observe(viewLifecycleOwner, {
            if (it == null) {
                requestSignup()
            }
        })

        binding.greetingViewModel = greetingViewModel

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }

        when (requestCode) {
            CAPTURE_PHOTO_REQUEST -> {
                greetingViewModel.setUserAvatar(photoUri)
            }
            PICK_PHOTO_REQUEST -> {
                data?.let {
                    greetingViewModel.setUserAvatar(it.data!!)
                }
            }
        }
    }


    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(this.requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
                photoFile?.also {
                    photoUri = FileProvider.getUriForFile(
                        this.requireContext(),
                        "com.example.helloapp.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, this.CAPTURE_PHOTO_REQUEST)
                }
            }
        }
    }


    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = requireContext()
            .getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun onHello(view: View) {
        val alertDialog = AlertDialog.Builder(view.context).create()
        alertDialog.setTitle("Greetings")
        alertDialog.setMessage("Hello, ${greetingViewModel.userName.value}!")
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "Hey, honey"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }

    private fun requestSignup() {
        this.findNavController()
            .navigate(R.id.action_greetingFragment_to_userCredentialsFragment)
    }

    private fun selectImage() {
        val options =
            arrayOf<CharSequence>(
                "Take Photo",
                "Choose from Gallery",
                "Cancel"
            )

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your profile picture")

        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Take Photo" -> {
                    dispatchTakePictureIntent()
                }
                "Choose from Gallery" -> {
                    if (checkPermissions()) {
                        dispatchPickPictureIntent()
                    }
                }
                "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private fun dispatchPickPictureIntent() {
        val pickPhoto =
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
        startActivityForResult(pickPhoto, PICK_PHOTO_REQUEST)
    }

    private fun checkPermissions(): Boolean {
        var permissionsGranted = true
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsGranted = false
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_PERMISSION_REQUEST
            )
        }

        return permissionsGranted
    }
}