package mosis.project.travelreport.ui.main.adapters

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import mosis.project.travelreport.R
import mosis.project.travelreport.api.repos.UserRepository
import mosis.project.travelreport.data.UserInfoData
import mosis.project.travelreport.model.UserViewModel
import org.koin.android.ext.android.inject

class UserRatingAdapter(private val context: Activity, private var userList:ArrayList<UserInfoData>)
    :ArrayAdapter<UserInfoData>(context, R.layout.user_list_item, userList) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater:LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.user_list_item, null)

        val userImage:ImageView = view.findViewById(R.id.ivUserImage)
        val userName:TextView = view.findViewById(R.id.tvUserName)
        val userScore: TextView = view.findViewById(R.id.tvUserScore)

        userName.text = userList[position].fullName
        userScore.text = userList[position].score.toString()
        userImage.setImageURI(userList[position].imageID.toUri())
        Glide.with(context)
            .load(userList[position].imageID)
            .fitCenter()
            .into(userImage)

        return  view
    }


}