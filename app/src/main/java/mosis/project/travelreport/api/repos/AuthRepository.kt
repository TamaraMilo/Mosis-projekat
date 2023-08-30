package mosis.project.travelreport.api.repos

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {

    private val authFirebase =  FirebaseAuth.getInstance()

    fun signIn(callback: (String?)-> Unit) {

    }
}