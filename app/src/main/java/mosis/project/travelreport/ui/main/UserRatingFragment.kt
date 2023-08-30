package mosis.project.travelreport.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mosis.project.travelreport.R
import mosis.project.travelreport.data.UserInfoData
import mosis.project.travelreport.databinding.FragmentUserRatingBinding
import mosis.project.travelreport.model.UserViewModel
import mosis.project.travelreport.ui.main.adapters.UserRatingAdapter
import org.koin.android.ext.android.inject


class UserRatingFragment : Fragment() {

    private lateinit var binding:FragmentUserRatingBinding

    private var users:ArrayList<UserInfoData>? = null

    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userViewModel.getUsersSorted() {}

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentUserRatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lvUserRating.adapter = users?.let { UserRatingAdapter(requireActivity(), it) }
        userViewModel.users.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            Log.d("TAG_USERS", it.size.toString())
            val adapter = UserRatingAdapter(requireActivity(), it)
            binding.lvUserRating.adapter = adapter
        })
    }


}