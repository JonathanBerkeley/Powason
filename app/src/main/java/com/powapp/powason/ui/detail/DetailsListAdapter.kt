package com.powapp.powason.ui.detail

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
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
        val noLogo = "https://haveibeenpwned.com/Content/Images/PwnedLogos/List.png"


        //Unpack compromised data information from list
        var compromisedData: String = ""
        for (data in breach.DataClasses) {
            compromisedData += "$data, "
        }
        if (compromisedData.length > 2)
            compromisedData = compromisedData.substring(0, compromisedData.length - 2)

        //Format date to be neater and easier to read
        var formattedBreachDate = breach.BreachDate.toString()
        val dateLength = formattedBreachDate.length
        formattedBreachDate = (formattedBreachDate.substring(0, 10)
                + " - " + formattedBreachDate.substring(dateLength - 4, dateLength))

        with(holder.binding) {
            root.setBackgroundColor(Color.rgb(250, 250, 250))
            //Image from API
            Glide.with(context)
                .load(if (breach.LogoPath == noLogo) R.drawable.unfound else breach.LogoPath)
                .override(150, 150)
                .dontAnimate()
                .error(R.drawable.unfound)
                .into(breachCompanyImage)


            //Makes links clickable
            breachInfoText.movementMethod = LinkMovementMethod.getInstance()
            breachMetaInfo.movementMethod = LinkMovementMethod.getInstance()

            breachInfoText.text = (
                    Html.fromHtml(
                        "<b>" + breach.Title + ":</b>"
                        + (if (breach.IsVerified) " " else " <i>(unverified)</i> ")
                        + breach.Description
                        + "<br><br><b>Compromised data: </b>"
                        + compromisedData
                        , Html.FROM_HTML_MODE_COMPACT)
                    )

            if (breach.Domain != "") {
                breachMetaInfo.text = (
                    Html.fromHtml(
                        "$formattedBreachDate <br>"
                            + "<a href=\"https://"
                            + breach.Domain
                            + "\" target=\"_blank\" rel=\"noopener\">"
                            + breach.Domain
                            + "</a>"
                            , Html.FROM_HTML_MODE_COMPACT
                        )
                )
            }
            else {
                breachMetaInfo.text = (
                        Html.fromHtml(
                            "$formattedBreachDate <br>"
                            , Html.FROM_HTML_MODE_COMPACT
                        )
                )
            }
        }
    }

    override fun getItemCount() = breachList.size
}