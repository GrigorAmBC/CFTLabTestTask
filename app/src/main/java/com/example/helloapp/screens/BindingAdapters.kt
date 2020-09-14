package com.example.helloapp.screens

import android.graphics.Bitmap
import android.widget.ImageView

import androidx.databinding.BindingAdapter


@BindingAdapter("imageBitmap")
fun loadImage(iv: ImageView, bitmap: Bitmap?) {
    if (bitmap != null) {
        iv.setImageBitmap(bitmap)
    }
}