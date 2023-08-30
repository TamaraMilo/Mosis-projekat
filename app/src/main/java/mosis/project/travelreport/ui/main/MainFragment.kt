package mosis.project.travelreport.ui.main

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import mosis.project.travelreport.databinding.FragmentMainBinding
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import mosis.project.travelreport.R
import mosis.project.travelreport.data.LocationData
import mosis.project.travelreport.model.LocationViewModel
import org.koin.android.ext.android.inject

import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MainFragment : Fragment() {

    // Binding
    private lateinit var binding:FragmentMainBinding
    // Map
    private lateinit var map: MapView

    companion object {
        var lvmInstance: LocationViewModel? = null
    }

    //ViewModel
    private val locationViewModel: LocationViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var ctx: Context? = activity?.applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx!!))
        map = requireView().findViewById<MapView>(R.id.map)
        map.setMultiTouchControls(true)
        map.controller.setZoom(15.0)
        val startPoint = GeoPoint(43.3209, 21.8958)
        map.controller.setCenter(startPoint)

        if(ActivityCompat.checkSelfPermission(this.requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this.requireContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            requestPermissionLauncher.launch(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            setMyLocationOverlay()
        }

        lvmInstance = locationViewModel

        if (!SearchFragment.isSearchActive) {
            lvmInstance?.let{ it.getAllPins {} }
        }

        locationViewModel.locations.observe (viewLifecycleOwner) {
            if (it == null) return@observe
            removeAllPinsFromMap()
            addAllMarkers(it)
        }

        binding.floatingAddLocationButton.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_AddLocationFragment)
        }
        binding.floatingRankedButton.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_UserRatingFragment)
        }
        binding.floatingAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_AccountFragment)
        }
        binding.floatingSearchButton.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_SearchFragment)
        }
        binding.floatingNearestLocationButton.setOnClickListener {
            locationViewModel.getAllPins {  }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {isGranted: Boolean->
            if(isGranted) {
                setMyLocationOverlay()
            }
        }

    private fun setMyLocationOverlay() {
        var myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(activity), map)
        myLocationOverlay.enableMyLocation()
        map.overlays.add(myLocationOverlay)
    }



    private fun addAllMarkers(pins: List<LocationData>) {
        pins.forEach { pin->
            var location :  android.location.Location= Location("")
            location.longitude = pin.lon
            location.latitude = pin.lat
            addMarker(R.drawable.push_pin_48px, location, pin)

        }
    }
    private fun addMarker(icon: Int, location: android.location.Location, locationData: LocationData) {
        if(location.latitude != null && location.longitude !=null) {
            val userMarker = Marker(map)
            userMarker.icon = ContextCompat.getDrawable(this.requireContext(), icon)
            val point = GeoPoint(location.latitude, location.longitude)


            userMarker.position = point
            userMarker.id = "pin"

            userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            userMarker.setOnMarkerClickListener { marker, mapView ->
                val bundle = bundleOf("locationData" to locationData)
                findNavController().navigate(R.id.action_MainFragment_to_LocationFragment, bundle)
                true
            }
            map.overlays.add(userMarker)
        }
    }

    private fun removeAllPinsFromMap() {
        map.overlays.forEach {
            if (it is Marker && it.id == "pin") {
                map.overlays.remove(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }
}