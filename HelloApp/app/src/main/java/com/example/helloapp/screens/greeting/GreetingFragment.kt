package com.example.helloapp.screens.greeting

import android.R.attr
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.helloapp.R
import com.example.helloapp.databinding.FragmentGreetingBinding


class GreetingFragment : Fragment() {
    private lateinit var binding: FragmentGreetingBinding
    private lateinit var greetingViewModel: GreetingViewModel
    private lateinit var greetingViewModelFactory: GreetingViewModelFactory
    private val TAKE_PHOTO_REQUEST = 105
    private val PICK_PHOTO_REQUEST = 106

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.greeting_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.log_out -> {
                greetingViewModel.clearUsernameData(arguments)
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
        greetingViewModelFactory = GreetingViewModelFactory(sharedPref)
        greetingViewModel = ViewModelProvider(this, greetingViewModelFactory)
            .get(GreetingViewModel::class.java)

        greetingViewModel.setUserNameFromBundle(arguments)

        greetingViewModel.userName.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                requestSignup()
            }
        })

        binding.greetingViewModel = greetingViewModel

        return binding.root
    }

    private fun requestSignup() {
        this.findNavController().navigate(R.id.action_greetingFragment_to_userCredentialsFragment)
    }

    private fun selectImage() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your profile picture")

        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Take Photo" -> {
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePicture, TAKE_PHOTO_REQUEST)
                }
                "Choose from Gallery" -> {
                    val pickPhoto =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, PICK_PHOTO_REQUEST)
                }
                "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) {
            return
        }

        when (requestCode) {
            TAKE_PHOTO_REQUEST -> {
                setCapturedPhoto(data.extras?.get("data") as Bitmap)
            }
            PICK_PHOTO_REQUEST -> {
                setPickedPhoto(data.data)
            }
        }
    }

    private fun setCapturedPhoto(bitmap: Bitmap) {
        greetingViewModel.setUserAvatar(bitmap)
    }

    private fun setPickedPhoto(selectedImage: Uri?) {
        if (selectedImage != null) {
            greetingViewModel.setUserAvatar(selectedImage, requireActivity().contentResolver)
        }
    }

    private fun onHello(view: View) {
        val alertDialog = AlertDialog.Builder(view.context).create()
        alertDialog.setTitle("Greetings")
        alertDialog.setMessage("Hello, " + greetingViewModel.userName.value + "!")
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "Hey, honey"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }
}