package de.mloy.archeology.ui.editlocation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.lifecycle.Observer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import de.mloy.archeology.BaseActivity
import de.mloy.archeology.R
import de.mloy.archeology.getViewModel
import de.mloy.archeology.model.Location
import de.mloy.archeology.model.toCameraUpdate
import de.mloy.archeology.model.toLatLng

class EditLocationActivity : BaseActivity(), GoogleMap.OnMarkerDragListener,
    GoogleMap.OnMarkerClickListener {

    companion object {
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

    private lateinit var latView: TextView
    private lateinit var lngView: TextView

    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_location)
        postponeEnterTransition()

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

        map.addMarker(options)
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

    override fun onMarkerDragEnd(marker: Marker) {
        setCurrentLocation(marker)
    }

    override fun onMarkerDragStart(marker: Marker) {
        viewModel.markDirty()
        setCurrentLocation(marker)
    }

    override fun onMarkerDrag(marker: Marker) {
        setCurrentLocation(marker)
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        map?.animateCamera(p0.position.toCameraUpdate())
        return true
    }
}
