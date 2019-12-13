package de.mloy.archeology.model.room

import androidx.room.*
import de.mloy.archeology.model.Site

@Dao
interface SiteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun create(site: Site)

    @Query("SELECT * FROM Site")
    fun findAll(): List<Site>

    @Query("SELECT * FROM Site WHERE id = :id")
    fun findById(id: Long): Site?

    @Update
    fun update(site: Site)

    @Delete
    fun delete(site: Site)
}