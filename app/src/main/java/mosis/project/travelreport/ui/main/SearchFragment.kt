package mosis.project.travelreport.ui.main

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import mosis.project.travelreport.R
import mosis.project.travelreport.data.enum.SearchEnum
import mosis.project.travelreport.databinding.FragmentSearchBinding
import mosis.project.travelreport.location.LocationWizard
import mosis.project.travelreport.model.LocationViewModel
import mosis.project.travelreport.ui.main.adapters.LocationAdapter
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment : Fragment() {


    private lateinit var binding: FragmentSearchBinding

    private val locationViewModel: LocationViewModel by activityViewModels()
    private var searchCategory: SearchEnum = SearchEnum.NAME

    private var dateStart: Date = Date()
    private var dateEnd: Date = Date()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSearchBinding.inflate(inflater , container, false)

        binding.lvLocations.setOnItemClickListener { parent, view, position, id ->
            locationViewModel.locationSearch.value?.let {
                var bundle = bundleOf("locationData" to it[position])
                findNavController().navigate(R.id.action_SearchFragment_to_LocationFragment, bundle)
            }
        }

        locationViewModel.locationSearch.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            binding.lvLocations.adapter = LocationAdapter(requireActivity(), ArrayList(it))
        })


        var builder = MaterialDatePicker.Builder.dateRangePicker().build()
        builder.addOnPositiveButtonClickListener {selection->
            var startDate = selection.first
            var endDate = selection.second

            var sdf =SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            dateStart = Date(startDate)
            dateEnd = Date(endDate)
            binding.tvStartDate.text = sdf.format(Date(startDate))
            binding.tvEndDate.text = sdf.format(Date(endDate))


        }


        binding.bNameSearch.setOnClickListener {
            binding.tvSearchBy.text = "Name"
            searchCategory = SearchEnum.NAME
            binding.vf.displayedChild = 0
        }

        binding.bUserSearch.setOnClickListener {
            binding.tvSearchBy.text = "User"
            searchCategory = SearchEnum.USER
            binding.vf.displayedChild = 1
        }

        binding.bDateSearch.setOnClickListener {
            binding.tvSearchBy.text = "Date"
            searchCategory = SearchEnum.DATE
            binding.vf.displayedChild = 4
        }
        binding.bTagSearch.setOnClickListener {
            binding.tvSearchBy.text = "Tag"
            searchCategory = SearchEnum.TAG
            binding.vf.displayedChild = 2
        }
        binding.bLocationSearch.setOnClickListener {
            binding.tvSearchBy.text = "Location"
            searchCategory = SearchEnum.LOCATION
            binding.vf.displayedChild = 3
        }
        binding.bDatePicker.setOnClickListener {
            builder.show(fragmentManager!!, "Date picker")
        }

        binding.bSearch.setOnClickListener {
            when(searchCategory) {
                SearchEnum.NAME-> {
                    locationViewModel.getLocationsByName(binding.etSearchByName.text.toString()) {
                        if(it!=null) {
                            Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                SearchEnum.LOCATION-> {
                    var radius = binding.etSearchByLocation.text.toString().toInt()
                    LocationWizard.instance?.findMostAccurateLocation()?.let {
                        locationViewModel.getLocationsByDistance(it, radius) {
                            if(it!=null) {
                                Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }
                SearchEnum.DATE-> {

                    locationViewModel.getLocationsByDate(dateStart, dateEnd) {
                        if(it!=null) {
                            Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
                        }

                    }
                }
                SearchEnum.USER-> {
                    var author = binding.etSearchByUserName.text.toString()
                    locationViewModel.getLocationsByAuthor(author) {
                        if(it!=null) {
                            Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
                        }

                    }
                }
                SearchEnum.TAG-> {
                    var tag = binding.etSearchByTag.text.toString()
                    locationViewModel.getLocationsByTag(tag) {
                        if(it!=null) {
                            Toast.makeText(this.context, it, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            binding.vf.displayedChild = 5
        }

        binding.bAllLocations.setOnClickListener {
            isSearchActive = true
            locationViewModel.transferToMapFromSearch()
            parentFragmentManager.popBackStack()
        }



        return binding.root
    }

    companion object {
        var isSearchActive = false
    }
}