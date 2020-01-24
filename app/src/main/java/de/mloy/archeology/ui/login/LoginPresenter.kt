package de.mloy.archeology.ui.login

import de.mloy.archeology.getViewModel
import de.mloy.archeology.ui.sitelist.SiteListActivity

class LoginPresenter(private val activity: LoginActivity) {
    val vm: LoginViewModel = activity.getViewModel()

    fun register() {

    }

    fun logIn() {
        continueToApp()
    }

    fun continueToApp() {
        val intent = SiteListActivity.create(activity)
        activity.startActivity(intent)
        activity.finish()
    }
}