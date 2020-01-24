package de.mloy.archeology.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import de.mloy.archeology.BaseActivity
import de.mloy.archeology.R
import de.mloy.archeology.databinding.ActivitySettingsBinding

class SettingsActivity : BaseActivity() {

    companion object {
        fun create(ctx: Context): Intent {
            return Intent(ctx, SettingsActivity::class.java)
        }
    }

    lateinit var binding: ActivitySettingsBinding
        private set

    lateinit var presenter: SettingsPresenter
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SettingsPresenter(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.lifecycleOwner = this
        binding.presenter = presenter

        setupUI(enableUp = true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}