package mosis.project.travelreport.api.repos

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import mosis.project.travelreport.data.CommentData
import mosis.project.travelreport.data.LocationData

class CommentRepository {

    private val databaseReference = FirebaseDatabase.getInstance().reference


    fun getAllCommentForLocation(locationId:String, callback:(List<CommentData>?)->Unit) {
//        
        databaseReference.child("comments").child(locationId).get()
            .addOnSuccessListener {
                val comments = ArrayList<CommentData>()
                for(snapshot in it.children) {
                    snapshot.getValue(CommentData::class.java)?.let {itComment->
                        comments.add(itComment)
                    }
                }
                callback.invoke(comments)
            }
            .addOnFailureListener {
                callback.invoke(null)
            }
    }

    fun addComment(comment:CommentData, callback: (String?) -> Unit) {
        databaseReference.child("comments").child(comment.locationId).child(comment.commentId).setValue(comment)
            .addOnSuccessListener {
                callback.invoke(null)
            }
            .addOnFailureListener {4
                callback.invoke(it.message)
            }
    }
}