package de.mloy.archeology.ui.editlocation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import de.mloy.archeology.BaseActivity
import de.mloy.archeology.R
import de.mloy.archeology.getViewModel
import de.mloy.archeology.helper.checkLocationPermissions
import de.mloy.archeology.helper.createDefaultLocationRequest
import de.mloy.archeology.model.Location
import de.mloy.archeology.model.toCameraUpdate
import de.mloy.archeology.model.toLatLng
import de.mloy.archeology.model.toModelLocation

class EditLocationActivity : BaseActivity(), GoogleMap.OnMarkerDragListener,
    GoogleMap.OnMarkerClickListener {

    companion object {
        private const val REQUEST_LOCATION_CODE = 0

        const val EXTRA_LOCATION = "location"

        private const val EXTRA_TITLE = "title"
        private const val EXTRA_START_LOCATION = "startLocation"

        fun create(ctx: Context, title: String, startLocation: Location = Location()): Intent {
            val intent = Intent(ctx, EditLocationActivity::class.java)

            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_START_LOCATION, startLocation)

            return intent
        }
    }

    private lateinit var viewModel: EditLocationViewModel
    private lateinit var title: String
    private lateinit var startLocation: Location

    private lateinit var locationService: FusedLocationProviderClient
    private var gpsOn = false

    private val locationRequest = createDefaultLocationRequest()
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            val l = p0?.lastLocation ?: return
            val loc = l.toModelLocation()
            viewModel.updateLocation(loc)
            moveMarker(loc)
        }
    }

    private lateinit var latView: TextView
    private lateinit var lngView: TextView

    private var map: GoogleMap? = null
    private var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_location)
        postponeEnterTransition()

        locationService = LocationServices.getFusedLocationProviderClient(this)

        viewModel = getViewModel()

        title = intent.getStringExtra(EXTRA_TITLE)!!
        startLocation = intent.getParcelableExtra(EXTRA_START_LOCATION)!!
        if (savedInstanceState == null) {
            if (startLocation.isValid) {
                viewModel.updateLocation(startLocation)
            }
        }

        val transInflater = TransitionInflater.from(this)
        window.enterTransition = transInflater.inflateTransition(R.transition.explode)
        window.sharedElementEnterTransition = ChangeBounds()

        setupUI(enableUp = true)

        val titleView = findViewById<TextView>(R.id.titleView)
        latView = findViewById(R.id.latView)
        lngView = findViewById(R.id.lngView)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        titleView.text = title

        mapFragment.getMapAsync { map ->
            initMap(map)
        }

        viewModel.getLocation().observe(this, Observer {
            updateLocation(it)
        })
    }

    private fun initMap(map: GoogleMap) {
        this.map = map

        map.setOnMarkerDragListener(this)
        map.setOnMarkerClickListener(this)

        val loc = viewModel.getLocation().value!!
        val options = MarkerOptions()
            .draggable(true)
            .position(loc.toLatLng())

        marker = map.addMarker(options)
        map.moveCamera(loc.toCameraUpdate())

        startPostponedEnterTransition()
    }

    private fun updateLocation(loc: Location) {
        latView.text = getString(R.string.latitude_value, loc.lat)
        lngView.text = getString(R.string.longitude_value, loc.lng)
    }

    private fun setCurrentLocation(marker: Marker) {
        val m = map ?: return

        val loc = Location(
            marker.position.latitude,
            marker.position.longitude,
            m.cameraPosition.zoom
        )

        viewModel.updateLocation(loc)
    }

    private fun finishWithResult() {
        if (viewModel.getDirty()) {
            // set result
            val loc = viewModel.getLocation().value ?: Location()
            val intent = Intent()
            intent.putExtra(EXTRA_LOCATION, loc)
            setResult(Activity.RESULT_OK, intent)
        } else {
            setResult(Activity.RESULT_CANCELED)
        }

        finish()
    }

    private fun setGps(on: Boolean = true) {
        if (on && !gpsOn) {
            if (checkLocationPermissions(this, REQUEST_LOCATION_CODE)) {
                locationService.requestLocationUpdates(locationRequest, locationCallback, null)
                viewModel.markDirty()
            } else {
                return
            }
        } else if (!on && gpsOn) {
            locationService.removeLocationUpdates(locationCallback)
        }

        gpsOn = on
        invalidateOptionsMenu()
    }

    private fun moveMarker(location: Location) {
        marker?.position = location.toLatLng()
        map?.animateCamera(location.toCameraUpdate())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finishWithResult()
                return true
            }

            R.id.unset -> {
                // unset location
                viewModel.markDirty()
                viewModel.updateLocation(Location())
                finishWithResult()
                return true
            }

            R.id.gps -> {
                setGps(!gpsOn)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finishWithResult()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_edit_location, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (gpsOn) {
            val gpsEntry = menu.findItem(R.id.gps)
            gpsEntry.setIcon(R.drawable.ic_location_enabled_white_24dp)
            gpsEntry.setTitle(R.string.disable_locating)
        }

        return true
    }

    override fun onMarkerDragEnd(marker: Marker) {
        setCurrentLocation(marker)
    }

    override fun onMarkerDragStart(marker: Marker) {
        viewModel.markDirty()
        setGps(false)
        setCurrentLocation(marker)
    }

    override fun onMarkerDrag(marker: Marker) {
        setCurrentLocation(marker)
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        map?.animateCamera(p0.position.toCameraUpdate())
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_LOCATION_CODE -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setGps(true)
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        if (gpsOn) {
            locationService.removeLocationUpdates(locationCallback)
        }
        super.onDestroy()
    }
}
