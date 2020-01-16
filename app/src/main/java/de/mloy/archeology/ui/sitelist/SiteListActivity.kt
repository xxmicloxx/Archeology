package de.mloy.archeology.ui.sitelist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.mloy.archeology.BaseActivity
import de.mloy.archeology.R

class SiteListActivity : BaseActivity() {

    companion object {
        fun create(ctx: Context): Intent {
            return Intent(ctx, SiteListActivity::class.java)
        }
    }

    private lateinit var presenter: SiteListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_list)

        presenter = SiteListPresenter(this)
        setupUI()

        val siteList = findViewById<RecyclerView>(R.id.sitelist).apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SiteListActivity)
        }

        presenter.viewModel.sites.observe(this, Observer {
            siteList.adapter = SiteListAdapter(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sitelist, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add -> {
                presenter.addSite()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
