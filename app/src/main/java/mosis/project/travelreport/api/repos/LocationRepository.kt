package mosis.project.travelreport.api.repos

import android.net.Uri
import android.os.Debug
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import mosis.project.travelreport.data.LocationData
import mosis.project.travelreport.data.UserInfoData
import java.util.*
import kotlin.collections.ArrayList

class LocationRepository {

    private val storageReference = FirebaseStorage.getInstance().reference
    private val databaseReference = FirebaseDatabase.getInstance().reference


    fun addLocation(locationData: LocationData, uri: Uri, callback: (String?) -> Unit) {
        storageReference.child("images").child(locationData.locationId).putFile(uri)
            .addOnSuccessListener {
                storageReference.child("images")
                    .child(locationData.locationId).downloadUrl.addOnSuccessListener { uri ->
                        locationData.image = uri.toString()
                        databaseReference.child("pins").child(locationData.locationId)
                            .setValue(locationData)
                            .addOnSuccessListener {
                                callback.invoke(null)
                            }
                            .addOnFailureListener {
                                callback.invoke(it.message)
                            }
                    }
                    .addOnFailureListener {
                        callback.invoke(it.message)
                    }
            }
    }

    fun getAllPins(callback: (List<LocationData>?) -> Unit) {
        databaseReference.child("pins").get()
            .addOnSuccessListener {
                val locations = ArrayList<LocationData>()
                for (snapshot in it.children) {
                    snapshot.getValue(LocationData::class.java)?.let { itPinData ->
                        locations.add(itPinData)
                    }
                }

                callback.invoke(locations)
            }
            .addOnFailureListener {
                callback.invoke(null)
            }
    }


    fun getLocationsByName(locationName: String, callback: (List<LocationData>?) -> Unit) {
        databaseReference.child("pins")
            .orderByChild("name")
            .equalTo(locationName)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var locations = ArrayList<LocationData>()
                    for(snapshot in dataSnapshot.children) {
                        snapshot.getValue(LocationData::class.java)?.let {
                            locations.add(it)
                        }
                    }
                    callback.invoke(locations)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.invoke(null)
                }
            })
    }

    fun getLocationsByTag(locationTag: String, callback: (List<LocationData>?) -> Unit) {
        databaseReference.child("pins")
            .orderByChild("tag")
            .equalTo(locationTag)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var locations = ArrayList<LocationData>()
                    for (snapshot in dataSnapshot.children) {
                        snapshot.getValue(LocationData::class.java)?.let {
                            locations.add(it)
                        }
                    }
                    callback.invoke(locations)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.invoke(null)
                }

            })
    }


    fun getLocationsByAuthor(authorEmail: String, callback: (List<LocationData>?) -> Unit) {


        databaseReference.child("users").orderByChild("email").equalTo(authorEmail)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshotUser: DataSnapshot) {
                    for (snap in snapshotUser.children) {
                        var user = snap.getValue(UserInfoData::class.java)
                        user?.let { itUser ->
                            databaseReference.child("pins").orderByChild("userId")
                                .equalTo(itUser.userID)
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        var locations = ArrayList<LocationData>()
                                        for (snapLoc in snapshot.children) {
                                            snapLoc.getValue(LocationData::class.java)
                                                ?.let { itLocationData ->
                                                    locations.add(itLocationData)
                                                }
                                        }
                                        callback.invoke(locations)
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        callback.invoke(null)
                                    }
                                })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback.invoke(null)
                }
            })
    }
}