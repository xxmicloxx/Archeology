package de.mloy.archeology.model.json

import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import de.mloy.archeology.helper.exists
import de.mloy.archeology.helper.read
import de.mloy.archeology.helper.write
import de.mloy.archeology.model.Site
import de.mloy.archeology.model.SiteStore
import org.joda.time.LocalDate
import java.util.*

class SiteJSONStore(private val context: Context) : SiteStore {

    companion object {
        const val JSON_FILE = "sites.json"

        private val listType = object : TypeToken<ArrayList<Site>>() {}.type

        fun generateRandomId(): Long {
            return Random().nextLong()
        }
    }

    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .create()

    private var sites = mutableListOf<Site>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): List<Site> {
        return sites
    }

    override fun create(site: Site) {
        site.id = generateRandomId()
        sites.add(site)
        serialize()
    }

    override fun update(site: Site) {
        findById(site.id)?.apply {
            title = site.title
            description = site.description
            images = site.images
            location = site.location
            isVisited = site.isVisited
            dateVisited = site.dateVisited
            notes = site.notes
        }

        serialize()
    }

    override fun delete(site: Site) {
        sites.removeAll { it.id == site.id }
    }

    override fun findById(id: Long): Site? {
        return sites.find { it.id == id }
    }

    private fun serialize() {
        val json = gson.toJson(sites, listType)
        write(context, JSON_FILE, json)
    }

    private fun deserialize() {
        val json = read(context, JSON_FILE) ?: return
        sites = gson.fromJson(json, listType)
    }
}

class LocalDateAdapter : TypeAdapter<LocalDate>() {
    override fun write(writer: JsonWriter, value: LocalDate?) {
        if (value != null) {
            val millis = value.toDateTimeAtStartOfDay().millis
            writer.value(millis)
        } else {
            writer.nullValue()
        }
    }

    override fun read(reader: JsonReader): LocalDate? {
        if (reader.peek() == JsonToken.NULL) {
            return null
        }

        val millis = reader.nextLong()
        return LocalDate(millis)
    }
}