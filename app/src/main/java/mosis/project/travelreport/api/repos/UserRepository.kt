package mosis.project.travelreport.api.repos

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import mosis.project.travelreport.data.UserInfoData

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference
    private val databaseReference = FirebaseDatabase.getInstance().reference

    fun getCurrentUserInfo(callback: (UserInfoData?) -> Unit) {
        auth.currentUser?.uid?.let {
            databaseReference.child("users").child(it).get()
                .addOnSuccessListener { itUserSnapshot ->
                    callback.invoke(itUserSnapshot.getValue(UserInfoData::class.java))

                }
                .addOnFailureListener {
                    callback.invoke(null)
                }

        }
    }

    fun getUsers(callback: (List<UserInfoData>?) -> Unit) {
        databaseReference.child("users").get()
            .addOnSuccessListener {
                val users = ArrayList<UserInfoData>()
                for (snapshot in it.children) {
                    snapshot.getValue(UserInfoData::class.java)?.let { itUserInfo ->
                        users.add(itUserInfo)
                    }
                }
                callback.invoke(users)
            }
            .addOnFailureListener {
                callback.invoke(null)
            }
    }

    fun updateUserInfoPosts(value: Int, callback: (String?) -> Unit) {
        getCurrentUserInfo {
            it?.let { userInfo ->
                userInfo.numberOfPosts = userInfo.numberOfPosts.plus(1)
                userInfo.score = userInfo.score.plus(value)
                databaseReference.child("users").child(userInfo.userID)
                    .updateChildren(userInfo.toMap())
                    .addOnSuccessListener {
                        callback.invoke(null)
                    }
                    .addOnFailureListener { exc ->
                        callback.invoke(exc.message)
                    }

            }
        }
    }

    fun updateUserInfoScore(value: Int, callback: (String?) -> Unit) {
        getCurrentUserInfo {
            it?.let { userInfo ->
                userInfo.score = userInfo.score.plus(value)
                databaseReference.child("users").child(userInfo.userID)
                    .updateChildren(userInfo.toMap())
                    .addOnSuccessListener {
                        callback.invoke(null)
                    }
                    .addOnFailureListener { exc ->
                        callback.invoke(exc.message)
                    }
            }
        }
    }


    fun addUserInfo(userInfo: UserInfoData, userImage: Uri, callback: (String?) -> Unit) {
        storageReference.child("userImages").child(userInfo.userID).putFile(userImage)
            .addOnSuccessListener {
                storageReference.child("userImages")
                    .child(userInfo.userID).downloadUrl.addOnSuccessListener { uri ->
                    userInfo.imageID = uri.toString()
                    getUserEmail { itEmail ->
                        itEmail?.let { email ->
                            userInfo.email = email
                            databaseReference.child("users").child(userInfo.userID)
                                .setValue(userInfo)
                                .addOnSuccessListener {
                                    callback.invoke(null)
                                }
                                .addOnFailureListener {
                                    callback.invoke(it.message)
                                }
                        }

                    }
                }
            }
    }

    fun getUserId(callback: (String?) -> Unit) {
        var user: String = ""
        auth.currentUser?.let {
            user = it.uid
        }
        if (user.equals("")) {
            callback.invoke(null)
        } else {
            callback.invoke(user)
        }
    }

    fun getUser(userId: String, callback: (UserInfoData?) -> Unit) {
        databaseReference.child("users").child(userId).get()
            .addOnSuccessListener {
                val userInfo = it.getValue(UserInfoData::class.java)
                callback.invoke(userInfo)
            }
            .addOnFailureListener {
                callback.invoke(null)
            }
    }

    fun getUserEmail(callback: (String?) -> Unit) {
        var userEmail = ""
        auth.currentUser?.let {
            userEmail = it.email.toString()
        }
        if (userEmail.equals("")) {
            callback.invoke(null)
        } else {
            callback.invoke(userEmail)
        }
    }


}