package de.mloy.archeology.ui.login

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.mloy.archeology.BaseViewModel

class LoginViewModel(app: Application) : BaseViewModel(app) {
    private val email = MutableLiveData<String>("")
    private val password = MutableLiveData<String>("")
    private val isValid = MediatorLiveData<Boolean>()

    fun getEmail(): MutableLiveData<String> = email
    fun getPassword(): MutableLiveData<String> = password
    fun getIsValid(): LiveData<Boolean> = isValid

    init {
        isValid.addSource(email) { checkValid() }
        isValid.addSource(password) { checkValid() }
    }

    private fun checkValid() {
        val valid = !email.value.isNullOrBlank() && !password.value.isNullOrBlank()
        isValid.value = valid
    }
}