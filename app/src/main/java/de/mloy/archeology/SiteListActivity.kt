package de.mloy.archeology

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import de.mloy.archeology.ui.sitelist.SiteListFragment

class SiteListActivity : AppCompatActivity() {

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
