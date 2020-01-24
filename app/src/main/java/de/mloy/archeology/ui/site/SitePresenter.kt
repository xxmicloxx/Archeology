package de.mloy.archeology.ui.site

import android.app.ActivityOptions
import android.transition.ChangeBounds
import androidx.appcompat.app.AlertDialog
import de.mloy.archeology.R
import de.mloy.archeology.getViewModel
import de.mloy.archeology.ui.editlocation.EditLocationActivity

class SitePresenter(private val activity: SiteActivity) {
    val vm: SiteViewModel = activity.getViewModel()

    fun editLocation() {
        var title = vm.getTitle().value!!
        if (title.isBlank()) {
            title = "Untitled Site"
        }

        val startLocation = vm.getLocation().value ?: return
        val intent = EditLocationActivity.create(activity, title, startLocation)

        val window = activity.window
        val transition: ActivityOptions
        if (startLocation.isValid) {
            transition = ActivityOptions
                .makeSceneTransitionAnimation(activity, activity.binding.mapLayout, "map")

            window.exitTransition = null
            window.reenterTransition = null
            window.sharedElementsUseOverlay = false
            window.sharedElementExitTransition = ChangeBounds()
            window.sharedElementReenterTransition = null
        } else {
            // start without shared element
            transition = ActivityOptions.makeSceneTransitionAnimation(activity)
            window.exitTransition = null
            window.reenterTransition = null
            window.sharedElementsUseOverlay = false
        }

        activity.startActivityForResult(
            intent,
            SiteActivity.LOCATION_REQUEST_CODE,
            transition.toBundle()
        )
    }

    fun onNotesPreChanged(s: CharSequence?) {
        if (s.isNullOrBlank()) {
            vm.setVisited()
        }
    }

    fun commitAndFinish() {
        if (vm.getTitle().value.isNullOrBlank()) {
            // error
            AlertDialog.Builder(activity)
                .setMessage(R.string.site_missing_title_message)
                .setPositiveButton(android.R.string.ok, null)
                .show()

            return
        }

        vm.commit()
        activity.finishAfterTransition()
    }

    fun deleteAndFinish() {
        vm.siteStore.delete(vm.site)
        activity.finishAfterTransition()
    }

    fun showDismissDialog() {
        AlertDialog.Builder(activity)
            .setMessage(R.string.site_discard_confirm_message)
            .setPositiveButton(R.string.discard) { _, _ ->
                activity.finishAfterTransition()
            }
            .setNegativeButton(R.string.continue_editing, null)
            .show()
    }
}