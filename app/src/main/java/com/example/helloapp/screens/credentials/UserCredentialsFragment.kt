package com.example.helloapp.screens.credentials

import android.os.Bundle
import android.text.InputFilter
import android.view.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.helloapp.R
import com.example.helloapp.databinding.FragmentUserCredentialsBinding
import com.google.android.material.snackbar.Snackbar

class UserCredentialsFragment : Fragment() {
  private val USERNAME_KEY = "username"
  private lateinit var credentialsViewModel: UserCredentialsViewModel
  private lateinit var binding: FragmentUserCredentialsBinding

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    binding = DataBindingUtil.inflate(
      inflater,
      R.layout.fragment_user_credentials,
      container,
      false
    )
    binding.lifecycleOwner = this

    credentialsViewModel = ViewModelProvider(this)
      .get(UserCredentialsViewModel::class.java)

    binding.credentialsViewModel = credentialsViewModel

    binding.signUpButton.setOnClickListener {
      onSignup()
    }

    return binding.root
  }

  fun onSignup() {
    if (checkCredentials()) {
      navigateBackToGreeting(binding.userNameEditText.text.toString())
    }

  }

  private fun navigateBackToGreeting(username: String) {
    val bundle = bundleOf(USERNAME_KEY to username)
    findNavController().navigate(R.id.action_userCredentialsFragment_to_greetingFragment, bundle)
  }

  private fun checkCredentials(): Boolean { // true if credentials are ok
    val username = binding.userNameEditText.text.toString()
    val userSurname = binding.userSurnameEditText.text.toString()
    val birthDate = binding.birthDateEditText.text.toString()
    val password1 = binding.passwordEditText1.text.toString()
    val password2 = binding.passwordEditText2.text.toString()

    with(credentialsViewModel) {
      if (showMessageViaToast(checkBirthDate(birthDate)))
        return false
      if (showMessageViaToast(checkPasswords(password1, password2)))
        return false
      if (showMessageViaToast(checkUserName(username)))
        return false
      if (showMessageViaToast(checkUserSurname(userSurname)))
        return false
    }

    return true
  }

  private fun showMessageViaToast(message: String?): Boolean { //true if it has shown a toast message
    message?.let {
      Snackbar.make(this.requireView(), message, Snackbar.LENGTH_SHORT)
        .show()
      return true
    }

    return false
  }
}