package com.powapp.powason.ui.detail

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.powapp.powason.R
import com.powapp.powason.databinding.FragmentDetailBinding
import com.powapp.powason.ui.login.ViewLoginFragmentArgs
import com.powapp.powason.ui.shared.SharedViewModel

class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var sharedViewModel: SharedViewModel

    //Lazy evaluation of args passed in from LandingFragment
    private val args: ViewLoginFragmentArgs by navArgs()
    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //Inflates the fragment with the data passed in from the LandingFragment
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        recyclerView = binding.breachRecyclerView
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        //For navigating backwards up the navigation chain - adds back button
        (activity as AppCompatActivity).supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        }
        //Gives the icon an options menu
        setHasOptionsMenu(true)

        requireActivity().title = "Account breach info"

        //Creates ViewLoginViewModel file as a ViewModel that can be referenced further on
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)

        viewModel.breachInfo.observe(viewLifecycleOwner, Observer {
            val adapter = DetailsListAdapter(requireContext(), it)
            recyclerView.adapter = adapter
        })

        //Uses the same backward navigation for the back button or hand gestures, so that navigation back with
        //the SystemUI is handled the same way
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateBack()
                }
            }
        )

        viewModel.injectBreachInfoById(args.loginId, sharedViewModel.dataRepository)

        sharedViewModel.dataRepository.breachInfo.observe(viewLifecycleOwner, Observer {
            Log.i("Testing", "Hello, world!" + it.breachInfoData[0].Description)
        })

        return binding.root
    }

    //Handles the navigate back button on the view account screen
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> navigateBack()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateBack(): Boolean {
        collapseKeyboard()
        findNavController().navigateUp()
        return true
    }

    private fun collapseKeyboard() {
        //For collapsing the keyboard
        val imm = requireActivity()
            .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun getFullBreachInfo() {
    }


}