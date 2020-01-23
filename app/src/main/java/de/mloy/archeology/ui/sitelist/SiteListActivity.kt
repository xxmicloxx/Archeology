package de.mloy.archeology.ui.sitelist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.mloy.archeology.BaseActivity
import de.mloy.archeology.R
import de.mloy.archeology.getViewModel
import de.mloy.archeology.model.Site
import de.mloy.archeology.ui.site.SiteActivity

class SiteListActivity : BaseActivity() {

    companion object {
        const val RELOAD_REQUEST_CODE = 0

        fun create(ctx: Context): Intent {
            return Intent(ctx, SiteListActivity::class.java)
        }
    }

    private lateinit var viewModel: SiteListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_list)

        viewModel = getViewModel()
        setupUI()

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
        menuInflater.inflate(R.menu.sitelist, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
}
