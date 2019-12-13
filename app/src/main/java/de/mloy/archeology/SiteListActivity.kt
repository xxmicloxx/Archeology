package de.mloy.archeology

import android.content.Context
import android.content.Intent
import android.os.Bundle
import de.mloy.archeology.ui.sitelist.SiteListFragment

class SiteListActivity : AppActivity() {

    companion object {
        fun create(ctx: Context): Intent {
            return Intent(ctx, SiteListActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.site_list_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SiteListFragment.newInstance())
                .commitNow()
        }
    }

}
