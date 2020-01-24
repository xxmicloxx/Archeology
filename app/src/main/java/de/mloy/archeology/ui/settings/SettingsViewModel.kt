package de.mloy.archeology.ui.settings

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import de.mloy.archeology.BaseViewModel

class SettingsViewModel(app: Application) : BaseViewModel(app) {
    private val email = MutableLiveData<String>("something@example.com")
    private val totalSites = MutableLiveData<Int>(siteStore.count())
    private val visitedSites = MutableLiveData<Int>(siteStore.countVisited())
    private val newPassword = MutableLiveData<String>("")
    private val newPasswordRetyped = MutableLiveData<String>("")

    fun getEmail(): LiveData<String> = email
    fun getTotalSites(): LiveData<Int> = totalSites
    fun getVisitedSites(): LiveData<Int> = visitedSites
    fun getNewPassword(): MutableLiveData<String> = newPassword
    fun getNewPasswordRetyped(): MutableLiveData<String> = newPasswordRetyped
}