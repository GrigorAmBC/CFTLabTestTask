package com.example.helloapp.screens.greeting

import android.content.ContentResolver
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class GreetingViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?>
        get() = _userName

    private val _userAvatar = MutableLiveData<Bitmap>()

    val userAvatar: LiveData<Bitmap>
        get() = _userAvatar

    private val USER_NAME_KEY = "username"

    init {
        _userName.value = sharedPreferences.getString(USER_NAME_KEY, null)
    }

    fun setUserNameFromBundle(bundle: Bundle?) {
        bundle?.let {
            val userNameStr = bundle.getString(USER_NAME_KEY)
            if (userNameStr != null && userNameStr.isNotEmpty()) {
                _userName.value = userNameStr
                sharedPreferences
                with(sharedPreferences.edit()) {
                    putString(USER_NAME_KEY, userNameStr)
                    commit()
                }
            }
        }
    }

    fun clearUsernameData(bundle: Bundle?) {
        with(sharedPreferences.edit()) {
            putString(USER_NAME_KEY, null)
            commit()
        }
        _userName.value = null

        bundle?.let {
            bundle.remove(USER_NAME_KEY)
        }
    }

    fun setUserAvatar(imageUri: Uri, contentResolver: ContentResolver) {
        val source: ImageDecoder.Source =
            ImageDecoder.createSource(contentResolver, imageUri)
        _userAvatar.value = ImageDecoder.decodeBitmap(source)
    }

    fun setUserAvatar(bitmap: Bitmap) {
        _userAvatar.value = bitmap
    }

}