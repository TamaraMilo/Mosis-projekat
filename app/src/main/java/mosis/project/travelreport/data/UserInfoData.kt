package mosis.project.travelreport.data

import com.google.firebase.database.Exclude
import java.util.Date

data class UserInfoData(
    var userID:String = "",
    var fullName:String = "",
    var phoneNumber: String = "",
    var date:String = "",
    var imageID:String = "",
    var email:String = "",
    var numberOfPosts: Int = 0,
    var score: Int = 0
    ): Comparable<UserInfoData>
{
    override operator fun compareTo(other: UserInfoData): Int {
        if(score == other.score) return 0
        if(score < other.score) return -1
        return 1
    }

    @Exclude
    fun toMap() : Map<String, Any?>
    {
        return mapOf(
            "userID" to userID,
            "fullName" to fullName,
            "phoneNumber" to phoneNumber,
            "date" to date,
            "imageID" to imageID,
            "numberOfPosts" to numberOfPosts,
            "score" to score,

        )
    }

}
