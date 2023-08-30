package mosis.project.travelreport.model

import android.net.Uri
import android.os.Debug
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import mosis.project.travelreport.api.repos.UserRepository
import mosis.project.travelreport.data.UserInfoData
import java.net.URI

class UserViewModel : ViewModel() {


    private val userRepository = UserRepository()

    val users : MutableLiveData<ArrayList<UserInfoData>> = MutableLiveData()


    // updates user info when user uploads photo
    // adds 10 points to user and
     fun updateUserPost(callback: (String?) -> Unit) {
        userRepository.updateUserInfoPosts(10) {
            callback.invoke(it)
        }
    }

    // updates user score when he adds comment
    fun updateUserScore(score: Int, callback: (String?) -> Unit){
        userRepository.updateUserInfoScore(score) {
            callback.invoke(it)
        }
    }

    fun getUserFromDatabase(callback: (UserInfoData?) -> Unit) {
        userRepository.getCurrentUserInfo {
            callback.invoke(it)
            }
        }


    fun getUsersSorted(callback: (Unit?) -> Unit) {
        userRepository.getUsers {
            it?.let{ itUsers->
                users.value = ArrayList(itUsers.sortedDescending())
                callback.invoke(null)
            }
        }
    }

    fun addUserInfoData(userInfo: UserInfoData, uri: Uri, callback: (String?) -> Unit) {
        userRepository.addUserInfo(userInfo, uri) {
            callback.invoke(it)
        }
    }

    fun getUserId(callback: (String?) -> Unit) {
        userRepository.getUserId {
            callback.invoke(it)
        }
    }


    fun getUserById(userId: String, callback: (UserInfoData?)->Unit) {
        userRepository.getUser(userId) {
            callback.invoke(it)
        }
    }

    fun getUserEmail(callback: (String?) -> Unit) {
        userRepository.getUserEmail{
            callback.invoke(it)
        }
    }

}