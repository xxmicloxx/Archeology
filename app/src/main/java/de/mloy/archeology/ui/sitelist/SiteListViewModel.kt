package de.mloy.archeology.ui.sitelist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.mloy.archeology.BaseViewModel
import de.mloy.archeology.model.Site

class SiteListViewModel(app: Application) : BaseViewModel(app) {
    private val sites = MutableLiveData<List<Site>>(siteStore.findAll())

    fun getSites(): LiveData<List<Site>> {
        return sites
    }

    fun reload() {
        sites.value = siteStore.findAll()
    }
}