package com.example.helloapp.screens.greeting

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GreetingViewModelFactory(private val sharedPreferences: SharedPreferences): ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(GreetingViewModel::class.java)) {
      return GreetingViewModel(sharedPreferences) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")

  }
}