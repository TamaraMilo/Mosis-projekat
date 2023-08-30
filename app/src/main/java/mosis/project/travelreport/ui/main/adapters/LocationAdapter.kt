package mosis.project.travelreport.ui.main.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import mosis.project.travelreport.R
import mosis.project.travelreport.data.LocationData

class LocationAdapter(private val context:Activity, private var locationList:ArrayList<LocationData>)
    :ArrayAdapter<LocationData>(context, R.layout.user_list_item, locationList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            var inflater: LayoutInflater = LayoutInflater.from(context)
            val view: View = inflater.inflate(R.layout.location_list_item, null)

            val locationImage: ImageView = view.findViewById(R.id.ivLocationListAdapter)
            val locationName: TextView = view.findViewById(R.id.tvLocationNameListAdapter)

            Glide.with(context)
                .load(locationList[position].image)
                .fitCenter()
                .into(locationImage)
            locationName.text = locationList[position].name
            return view
        }

        fun setData(data: ArrayList<LocationData>) {
            locationList = data
            notifyDataSetChanged()
        }
    }