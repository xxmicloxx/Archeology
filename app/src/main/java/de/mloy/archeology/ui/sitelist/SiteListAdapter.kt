package de.mloy.archeology.ui.sitelist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import de.mloy.archeology.R
import de.mloy.archeology.helper.readImageFromPath
import de.mloy.archeology.model.Site

class SiteListAdapter(private val sites: List<Site>) :
    RecyclerView.Adapter<SiteListAdapter.ViewHolder>() {
    inner class ViewHolder(view: CardView) : RecyclerView.ViewHolder(view) {
        private val context = view.context
        private val title = view.findViewById<TextView>(R.id.titleTextView)
        private val latitude = view.findViewById<TextView>(R.id.latitudeTextView)
        private val longitude = view.findViewById<TextView>(R.id.longitudeTextView)
        private val visited = view.findViewById<CheckBox>(R.id.visitedBox)
        private val image = view.findViewById<ImageView>(R.id.previewImage)
        private var site: Site? = null

        init {
            view.setOnClickListener {

            }
        }

        fun bind(site: Site) {
            this.site = site

            title.text = site.title
            visited.isChecked = site.isVisited

            val imagePath = site.images.firstOrNull()
            if (imagePath != null) {
                image.visibility = View.VISIBLE
                image.setImageBitmap(readImageFromPath(context, imagePath))
            } else {
                image.visibility = View.GONE
            }

            site.location.apply {
                if (isValid) {
                    latitude.visibility = View.VISIBLE
                    longitude.visibility = View.VISIBLE
                    latitude.text = context.getString(R.string.sitelist_latitude, lat)
                    longitude.text = context.getString(R.string.sitelist_longitude, lng)
                } else {
                    latitude.visibility = View.GONE
                    longitude.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.listitem_site_list, parent, false) as CardView

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return sites.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val site = sites[position]
        holder.bind(site)
    }
}