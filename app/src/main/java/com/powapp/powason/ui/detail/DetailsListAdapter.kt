package com.powapp.powason.ui.detail

import android.content.Context
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.powapp.powason.R
import com.powapp.powason.data.BreachInfo
import com.powapp.powason.databinding.BreachItemBinding
import com.powapp.powason.util.FAVICON_API

class DetailsListAdapter(
    val context: Context,
    private val breachList: List<BreachInfo>
) :
    //Calls constructor for inner class
    RecyclerView.Adapter<DetailsListAdapter.ViewHolder>() {

    //Gets a reference to the root view of the breach_item.xml file
    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
            val binding = BreachItemBinding.bind(itemView)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        //Gets reference to the root of the layout file
        val view = inflater.inflate(R.layout.breach_item, parent, false)

        //Initialize and return an instance of the ViewHolder class
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val breach = breachList[position]

        Glide.with(context)
            .load(breach.LogoPath)
            .into(holder.binding.breachCompanyImage)
        with(holder.binding) {

            breachInfoText.text = (
                    Html.fromHtml(
                        breach.Title
                        + (if (breach.IsVerified) " " else " (unverified) ")
                        + breach.Description
                        , Html.FROM_HTML_MODE_COMPACT)
                    )
        }
    }

    override fun getItemCount() = breachList.size
}