package com.powapp.powason.ui.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.powapp.powason.LoginListAdapter
import com.powapp.powason.R
import com.powapp.powason.data.AccountData
import com.powapp.powason.databinding.LandingFragmentBinding
import com.powapp.powason.util.*

class LandingFragment : Fragment(),
    LoginListAdapter.ListItemListener {

    private lateinit var viewModel: LandingViewModel
    private lateinit var binding: LandingFragmentBinding
    private lateinit var adapter: LoginListAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Disables back navigation on main screen
        (activity as AppCompatActivity)
            .supportActionBar?.setDisplayHomeAsUpEnabled(false)

        requireActivity().title = "Accounts"
        Log.i("App init, version: ", APP_VERSION)

        //Enables the options menu for this fragment
        setHasOptionsMenu(true)

        //Inflates the fragment and returns reference to binding variable
        binding = LandingFragmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(LandingViewModel::class.java)

        with(binding.recyclerView) {
            setHasFixedSize(true)
            val divider = DividerItemDecoration(
                context, LinearLayoutManager(context).orientation
            )
            addItemDecoration(divider)
        }

        viewModel.loginData.observe(viewLifecycleOwner, Observer {
            val loginUrls = StringBuilder()
            for (login in it) {
                Log.i(DBG, "${login.target} (${login.target_url})")
                loginUrls.append(login.target_url + "\n")
            }
        })

        viewModel.breachData.observe(viewLifecycleOwner, Observer {
            val breachNames = StringBuilder()

            if (DEV_MODE && it.dataEntity?.username != "") {
                Log.i(HIBP, "Breaches for: " + it.dataEntity?.username)

                //If no breach was found
                if (it.isEmpty()) {
                    Log.i(HIBP, "No breach found!")
                }
            }

            for (breach in it.breach) {
                Log.i(HIBP, breach.Name)
                breachNames.append(breach.Name + "\n")
            }
            Log.i(HIBP, "Breaches: " + it.breach.count())
        })

        viewModel.loginList?.observe(viewLifecycleOwner, Observer {
            Log.i("dataLogging!", it.toString())
            adapter = LoginListAdapter(it, this@LandingFragment)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        })

        //Adds click listener to floating action button for adding new account logins
        binding.floatingActionButton.setOnClickListener {
            onItemClick(NEW_ENTRY_ID)
        }
        return binding.root
    }

    //Inflates the options menu layout
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (DEV_MODE)
            inflater.inflate(R.menu.quick_menu_dev, menu)
        else
            inflater.inflate(R.menu.quick_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //Handles clicks on the options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sample_data -> addSampleData()
            R.id.action_delete_all -> deleteAllListings()
            R.id.action_check_security -> checkAccountSecurity()
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //Development function for adding sample data for testing
    private fun addSampleData(): Boolean {
        viewModel.addSampleData()
        return true
    }

    //Development function for deleting all data in the database
    private fun deleteAllListings(): Boolean {
        //Since this could cause a lot of problems if done accidentally, a confirmation box is added
        val alertDialogBuilder = AlertDialog.Builder(binding.root.context)
        alertDialogBuilder.setMessage("Are you sure you want to delete all accounts? (This cannot be undone)")
        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            viewModel.deleteAllListings()
        }
        alertDialogBuilder.setNegativeButton("No", null)
        alertDialogBuilder.show()
        return true
    }

    private fun checkAccountSecurity(): Boolean {
        viewModel.checkAccountSecurity()
        return true
    }

    //LoginListAdapter interface implementation, handles clicks recycler view clicks
    override fun onItemClick(itemId: Int) {
        Log.i("itemClick", "for id $itemId")

        //Handling ID of item clicked, passing action to nav controller to navigate to the editor
        val navAction = LandingFragmentDirections.actionEditLogin(itemId)
        findNavController().navigate(navAction)
    }

}