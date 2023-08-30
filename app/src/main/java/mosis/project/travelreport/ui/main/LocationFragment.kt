package mosis.project.travelreport.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import mosis.project.travelreport.R
import mosis.project.travelreport.data.CommentData
import mosis.project.travelreport.data.LocationData
import mosis.project.travelreport.data.UserInfoData
import mosis.project.travelreport.databinding.FragmentLocationBinding
import mosis.project.travelreport.model.CommentViewModel
import mosis.project.travelreport.model.UserViewModel
import mosis.project.travelreport.ui.main.adapters.CommentAdapter
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class LocationFragment : Fragment() {

    private lateinit var binding: FragmentLocationBinding

    private var locationData: LocationData? = null
    private var user: UserInfoData? = null

    private val userViewModel: UserViewModel by inject()
    private val commentsViewModel: CommentViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationData = arguments?.getParcelable<LocationData>("locationData")
        locationData?.let {
            userViewModel.getUserById(it.userId) { itUser ->
                user = itUser
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLocationBinding.inflate(inflater, container, false)

        binding.rb.numStars = 5

        locationData?.let { itLocation ->
            locationData = itLocation
            userViewModel.getUserById(itLocation.userId) { itUser ->
                itUser?.let { itUser1 ->
                    this.context?.let { it1 ->
                        Glide.with(it1)
                            .load(itLocation.image)
                            .fitCenter()
                            .into(binding.ivLocation)
                    }

                    binding.ivLocation.background = null
                    binding.tvDescriptionDisplay.text = itLocation.description
                    binding.tvNameDisplay.text = itLocation.name
                    binding.tvRateDisplay.text = itLocation.rate.toString()
                    var formatter = SimpleDateFormat("dd/MM/yyyy" , Locale.getDefault())

                    binding.tvDateDisplay.text = formatter.format(Date(itLocation.date))
                    binding.tvTagDisplay.text = itLocation.tag
                    binding.tvLatDisplay.text = itLocation.lat.toString()
                    binding.tvLogDisplay.text = itLocation.lon.toString()
                    binding.tvUserNameDisplay.text = itUser1.email

                    commentsViewModel.getAllCommentForLocation(itLocation.locationId)

                }
            }
        }

        binding.bCommit.setOnClickListener {
            userViewModel.getUserFromDatabase { itCurrentUser ->
                itCurrentUser?.let {
                    val locationId = locationData?.locationId
                    val commentId = UUID.randomUUID().toString()
                    val comment = binding.etCommentSection.text.toString()
                    val rate = binding.rb.rating.toInt()

                    userViewModel.getUserEmail {itEmail->
                        itEmail?.let {email->
                            val commentData =
                                locationId?.let { it3 ->
                                    CommentData(
                                        it3,
                                        commentId,
                                        rate.toDouble(),
                                        comment,
                                        email
                                    )
                                }
                            commentData?.let { it1 ->
                                commentsViewModel.addComment(it1) { error ->
                                    if (error != null) {
                                        Toast.makeText(this.context, error, Toast.LENGTH_SHORT).show()
                                    } else {
                                        userViewModel.updateUserScore(15) { errorUser ->
                                            if (errorUser != null) {
                                                Toast.makeText(this.context, errorUser, Toast.LENGTH_SHORT)
                                                    .show()
                                            } else {
                                                Toast.makeText(
                                                    this.context,
                                                    "You got 15 points for commenting!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        commentsViewModel.comments.observe(viewLifecycleOwner) {
            binding.lvComments.adapter = CommentAdapter(requireActivity(), ArrayList(it))
        }

        return binding.root
    }

}