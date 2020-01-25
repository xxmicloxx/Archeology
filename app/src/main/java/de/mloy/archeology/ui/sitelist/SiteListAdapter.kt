package de.mloy.archeology.ui.sitelist

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import de.mloy.archeology.R
import de.mloy.archeology.model.Site

class SiteListAdapter(private val sites: List<Site>, private val activity: SiteListActivity) :
    RecyclerView.Adapter<SiteListAdapter.ViewHolder>() {
    inner class ViewHolder(view: CardView) : RecyclerView.ViewHolder(view) {
        private val context = view.context
        private val title = view.findViewById<TextView>(R.id.titleTextView)
        private val latitude = view.findViewById<TextView>(R.id.latitudeTextView)
        private val longitude = view.findViewById<TextView>(R.id.longitudeTextView)
        private val visited = view.findViewById<ImageView>(R.id.visitedBox)
        private val favorite = view.findViewById<ImageView>(R.id.favoriteBox)
        private val image = view.findViewById<ImageView>(R.id.previewImage)
        private var site: Site? = null

        init {
            view.setOnClickListener {
                site ?: return@setOnClickListener

                activity.editSite(site!!)
            }
        }

        fun bind(site: Site) {
            this.site = site

            title.text = site.title
            visited.visibility = if (site.isVisited) View.VISIBLE else View.GONE
            favorite.visibility = if (site.isFavorite) View.VISIBLE else View.GONE

            val imagePath = site.images.firstOrNull()
            Glide.with(image)
                .load(imagePath)
                .error(R.drawable.ic_image_frame_black_24dp)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        image.scaleType = ImageView.ScaleType.CENTER
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        image.scaleType = ImageView.ScaleType.CENTER_CROP
                        return false
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(image)

            site.location.apply {
                if (isValid) {
                    latitude.visibility = View.VISIBLE
                    longitude.visibility = View.VISIBLE
                    latitude.text = context.getString(R.string.latitude_value, lat)
                    longitude.text = context.getString(R.string.longitude_value, lng)
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