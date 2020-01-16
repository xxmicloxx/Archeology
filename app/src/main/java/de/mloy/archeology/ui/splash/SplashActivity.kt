package de.mloy.archeology.ui.splash

import android.os.Bundle
import de.mloy.archeology.BaseActivity
import de.mloy.archeology.R
import de.mloy.archeology.ui.sitelist.SiteListActivity

class SplashActivity : BaseActivity() {

    companion object {
        private const val SPLASH_DELAY: Long = 3000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler.postDelayed({
            onSplashTimeout()
        }, SPLASH_DELAY)
    }

    private fun onSplashTimeout() {
        startActivity(SiteListActivity.create(this))
        overridePendingTransition(
            android.R.anim.fade_in,
            R.anim.hold
        )
        finish()
    }
}
