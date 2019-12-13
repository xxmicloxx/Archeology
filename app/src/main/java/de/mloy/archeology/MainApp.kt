package de.mloy.archeology

import android.app.Application
import de.mloy.archeology.model.SiteStore

class MainApp : Application() {
    lateinit var store: SiteStore
}