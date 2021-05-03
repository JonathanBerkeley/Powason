package com.powapp.powason.ui.detail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.powapp.powason.R
import com.powapp.powason.data.BreachInfo
import com.powapp.powason.databinding.BreachItemBinding
import com.powapp.powason.databinding.ListItemBinding

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
        val view = inflater.inflate(R.layout.list_item, parent, false)

        //Initialize and return an instance of the ViewHolder class
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val breach = breachList[position]
        with(holder.binding) {
            breachInfoText.text = breach.Description
        }
    }

    override fun getItemCount() = breachList.size
}