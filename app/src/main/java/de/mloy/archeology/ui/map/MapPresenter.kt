package de.mloy.archeology.ui.map

import android.graphics.drawable.Drawable
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.model.MarkerOptions
import de.mloy.archeology.R
import de.mloy.archeology.getViewModel
import de.mloy.archeology.model.Site
import de.mloy.archeology.model.toCameraUpdate
import de.mloy.archeology.model.toLatLng
import de.mloy.archeology.ui.site.SiteActivity


class MapPresenter(private val activity: MapActivity) {
    val vm: MapViewModel = activity.getViewModel()

    init {
        vm.getSelectedSite().observe(activity, Observer { onSelectedSiteChanged(it) })
    }

    private fun onSelectedSiteChanged(site: Site?) {
        val transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(activity.binding.locationCard)

        TransitionManager.beginDelayedTransition(activity.binding.contentLayout, transition)
        activity.binding.locationCard.visibility = if (site != null) View.VISIBLE else View.GONE

        site ?: return

        val image = site.images.firstOrNull()

        Glide.with(activity)
            .load(image)
            .error(R.drawable.ic_image_frame_black_24dp)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    activity.binding.locationImage.scaleType = ImageView.ScaleType.CENTER
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    activity.binding.locationImage.scaleType = ImageView.ScaleType.CENTER_CROP
                    return false
                }
            })
            .transition(DrawableTransitionOptions.withCrossFade())
            .circleCrop()
            .into(activity.binding.locationImage)
    }

    fun refresh() {
        activity.map.clear()

        val sites = vm.siteStore.findAll().filter { it.location.isValid }
        sites.forEach {
            val options = MarkerOptions()
                .title(it.title)
                .position(it.location.toLatLng())

            activity.map.addMarker(options).tag = it
            activity.map.moveCamera(it.location.toCameraUpdate())
        }

        // reload site
        val oldSite = vm.getSelectedSite().value
        if (oldSite != null) {
            val site = vm.siteStore.findById(oldSite.id)
            vm.getSelectedSite().value = site
            if (site != null) {
                activity.map.moveCamera(site.location.toCameraUpdate())
            }
        }
    }

    fun editSite() {
        // edit currently selected site
        val intent = SiteActivity.create(activity, vm.getSelectedSite().value)
        activity.startActivityForResult(intent, MapActivity.REFRESH_REQUEST_CODE)
    }
}