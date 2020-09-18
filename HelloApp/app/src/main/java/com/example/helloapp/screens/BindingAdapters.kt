package com.example.helloapp.screens

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter


@BindingAdapter("imageUri")
fun loadImage(iv: ImageView, imageUri: Uri?) {
    if (imageUri != null) {
        iv.setImageURI(imageUri)
    }
}