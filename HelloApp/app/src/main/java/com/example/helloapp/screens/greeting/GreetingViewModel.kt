package com.example.helloapp.screens.greeting

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class GreetingViewModel(private val sharedPreferences: SharedPreferences, val context: Context) :
    ViewModel() {
    private val _userName = MutableLiveData<String?>()
    val userName: LiveData<String?>
        get() = _userName

    private val _userAvatarUri = MutableLiveData<Uri?>()

    val userAvatarUri: LiveData<Uri?>
        get() = _userAvatarUri

    private val USER_NAME_KEY = "username"
    private val USER_AVATAR_PATH_KEY = "avatar_image_path"

    init {
        _userName.value = sharedPreferences.getString(USER_NAME_KEY, null)
        sharedPreferences.getString(USER_AVATAR_PATH_KEY, null)?.let { path ->
            _userAvatarUri.value = Uri.parse(path)
        }
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

    fun clearUserData(bundle: Bundle?) {
        with(sharedPreferences.edit()) {
            putString(USER_NAME_KEY, null)
            putString(USER_AVATAR_PATH_KEY, null)
            commit()
        }
        _userAvatarUri.value = null
        _userName.value = null

        bundle?.let {
            bundle.remove(USER_NAME_KEY)
        }
    }

    fun setUserAvatar(imageUri: Uri) {
        saveAvatarPath(imageUri.toString())
        _userAvatarUri.value = imageUri
    }

    private fun saveAvatarPath(path: String) {
        if (path.isNotEmpty()) {
            with(sharedPreferences.edit()) {
                putString(USER_AVATAR_PATH_KEY, path)
                commit()
            }
        }
    }
}