package de.mloy.archeology.ui.sitelist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.mloy.archeology.BaseViewModel
import de.mloy.archeology.model.Site

class SiteListViewModel(app: Application) : BaseViewModel(app) {
    private val sites = MutableLiveData<List<Site>>(siteStore.findAll())
    var favoriteFilter = false
        set(value) {
            field = value
            reload()
        }

    fun getSites(): LiveData<List<Site>> {
        return sites
    }

    fun reload() {
        if (favoriteFilter) {
            sites.value = siteStore.findAll().filter { it.isFavorite }
        } else {
            sites.value = siteStore.findAll()
        }
    }
}