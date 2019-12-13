package de.mloy.archeology.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity
@Parcelize
data class Site(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var predefined: Boolean = false,
    var title: String = "",
    var description: String = "",
    var images: List<String> = listOf(),
    @Embedded var location: Location = Location(),
    var visited: Boolean = false,
    var dateVisited: Date? = null,
    var notes: String = ""
) : Parcelable

@Entity
@Parcelize
data class Location(
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var zoom: Float = 0f
) : Parcelable {

    val isValid: Boolean
        get() = zoom != 0f
}