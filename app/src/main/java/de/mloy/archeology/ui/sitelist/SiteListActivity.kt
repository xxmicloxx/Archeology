package de.mloy.archeology.ui.sitelist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.mloy.archeology.BaseActivity
import de.mloy.archeology.R
import de.mloy.archeology.getViewModel
import de.mloy.archeology.model.Site
import de.mloy.archeology.ui.login.LoginActivity
import de.mloy.archeology.ui.map.MapActivity
import de.mloy.archeology.ui.settings.SettingsActivity
import de.mloy.archeology.ui.site.SiteActivity

class SiteListActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val RELOAD_REQUEST_CODE = 0

        fun create(ctx: Context): Intent {
            return Intent(ctx, SiteListActivity::class.java)
        }
    }

    private lateinit var viewModel: SiteListViewModel
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_list)

        viewModel = getViewModel()
        setupUI()

        drawer = findViewById(R.id.drawerLayout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        drawerToggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.drawer_open, R.string.drawer_close
        )

        drawer.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.navigation)
        navigationView.setNavigationItemSelectedListener(this)

        val siteList = findViewById<RecyclerView>(R.id.sitelist).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SiteListActivity)
        }

        val emptyView = findViewById<TextView>(R.id.emptyText)

        viewModel.getSites().observe(this, Observer {
            siteList.adapter = SiteListAdapter(it, this)
            if (it.isEmpty()) {
                siteList.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
            } else {
                siteList.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_sitelist, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (viewModel.favoriteFilter) {
            val fav = menu.findItem(R.id.favorite)
            fav.setTitle(R.string.disable_filter)
            fav.setIcon(R.drawable.ic_star_white_24dp)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }

        when (item.itemId) {
            R.id.favorite -> {
                viewModel.favoriteFilter = !viewModel.favoriteFilter
                invalidateOptionsMenu()
            }

            R.id.add -> {
                val intent = SiteActivity.create(this)
                startActivityForResult(intent, RELOAD_REQUEST_CODE)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RELOAD_REQUEST_CODE -> {
                viewModel.reload()
            }
        }
    }

    fun editSite(site: Site) {
        val intent = SiteActivity.create(this, site)
        startActivityForResult(intent, RELOAD_REQUEST_CODE)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map -> {
                val intent = MapActivity.create(this)
                startActivity(intent)
            }

            R.id.settings -> {
                val intent = SettingsActivity.create(this)
                startActivity(intent)
            }

            R.id.logout -> {
                // log out
                val auth = FirebaseAuth.getInstance()
                auth.signOut()

                // go to login activity
                val intent = LoginActivity.create(this)
                startActivity(intent)
                finish()
            }

            else -> return false
        }

        drawer.closeDrawers()
        return true
    }
}
