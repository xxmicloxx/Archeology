package de.mloy.archeology.model

import android.content.Context
import de.mloy.archeology.model.firebase.SiteFirebaseStore

interface SiteStore {
    companion object {
        // using a singleton as per recommendation of the android documentation
        // https://developer.android.com/reference/android/app/Application.html

        private var instance: SiteStore? = null

        fun getInstance(context: Context): SiteStore {
            synchronized(this) {
                if (instance == null) {
                    instance = SiteFirebaseStore(context)
                }

                return instance!!
            }
        }
    }

    fun findAll(): List<Site>
    fun create(site: Site)
    fun update(site: Site)
    fun delete(site: Site)
    fun findById(id: Long): Site?
    fun count(): Int
    fun countVisited(): Int
}