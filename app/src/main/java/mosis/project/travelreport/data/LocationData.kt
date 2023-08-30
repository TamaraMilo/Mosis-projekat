package mosis.project.travelreport.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.logging.SimpleFormatter

@Parcelize
data class LocationData(var locationId: String = "",
                        var userId: String = "",
                        var image: String = "",
                        var description: String = "",
                        var rate: Int =0,
                        var lat: Double = 0.0,
                        var lon: Double = 0.0,
                        var date: String ="",
                        var tag: String = "",
                        var name:String="",
                    ): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),

    ) {
    }

    companion object : Parceler<LocationData> {

        override fun LocationData.write(parcel: Parcel, flags: Int) {
            parcel.writeString(locationId)
            parcel.writeString(userId)
            parcel.writeString(image)
            parcel.writeString(description)
            parcel.writeInt(rate)
            parcel.writeDouble(lat)
            parcel.writeDouble(lon)
            parcel.writeString(tag)
            parcel.writeString(name)
            parcel.writeString(date)
        }

        override fun create(parcel: Parcel): LocationData {
            return LocationData(parcel)
        }
    }



}
