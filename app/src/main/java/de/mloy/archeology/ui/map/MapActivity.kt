package de.mloy.archeology.ui.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import de.mloy.archeology.BaseActivity
import de.mloy.archeology.R
import de.mloy.archeology.databinding.ActivityMapBinding
import de.mloy.archeology.model.Site
import de.mloy.archeology.model.toCameraUpdate

class MapActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    companion object {
        const val REFRESH_REQUEST_CODE = 0

        fun create(ctx: Context): Intent {
            return Intent(ctx, MapActivity::class.java)
        }
    }

    lateinit var binding: ActivityMapBinding
        private set

    lateinit var presenter: MapPresenter
        private set

    lateinit var map: GoogleMap
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = MapPresenter(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        binding.lifecycleOwner = this
        binding.presenter = presenter

        setupUI(enableUp = true)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.setOnMarkerClickListener(this)

        presenter.refresh()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val site = marker.tag as Site
        presenter.vm.getSelectedSite().value = site
        map.animateCamera(site.location.toCameraUpdate())

        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REFRESH_REQUEST_CODE -> {
                // reload all the markers
                presenter.refresh()
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }
}