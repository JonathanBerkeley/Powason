package com.powapp.powa

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.powapp.powa.data.DataEntity
import com.powapp.powa.databinding.ListItemBinding
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
            loginTarget.text = login.target_name

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
        //Google's icon getting API
        val baseFaviconUrl: String = "https://www.google.com/s2/favicons?sz=128&domain_url="
        with (itemBinding) {
            //Get live favicons from site using Google's favicon generating API
            try {
                //Alternative loading method:
                //loginFavicon.setImageResource(R.drawable.loading)
                Glide.with(loginFavicon.context)
                    .load(baseFaviconUrl + loginTarget.text)
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
}