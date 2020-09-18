package com.example.helloapp.screens.credentials

import androidx.lifecycle.ViewModel
import java.text.ParseException
import java.text.SimpleDateFormat

class UserCredentialsViewModel : ViewModel() {
    fun checkBirthDate(date: String): String? {
        val sdfrmt = SimpleDateFormat("MM/dd/yyyy")
      sdfrmt.isLenient = false

        return try {
            sdfrmt.parse(date)
            null
        } catch (e: ParseException) {
            ("Invalid date format. Should be MM/dd/yyyy.")
        }
    }

    fun checkPasswords(passwordOne: String, passwordTwo: String): String? {
        if (passwordOne.isEmpty()) {
            return "Password must not be empty"
        }
        if (!passwordOne.equals(passwordTwo)) {
            return "Passwords don't match"
        }
        if (passwordOne.length < 6) {
            return "Use at least 6 characters for password"
        }

        return null
    }

    fun checkUserName(name: String): String? {
        if (name.length < 2) {
            return "Name is too short"
        }
        return null
    }

    fun checkUserSurname(surname: String): String? {
        if (surname.length < 2) {
            return "Surname is too short"
        }
        return null
    }
}