package de.mloy.archeology.ui.login

import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import de.mloy.archeology.R
import de.mloy.archeology.getViewModel
import de.mloy.archeology.model.firebase.SiteFirebaseStore
import de.mloy.archeology.ui.sitelist.SiteListActivity

class LoginPresenter(private val activity: LoginActivity) {
    val vm: LoginViewModel = activity.getViewModel()
    private val auth = FirebaseAuth.getInstance()

    fun register() {
        val email = vm.getEmail().value!!
        val password = vm.getPassword().value!!

        vm.getIsLoading().value = true

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { res ->
                onRegisterComplete(res)
            }
    }

    private fun onRegisterComplete(res: Task<AuthResult>) {
        if (res.isSuccessful) {
            val fireStore = vm.siteStore as? SiteFirebaseStore
            if (fireStore == null) {
                registerSuccess()
            } else {
                fireStore.fetchSites {
                    registerSuccess()
                }
            }
        } else {
            vm.getIsLoading().value = false

            AlertDialog.Builder(activity)
                .setMessage(R.string.auth_failed)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }

    private fun registerSuccess() {
        Snackbar.make(
            activity.binding.registerButton,
            R.string.register_success,
            Snackbar.LENGTH_LONG
        )
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    continueToApp()
                }
            })
            .show()
    }

    fun logIn() {
        val email = vm.getEmail().value!!
        val password = vm.getPassword().value!!

        vm.getIsLoading().value = true

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { res ->
                onLoginComplete(res)
            }
    }

    private fun onLoginComplete(res: Task<AuthResult>) {
        if (res.isSuccessful) {
            // load data
            val fireStore = vm.siteStore as? SiteFirebaseStore
            if (fireStore == null) {
                continueToApp()
            } else {
                // load data
                fireStore.fetchSites {
                    continueToApp()
                }
            }

        } else {
            vm.getIsLoading().value = false

            AlertDialog.Builder(activity)
                .setMessage(R.string.auth_failed)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }

    fun continueToApp() {
        val intent = SiteListActivity.create(activity)
        activity.startActivity(intent)
        activity.finish()
    }
}