package de.mloy.archeology

import android.os.Bundle

class SplashActivity : AppActivity() {

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
        finish()
    }
}
