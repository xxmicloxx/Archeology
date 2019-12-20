package de.mloy.archeology

import android.app.Application
import de.mloy.archeology.model.SiteStore
import de.mloy.archeology.model.json.SiteJSONStore

class MainApp : Application() {
    lateinit var store: SiteStore

    override fun onCreate() {
        super.onCreate()
        store = SiteJSONStore(this)
    }
}