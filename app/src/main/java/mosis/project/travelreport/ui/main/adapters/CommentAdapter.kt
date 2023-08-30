package mosis.project.travelreport.ui.main.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import mosis.project.travelreport.R
import mosis.project.travelreport.data.CommentData
import mosis.project.travelreport.data.LocationData

class CommentAdapter(
    private val context: Activity,
    private var commentList: ArrayList<CommentData>
) : ArrayAdapter<CommentData>(context, R.layout.comment_item, commentList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.comment_item, null)

        val userName: TextView = view.findViewById(R.id.tvUserNameComment)
        val commentText: TextView = view.findViewById(R.id.tvCommentText)
        val commentRating: TextView = view.findViewById(R.id.tvNumOfStars)

        commentText.text = commentList[position].comment
        commentRating.text = "Rating" + commentList[position].rate
        userName.text = "@" + commentList[position].userEmail

        return view

    }
}