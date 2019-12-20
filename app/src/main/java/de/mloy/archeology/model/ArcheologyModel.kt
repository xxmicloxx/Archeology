package de.mloy.archeology.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Site(
    var id: Long = 0,
    var title: String = "",
    var description: String = "",
    var images: List<String> = listOf(),
    var location: Location = Location(),
    var visited: Boolean = false,
    var dateVisited: Date? = null,
    var notes: String = ""
) : Parcelable

@Parcelize
data class Location(
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var zoom: Float = 0f
) : Parcelable {

    val isValid: Boolean
        get() = zoom != 0f
}