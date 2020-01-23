package de.mloy.archeology

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import de.mloy.archeology.model.SiteStore

abstract class BaseViewModel(app: Application) : AndroidViewModel(app) {
    val siteStore: SiteStore
        get() = SiteStore.getInstance(getApplication())
}