package de.mloy.archeology.model

interface SiteStore {
    fun findAll(): List<Site>
    fun create(site: Site)
    fun update(site: Site)
    fun delete(site: Site)
    fun findById(id: Long): Site?
}