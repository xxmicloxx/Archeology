package de.mloy.archeology.ui.settings

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import de.mloy.archeology.BaseViewModel
import de.mloy.archeology.R

class SettingsViewModel(app: Application) : BaseViewModel(app) {
    private val auth = FirebaseAuth.getInstance()
    private val email = MutableLiveData<String>(
        auth.currentUser?.email
            ?: app.getString(R.string.unknown_email)
    )

    private val totalSites = MutableLiveData<Int>(siteStore.count())
    private val visitedSites = MutableLiveData<Int>(siteStore.countVisited())
    private val newPassword = MutableLiveData<String>("")
    private val newPasswordRetyped = MutableLiveData<String>("")
    private val passwordChanging = MutableLiveData<Boolean>(false)

    fun getEmail(): LiveData<String> = email
    fun getTotalSites(): LiveData<Int> = totalSites
    fun getVisitedSites(): LiveData<Int> = visitedSites
    fun getNewPassword(): MutableLiveData<String> = newPassword
    fun getNewPasswordRetyped(): MutableLiveData<String> = newPasswordRetyped
    fun getPasswordChanging(): MutableLiveData<Boolean> = passwordChanging
}