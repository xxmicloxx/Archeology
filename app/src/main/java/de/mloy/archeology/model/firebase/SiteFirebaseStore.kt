package de.mloy.archeology.model.firebase

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.mloy.archeology.helper.readImageFromPath
import de.mloy.archeology.model.Location
import de.mloy.archeology.model.Site
import de.mloy.archeology.model.SiteStore
import org.joda.time.LocalDate
import java.io.ByteArrayOutputStream
import java.util.*

class SiteFirebaseStore(private val context: Context) : SiteStore {

    companion object {
        fun generateRandomId(): Long {
            return Random().nextLong()
        }
    }

    val sites = mutableListOf<Site>()
    lateinit var userId: String
    lateinit var db: DatabaseReference
    lateinit var st: StorageReference

    private fun getSiteDB() = db.child("users").child(userId).child("sites")

    override fun findAll(): List<Site> =
        sites

    override fun create(site: Site) {
        val sites = getSiteDB()
        sites.push().key?.let {
            site.id = generateRandomId()
            site.fbId = it
            this.sites.add(site)
            sites.child(it).setValue(site.toFirebaseSite())
            uploadImages(site)
        }
    }

    override fun update(site: Site) {
        val index = sites.indexOfFirst { it.fbId == site.fbId }
        if (index != -1) {
            sites.removeAt(index)
            sites.add(index, site)
        }

        getSiteDB().child(site.fbId).setValue(site.toFirebaseSite())
        uploadImages(site)
    }

    override fun delete(site: Site) {
        getSiteDB().child(site.fbId).removeValue()
        sites.remove(site)
    }

    override fun findById(id: Long): Site? =
        sites.find { it.id == id }

    override fun count(): Int =
        sites.count()

    override fun countVisited(): Int =
        sites.count { it.isVisited }

    private fun uploadImages(site: Site) {
        val images = site.images.toMutableList()

        images.forEachIndexed { index, it ->
            if (it.isEmpty() || it[0] == 'h') {
                return@forEachIndexed
            }

            val imageRef = st.child("$userId/${site.fbId}/${index}")
            val outputStream = ByteArrayOutputStream()
            readImageFromPath(context, it)?.let { bitmap ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                val data = outputStream.toByteArray()
                val uploadTask = imageRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    // fuck
                    Log.e("FirebaseStore", "Upload failure! Image not stored.")
                }.addOnSuccessListener {
                    it.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                        images[index] = uri.toString()
                        site.images = images
                        getSiteDB().child(site.fbId).setValue(site.toFirebaseSite())
                    }
                }
            }
        }
    }

    fun fetchSites(done: () -> Unit) {
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        db = FirebaseDatabase.getInstance().reference
        st = FirebaseStorage.getInstance().reference
        sites.clear()
        getSiteDB().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                throw p0.toException()
            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.mapNotNull { it.getValue(FirebaseSite::class.java) }
                    .mapTo(sites) { it.toSite() }

                done()
            }
        })
    }
}

data class FirebaseSite(
    var id: Long = 0,
    var fbId: String = "",
    var title: String = "",
    var description: String = "",
    var images: List<String> = listOf(),
    var location: Location = Location(),
    var isVisited: Boolean = false,
    var dateVisited: Long? = 0,
    var rating: Int = 0,
    var notes: String = "",
    var isFavorite: Boolean = false
) {
    fun toSite(): Site {
        val millis = dateVisited
        val visited = if (millis == null || millis < 0L) null else LocalDate(millis)

        return Site(
            id,
            fbId,
            title,
            description,
            images,
            location,
            isVisited,
            visited,
            rating,
            notes,
            isFavorite
        )
    }
}

fun Site.toFirebaseSite(): FirebaseSite {
    val millis = dateVisited?.toDateTimeAtStartOfDay()?.millis
    return FirebaseSite(
        id,
        fbId,
        title,
        description,
        images,
        location,
        isVisited,
        millis,
        rating,
        notes,
        isFavorite
    )
}