package de.mloy.archeology.ui.site

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.mloy.archeology.BaseViewModel
import de.mloy.archeology.model.Location
import de.mloy.archeology.model.Site
import org.joda.time.LocalDate

class SiteViewModel(app: Application) : BaseViewModel(app) {
    private val title = MutableLiveData<String>()
    private val description = MutableLiveData<String>()
    private val images = MutableLiveData<List<String>>()
    private val location = MutableLiveData<Location>()
    private val notes = MutableLiveData<String>()
    private val visited = MutableLiveData<Boolean>()
    private val dateVisited = MutableLiveData<LocalDate?>()
    var edit = false

    lateinit var site: Site

    fun getTitle(): MutableLiveData<String> = title
    fun getDescription(): MutableLiveData<String> = description
    fun getImages(): LiveData<List<String>> = images
    fun getLocation(): MutableLiveData<Location> = location
    fun getNotes(): MutableLiveData<String> = notes
    fun getVisited(): MutableLiveData<Boolean> = visited
    fun getDateVisited(): MutableLiveData<LocalDate?> = dateVisited

    val isDirty: Boolean
        get() = getUpdatedSite() != site

    fun loadFromSite(site: Site) {
        this.site = site

        title.value = site.title
        description.value = site.description
        images.value = site.images
        location.value = site.location
        dateVisited.value = site.dateVisited
        visited.value = site.isVisited
        notes.value = site.notes
    }

    private fun getUpdatedSite(): Site =
        site.copy(
            title = title.value!!,
            description = description.value!!,
            images = images.value!!,
            location = location.value!!,
            isVisited = visited.value!!,
            dateVisited = dateVisited.value,
            notes = notes.value!!
        )

    fun commit() {
        val site = getUpdatedSite()
        if (edit) {
            siteStore.update(site)
        } else {
            siteStore.create(site)
        }
    }

    fun addImage(image: String) {
        val oldImages = images.value?.toMutableList() ?: mutableListOf()
        oldImages.add(image)
        images.value = oldImages
    }

    fun removeImage(idx: Int) {
        val images = images.value?.toMutableList() ?: return
        images.removeAt(idx)
        this.images.value = images
    }

    fun setVisited(visited: Boolean = true) {
        if (visited) {
            getDateVisited().value = LocalDate.now()
        } else {
            getDateVisited().value = null
        }
        getVisited().value = visited
    }
}