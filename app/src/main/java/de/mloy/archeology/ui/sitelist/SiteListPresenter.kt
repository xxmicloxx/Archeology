package de.mloy.archeology.ui.sitelist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import de.mloy.archeology.BasePresenter
import de.mloy.archeology.model.Site

class SiteListPresenter(activity: SiteListActivity) : BasePresenter<SiteListActivity>(activity) {
    val viewModel: SiteListViewModel = getViewModel()

    init {
        viewModel.updateSites(store.findAll())
    }

    fun addSite() {
        // TODO call add site
    }
}

class SiteListViewModel(app: Application) : AndroidViewModel(app) {
    val sites get() = mutableSites

    private var mutableSites = MutableLiveData<List<Site>>()

    fun updateSites(sites: List<Site>) {
        mutableSites.value = sites
    }
}