package de.mloy.archeology.ui.editlocation

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.mloy.archeology.BaseViewModel
import de.mloy.archeology.model.Location

class EditLocationViewModel(app: Application) : BaseViewModel(app) {
    private val location = MutableLiveData<Location>(
        Location(54.115740, -4.635352, 5.3f)
    )
    private var dirty = false

    fun getLocation(): LiveData<Location> =
        location

    fun getDirty(): Boolean =
        dirty

    fun updateLocation(loc: Location) {
        location.value = loc
    }

    fun markDirty() {
        dirty = true
    }
}