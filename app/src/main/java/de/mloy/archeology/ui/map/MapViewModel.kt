package de.mloy.archeology.ui.map

import android.app.Application
import androidx.lifecycle.MutableLiveData
import de.mloy.archeology.BaseViewModel
import de.mloy.archeology.model.Site

class MapViewModel(app: Application) : BaseViewModel(app) {
    private val selectedSite = MutableLiveData<Site?>(null)

    fun getSelectedSite(): MutableLiveData<Site?> = selectedSite
}