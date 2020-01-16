package de.mloy.archeology

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

abstract class BaseActivity : AppCompatActivity() {

    protected lateinit var handler: Handler
    private var basePresenter: BasePresenter<*>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handler = Handler()
    }

    override fun onDestroy() {
        basePresenter?.onDestroy()
        super.onDestroy()
    }

    fun <T : BasePresenter<*>> initPresenter(presenter: T): T {
        basePresenter = presenter
        return presenter
    }

    protected fun setupUI() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            setupToolbar(toolbar)
        }
    }

    private fun setupToolbar(toolbar: Toolbar) {
        toolbar.title = title
        setSupportActionBar(toolbar)
    }
}