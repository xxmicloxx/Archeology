package de.mloy.archeology.ui.settings

import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import de.mloy.archeology.R
import de.mloy.archeology.getViewModel

class SettingsPresenter(private val activity: SettingsActivity) {
    val vm: SettingsViewModel = activity.getViewModel()

    fun changePassword() {
        val pw1 = vm.getNewPassword().value!!
        val pw2 = vm.getNewPasswordRetyped().value!!

        if (pw1 != pw2) {
            AlertDialog.Builder(activity)
                .setMessage(R.string.passwords_dont_match)
                .setPositiveButton(android.R.string.ok, null)
                .show()

            return
        }

        if (pw1.isBlank()) {
            AlertDialog.Builder(activity)
                .setMessage(R.string.password_must_not_be_empty)
                .setPositiveButton(android.R.string.ok, null)
                .show()

            return
        }

        vm.getNewPassword().value = ""
        vm.getNewPasswordRetyped().value = ""

        Snackbar
            .make(activity.binding.passwordEdit, R.string.password_changed, Snackbar.LENGTH_LONG)
            .show()
    }
}