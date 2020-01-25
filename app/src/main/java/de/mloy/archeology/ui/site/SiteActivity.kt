package de.mloy.archeology.ui.site

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import de.mloy.archeology.BaseActivity
import de.mloy.archeology.R
import de.mloy.archeology.databinding.ActivitySiteBinding
import de.mloy.archeology.model.Location
import de.mloy.archeology.model.Site
import de.mloy.archeology.model.toCameraUpdate
import de.mloy.archeology.model.toLatLng
import de.mloy.archeology.ui.editlocation.EditLocationActivity
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

class SiteActivity : BaseActivity() {

    companion object {
        const val LOCATION_REQUEST_CODE = 0

        private const val SITE_EXTRA = "site"

        fun create(ctx: Context, site: Site? = null): Intent {
            val intent = Intent(ctx, SiteActivity::class.java)
            if (site != null) {
                intent.putExtra(SITE_EXTRA, site)
            }
            return intent
        }
    }

    lateinit var binding: ActivitySiteBinding
    lateinit var presenter: SitePresenter

    var mapFragment: SupportMapFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SitePresenter(this)

        presenter.vm.edit = intent.hasExtra("site")
        if (savedInstanceState == null) {
            val site = intent.getParcelableExtra("site") ?: Site()
            presenter.vm.loadFromSite(site)
        }

        if (!presenter.vm.edit) {
            title = getString(R.string.new_site_title)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_site)
        binding.lifecycleOwner = this
        binding.presenter = presenter

        setupUI(enableUp = true)

        presenter.vm.getImages().observe(this, Observer { updateImages(it) })
        presenter.vm.getLocation().observe(this, Observer { updateLocation(it) })
        presenter.vm.getDateVisited().observe(this, Observer { updateDateVisited(it) })

        // this prevents the beforeTextChanged listener from firing when the original text is loaded
        binding.executePendingBindings()

        binding.notesEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                presenter.onNotesPreChanged(s)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateDateVisited(date: LocalDate?) {
        if (date == null) {
            binding.visitedSwitch.setText(R.string.not_yet_visited)
        } else {
            val dateFormatter = DateTimeFormat.longDate()
            val dateStr = dateFormatter.print(date)
            binding.visitedSwitch.text = getString(R.string.visited_value, dateStr)
        }
    }

    private fun updateImages(it: List<String>) {
        val pos = binding.imagePager.currentItem

        val adapter = SiteImageAdapter(supportFragmentManager, it)
        binding.imagePager.adapter = adapter
        binding.imageIndicator.setViewPager(binding.imagePager)
        adapter.registerDataSetObserver(binding.imageIndicator.dataSetObserver)

        binding.imagePager.setCurrentItem(pos, false)
    }

    private fun updateLocation(loc: Location) {
        if (!loc.isValid && mapFragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(mapFragment!!)
                .commit()

            mapFragment = null
        }

        if (!loc.isValid) {
            return
        }

        if (mapFragment == null) {
            // add map fragment
            mapFragment = SupportMapFragment.newInstance()

            supportFragmentManager.beginTransaction()
                .add(R.id.mapLayout, mapFragment!!)
                .commit()
        }

        mapFragment!!.getMapAsync {
            it.clear()
            it.moveCamera(loc.toCameraUpdate())

            val markerOptions = MarkerOptions().position(loc.toLatLng())
            it.addMarker(markerOptions)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_site, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (!presenter.vm.edit) {
            menu.findItem(R.id.delete).isVisible = false
        }

        if (presenter.vm.site.isFavorite) {
            val favoriteItem = menu.findItem(R.id.favorite)
            favoriteItem.setTitle(R.string.remove_from_favorites)
            favoriteItem.setIcon(R.drawable.ic_star_white_24dp)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (resultCode != Activity.RESULT_OK) {
                    return
                }

                // get the location
                val dat = data ?: return
                val loc = dat.getParcelableExtra<Location>(EditLocationActivity.EXTRA_LOCATION)
                    ?: return

                presenter.vm.getLocation().value = loc
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }

            R.id.favorite -> {
                presenter.vm.site.isFavorite = !presenter.vm.site.isFavorite
                if (presenter.vm.edit) {
                    presenter.vm.siteStore.update(presenter.vm.site)
                }

                invalidateOptionsMenu()
            }

            R.id.save -> {
                presenter.commitAndFinish()
                return true
            }

            R.id.delete -> {
                presenter.deleteAndFinish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun finishAfterTransition() {
        window.sharedElementsUseOverlay = true
        super.finishAfterTransition()
    }

    override fun onBackPressed() {
        if (presenter.vm.isDirty) {
            presenter.showDismissDialog()
        } else {
            // just exit right now
            finishAfterTransition()
        }
    }
}
