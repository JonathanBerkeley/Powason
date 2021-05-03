package com.powapp.powason

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.powapp.powason.data.DataEntity
import com.powapp.powason.databinding.ListItemBinding
import com.powapp.powason.util.FAVICON_API
import java.lang.Exception

class LoginListAdapter(
    private val loginList: List<DataEntity>,
    private val listener: ListItemListener
) :
    //Calls constructor for inner class
    RecyclerView.Adapter<LoginListAdapter.ViewHolder>() {

    //Gets a reference to the root view of the list_view.xml file
    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val binding = ListItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        //Gets reference to the root of the layout file
        val view = inflater.inflate(R.layout.list_item, parent, false)

        //Initialize and return an instance of the ViewHolder class
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val login = loginList[position]
        with(holder.binding) {
            //Set the data for the list_item layout
            loginTitle.text = login.title
            


            //Shortens display of title if too long
            if (login.title.length > 22) {
                loginTitle.text = (login.title.substring(startIndex = 0, endIndex = 21) + "..")
            }
            loginTarget.text = login.target_name

            //Get a reference to the button
            val breachViewerBtn: Button = breachCountBtn

            //Build text for button with warning emoji
            if (login.breachCount!! > 0) {
                breachViewerBtn.visibility = View.VISIBLE
                breachCountBtn.text = ((
                        if (login.breachCount!! > 99) "99+"
                        else login.breachCount.toString()
                        ) + " ⚠️")
            }
            else run {
                breachViewerBtn.visibility = View.GONE
            }

            generateFavicon(this)

            //For listening for user clicks on the data in recycler view
            root.setOnClickListener {
                listener.onItemClick(login.id)
            }
        }
    }


    interface ListItemListener {
        //Implemented by the LandingFragment
        fun onItemClick(itemId: Int)
    }

    override fun getItemCount() = loginList.size

    //Function for generating the favicons for the account logins
    private fun generateFavicon(itemBinding: ListItemBinding) {
        //Using Google's icon getting API
        with (itemBinding) {
            //Get live favicons from site using Google's favicon generating API
            try {
                //Alternative loading method:
                //loginFavicon.setImageResource(R.drawable.loading)
                Glide.with(loginFavicon.context)
                    .load(FAVICON_API + loginTarget.text)
                    //Sets the icon to loading animation while
                    //waiting for the app to load the favicon or error out
                    .thumbnail(Glide.with(loginFavicon.context).load(R.drawable.loading))
                    .apply(
                        RequestOptions().override(150, 150)
                            .error(R.drawable.unfound)
                    )
                    .into(loginFavicon)
            } catch (ex: Exception) {
                Log.e("Glide exception", "$ex")
            }
        }
    }

    private fun securityCheck(itemBinding: ListItemBinding) {
        with (itemBinding) {
            try {
            } catch (ex: Exception) {
                Log.e("Secucheck exception", "$ex")
            }
        }
    }
}