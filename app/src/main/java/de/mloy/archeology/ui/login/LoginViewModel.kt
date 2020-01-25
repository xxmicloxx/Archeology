package de.mloy.archeology.ui.login

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.mloy.archeology.BaseViewModel

class LoginViewModel(app: Application) : BaseViewModel(app) {
    private val email = MutableLiveData<String>("")
    private val password = MutableLiveData<String>("")
    private val isLoading = MutableLiveData<Boolean>(false)

    private val isValid = MediatorLiveData<Boolean>()
    private val canClick = MediatorLiveData<Boolean>()


    fun getEmail(): MutableLiveData<String> = email
    fun getPassword(): MutableLiveData<String> = password
    fun getIsLoading(): MutableLiveData<Boolean> = isLoading

    fun getIsValid(): LiveData<Boolean> = isValid
    fun getCanClick(): LiveData<Boolean> = canClick

    init {
        isValid.addSource(email) { checkValid() }
        isValid.addSource(password) { checkValid() }

        canClick.addSource(isValid) { checkCanClick() }
        canClick.addSource(isLoading) { checkCanClick() }
    }

    private fun checkValid() {
        val valid = !email.value.isNullOrBlank() && !password.value.isNullOrBlank()
        isValid.value = valid
    }

    private fun checkCanClick() {
        canClick.value = isValid.value!! && !isLoading.value!!
    }
}