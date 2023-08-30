package mosis.project.travelreport.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import mosis.project.travelreport.R
import mosis.project.travelreport.data.UserInfoData
import mosis.project.travelreport.databinding.FragmentUserInfoAccountBinding
import mosis.project.travelreport.model.UserViewModel
import mosis.project.travelreport.ui.login.LoginActivity
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent.inject

class UserInfoAccountFragment : Fragment() {


    private lateinit var binding: FragmentUserInfoAccountBinding

    private lateinit var user:UserInfoData
    private val userViewModel:UserViewModel by inject()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUserInfoAccountBinding.inflate(inflater, container, false)

         userViewModel.getUserFromDatabase() {
            user = it!!
             user?.let {itUser->
                 this.context?.let {
                         it1 -> Glide
                                .with(it1)
                                .load(itUser.imageID)
                                .into(binding.ivUserAccountImage)
                 }
                 binding.tvUserNameInfo.text = it.fullName
                 binding.tvPhoneNumberInfo.text = itUser.phoneNumber
                 binding.tvScoreInfo.text = itUser.score.toString()
                 binding.tvNumOfPostsInfo.text = itUser.numberOfPosts.toString()
                 binding.tvDateInfo.text = itUser.date
                 userViewModel.getUserEmail { itEmail->
                     itEmail?.let { email->
                         binding.tvUserNameAccInfo.text= email
                     }
                 }
             }
        }

        binding.bLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            var intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)

        }

        return binding.root
    }



}