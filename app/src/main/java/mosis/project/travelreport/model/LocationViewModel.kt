package mosis.project.travelreport.model

import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import mosis.project.travelreport.api.repos.LocationRepository
import mosis.project.travelreport.data.LocationData
import java.text.SimpleDateFormat
import java.util.*
import javax.security.auth.callback.Callback
import kotlin.collections.ArrayList

class LocationViewModel : ViewModel() {
    var locations : MutableLiveData<List<LocationData>> = MutableLiveData()
    var locationSearch: MutableLiveData<List<LocationData>> = MutableLiveData()
    private val locationRepository = LocationRepository()

    fun getAllPins(callback: (Unit?)->Unit) {
        locationRepository.getAllPins {
            locations.value =  it
            callback.invoke(null)
        }
    }

    fun addPin(location: LocationData, uri: Uri, callback: (String?)->Unit) {
       locationRepository.addLocation(location,uri) {
           if (it == null) {
               val existing = locations.value?.toMutableList()
               existing?.add(location)
               existing?.let { itList ->
                   locations.value = itList
               }
           } else {
               callback.invoke(it)
           }
       }
    }

    fun getLocationsByName(locationName: String, callback: (String?) -> Unit) {
        locationRepository.getLocationsByName(locationName) {
            if(it != null) {
                locationSearch.value = it
                callback.invoke(null)
            } else {
                callback.invoke("Error fetching data")
            }

        }
    }
    fun getLocationsByTag(locationTag: String, callback: (String?) -> Unit) {
        locationRepository.getLocationsByTag(locationTag) {
            if(it!=null) {
                locationSearch.value = it
                callback.invoke(null)
            } else {
                callback.invoke("Error fetching data")
            }
        }
    }
    fun getLocationsByDate(from: Date, to: Date, callback: (String?) -> Unit) {
        locationRepository.getAllPins {
            var displayLoc = ArrayList<LocationData>()
            if(it!=null) {
                it?.let {loc ->
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    for(location in loc) {
                        if(sdf.parse(location.date).before(to) && sdf.parse(location.date).after(from)) {
                            displayLoc.add(location)
                        }

                    }
                    locationSearch.value = displayLoc.toList()
                }
            } else {
                callback("Error fetching data")
            }

        }

    }
    fun getLocationsByAuthor(authorUserName: String,  callback: (String?) -> Unit) {
        locationRepository.getLocationsByAuthor(authorUserName) {
            if(it!=null) {
                locationSearch.value = it
                callback.invoke(null)
            } else {
                callback.invoke("Error fetching data")
            }
        }
    }

    fun getLocationsByDistance(currentLocation: Location,radius: Int, callback: (String?) -> Unit) {
        var locations :List<LocationData>
        var locToDisplay: ArrayList<LocationData> = ArrayList()
        locationRepository.getAllPins {
            Log.d("TAG_LOC", it?.count().toString())
            if(it!=null) {
                locations = it
                for(location in locations) {
                    var result = FloatArray(1)
                    Location.distanceBetween(currentLocation.latitude, currentLocation.longitude, location.lat, location.lon,result)
                    if((result[0]/1000)<radius.toFloat()) {
                        locToDisplay.add(location)
                    }
                }
                Log.d("TAG_LOC", locToDisplay.count().toString())
                locationSearch.value = locToDisplay

            } else {
                callback.invoke("Error fetching data")
            }
        }
    }

    fun transferToMapFromSearch( ) {
        locations.value = locationSearch.value
    }
}