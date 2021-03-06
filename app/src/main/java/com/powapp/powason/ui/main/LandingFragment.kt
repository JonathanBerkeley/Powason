package com.powapp.powason.ui.main

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.powapp.powason.R
import com.powapp.powason.databinding.LandingFragmentBinding
import com.powapp.powason.ui.detail.DetailsListAdapter
import com.powapp.powason.ui.shared.SharedViewModel
import com.powapp.powason.util.*
import java.util.*

class LandingFragment : Fragment(),
    LoginListAdapter.ListItemListener {

    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: LandingFragmentBinding

    private lateinit var swipeLayout: SwipeRefreshLayout
    private lateinit var navController: NavController

    private var loginListAdapter: LoginListAdapter? = null
    private var detailListAdapter: DetailsListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Disables back navigation on main screen
        (activity as AppCompatActivity)
            .supportActionBar?.setDisplayHomeAsUpEnabled(false)

        requireActivity().title = "Accounts"

        //Enables the options menu for this fragment
        setHasOptionsMenu(true)

        //Inflates the fragment and returns reference to binding variable
        binding = LandingFragmentBinding.inflate(inflater, container, false)

        //Set colour
        binding.root.rootView.setBackgroundColor(Color.WHITE)

        //Hide refresh animation
        binding.loadingSecurity.visibility = View.GONE
        navController = Navigation.findNavController(
            requireActivity(), R.id.nav_host
        )
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        swipeLayout = binding.swipeLayout
        swipeLayout.setOnRefreshListener {
            checkEmailSecurity()
        }

        with(binding.recyclerView) {
            setHasFixedSize(true)
            val divider = DividerItemDecoration(
                context, LinearLayoutManager(context).orientation
            )
            addItemDecoration(divider)
        }

        //Alerts the user that their password has been seen cracked somewhere
        //This is very severe so its an alert
        viewModel.crackedPWInfo.observe(viewLifecycleOwner, Observer {
            if (requestComment) {
                binding.loadingSecurity.visibility = View.VISIBLE //Show loading wheel
                val checkPresent = it.getCrackedCount()
                if (checkPresent != "0") {
                    val alertDialogBuilder = AlertDialog.Builder(binding.root.context)
                    alertDialogBuilder.setMessage(
                        "Password for account: \"${it.dataEntity?.title}\" has been found " +
                                "${checkPresent}times in leaks. " +
                                "It is unsafe and you should change it anywhere that it has been used immediately!"
                    )
                    alertDialogBuilder.setTitle("SEVERE - PASSWORD LEAK")
                    alertDialogBuilder.setPositiveButton("Ok", null)
                    alertDialogBuilder.show()
                }
            }
            ++commentCounter
            if (commentCounter == dbCount || commentCounter + 1 == dbCount) {
                //Reset flag variables
                requestComment = false
                commentCounter = 0
                binding.loadingSecurity.visibility = View.GONE //Hide progress wheel
                dbCount = null
            }
        })

        viewModel.loginData.observe(viewLifecycleOwner, Observer {
            if (insertBool) {
                for (login in it) {
                    //Add to database!
                    viewModel.insertAccount(login)
                }
                insertBool = false
            }

            loginListAdapter = LoginListAdapter(it, this@LandingFragment)

            binding.recyclerView.adapter = loginListAdapter
            binding.recyclerView.layoutManager = LinearLayoutManager(activity)

            swipeLayout.isRefreshing = false
        })

        viewModel.breachName.observe(viewLifecycleOwner, Observer {
            if (DEV_MODE && it.dataEntity?.username != "") {
                Log.i(HIBP, "Breaches for: " + it.dataEntity?.username)

                //If no breach was found
                if (it.isEmpty()) {
                    Log.i(HIBP, "No breach found!")
                }
            }
            if (DEV_MODE) {
                for (breach in it.breach) {
                    Log.i(HIBP, breach.Name)
                }
                Log.i(HIBP, "Breaches: " + it.breach.count())
            }

            viewModel.modifyBreachCount(it.dataEntity?.id!!, it.breach.count())
        })

        viewModel.loginList?.observe(viewLifecycleOwner, Observer {
            loginListAdapter = LoginListAdapter(it, this@LandingFragment)

            binding.recyclerView.adapter = loginListAdapter
            binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        })

        //Adds click listener to floating action button for adding new account logins
        binding.floatingActionButton.setOnClickListener {
            onItemClick(NEW_ENTRY_ID)
        }

        if (QUERY_ALL_ON_FOCUS)
            checkEmailSecurity() // Can easily induce 429 response if unrestricted
        else if (QUERY_ON_FOCUS && loginListAdapter != null)
            checkEmailSecurity(loginListAdapter!!.itemCount + 1) // Can induce 429 response if unrestricted

        return binding.root
    }

    //Inflates the options menu layout
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.quick_menu_dev, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //Handles clicks on the options menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sample_data -> addSampleData()
            R.id.action_sync_data -> syncDataWithWs()
            R.id.action_delete_all -> deleteAllListings()
            R.id.action_check_emails -> checkEmailSecurity()
            R.id.action_check_passwords -> checkPasswordSecurity()
            R.id.action_check_security -> checkAccountSecurity()
            else -> return super.onOptionsItemSelected(item)
        }
    }

    //Development function for adding sample data for testing
    private fun addSampleData(): Boolean {
        viewModel.addSampleData()
        Thread.sleep(100) //Tiny delay to prevent race condition
        checkEmailSecurity()
        return true
    }

    //Syncs data with webservice (Local API)
    private fun syncDataWithWs(): Boolean {
        //Since this could cause a lot of problems if done accidentally, a confirmation box is added
        val alertDialogBuilder = AlertDialog.Builder(binding.root.context)
        alertDialogBuilder.setMessage("Are you sure you want to sync?")
        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            viewModel.syncDataWithWS()
        }
        alertDialogBuilder.setNegativeButton("No", null)
        alertDialogBuilder.show()

        Thread.sleep(300) //Tiny delay to prevent race condition
        checkEmailSecurity()
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

    private fun checkEmailSecurity(): Boolean {
        viewModel.checkEmailSecurity()
        getDBEntryCount()
        swipeLayout.isRefreshing = false
        return true
    }

    private fun checkEmailSecurity(accId: Int): Boolean {
        viewModel.checkEmailSecurity(accId)
        getDBEntryCount()
        swipeLayout.isRefreshing = false
        return true
    }

    private fun checkPasswordSecurity(): Boolean {
        requestComment = true //Tells the fragment it's allowed to spawn alerts
        getDBEntryCount()
        viewModel.checkPasswordSecurity()
        return true
    }

    private fun checkPasswordSecurity(accId: Int): Boolean {
        requestComment = true
        getDBEntryCount()
        viewModel.checkPasswordSecurity()
        return true
    }

    private fun checkAccountSecurity(): Boolean {
        requestComment = true
        getDBEntryCount()
        viewModel.checkAccountSecurity()
        swipeLayout.isRefreshing = false
        return true
    }

    private fun checkAccountSecurity(accId: Int): Boolean {
        requestComment = true
        getDBEntryCount()
        viewModel.checkAccountSecurity(accId)
        swipeLayout.isRefreshing = false
        return true
    }

    private fun getDBEntryCount() {
        viewModel.getEntryCount()
    }

    //LoginListAdapter interface implementation, handles clicks recycler view clicks
    override fun onItemClick(itemId: Int) {
        Log.i("itemClick", "for id $itemId")

        //Handling ID of item clicked, passing action to nav controller to navigate to the editor
        val navAction = LandingFragmentDirections.actionEditLogin(itemId)
        navController.navigate(navAction)
    }

    override fun onButtonPress(itemId: Int) {
        val navAction = LandingFragmentDirections.actionNavDetail(itemId)
        navController.navigate(navAction)
    }
}