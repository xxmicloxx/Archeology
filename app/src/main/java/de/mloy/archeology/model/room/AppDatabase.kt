package de.mloy.archeology.model.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.mloy.archeology.model.Site

@Database(entities = [Site::class], version = 1, exportSchema = false)
@TypeConverters(AppConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun siteDao(): SiteDao
}