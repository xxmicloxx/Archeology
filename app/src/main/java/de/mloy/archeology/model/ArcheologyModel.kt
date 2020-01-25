package de.mloy.archeology.model

import android.os.Parcelable
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize
import org.joda.time.LocalDate

@Parcelize
data class Site(
    var id: Long = 0,
    var fbId: String = "",
    var title: String = "",
    var description: String = "",
    var images: List<String> = listOf(),
    var location: Location = Location(),
    var isVisited: Boolean = false,
    var dateVisited: LocalDate? = null,
    var rating: Int = 0,
    var notes: String = "",
    var isFavorite: Boolean = false
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

fun Location.toLatLng(): LatLng = LatLng(this.lat, this.lng)

fun Location.toCameraUpdate(): CameraUpdate =
    CameraUpdateFactory.newLatLngZoom(this.toLatLng(), this.zoom)

fun LatLng.toCameraUpdate(): CameraUpdate =
    CameraUpdateFactory.newLatLng(this)

fun android.location.Location.toModelLocation(zoom: Float = 15f): Location =
    Location(latitude, longitude, zoom)