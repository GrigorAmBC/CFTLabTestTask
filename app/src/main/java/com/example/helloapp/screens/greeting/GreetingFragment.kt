package com.example.helloapp.screens.greeting

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
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

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.greeting_menu, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.log_out) {
      greetingViewModel.clearUsernameData(arguments)
      return true
    }

    return false
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
    binding.helloButton.setOnClickListener {
      onHello(it)
    }

    val sharedPref = this.requireActivity().getPreferences(Context.MODE_PRIVATE)
    greetingViewModelFactory = GreetingViewModelFactory(sharedPref)
    greetingViewModel = ViewModelProvider(this, greetingViewModelFactory)
      .get(GreetingViewModel::class.java)

    greetingViewModel.setUserNameFromBundle(arguments)

    // u can add observables here
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