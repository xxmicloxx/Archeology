package de.mloy.archeology

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import de.mloy.archeology.model.SiteStore

abstract class BasePresenter<T : BaseActivity>(val activity: T) {

    val context: Context
        get() = activity

    val applicationContext: Context
        get() = activity.applicationContext

    val store: SiteStore
        get() = SiteStore.getInstance(context)

    protected inline fun <reified K : ViewModel> getViewModel(): K {
        return ViewModelProviders.of(activity).get(K::class.java)
    }

    open fun onDestroy() {}
}