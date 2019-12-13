package de.mloy.archeology.model.room

import android.content.Context
import androidx.room.Room
import de.mloy.archeology.model.Site
import de.mloy.archeology.model.SiteStore

class SiteStoreRoom(context: Context) : SiteStore {

    private val dao: SiteDao

    init {
        Room.databaseBuilder(context, AppDatabase::class.java, "room.db")
            .fallbackToDestructiveMigration()
            .build()
            .apply {
                dao = siteDao()
            }
    }

    override fun findAll(): List<Site> {
        return dao.findAll()
    }

    override fun create(site: Site) {
        return dao.create(site)
    }

    override fun update(site: Site) {
        return dao.update(site)
    }

    override fun delete(site: Site) {
        return dao.delete(site)
    }

    override fun findById(id: Long): Site? {
        return dao.findById(id)
    }
}