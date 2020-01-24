package de.mloy.archeology.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import de.mloy.archeology.BaseActivity
import de.mloy.archeology.R
import de.mloy.archeology.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    companion object {
        fun create(ctx: Context): Intent {
            return Intent(ctx, LoginActivity::class.java)
        }
    }

    lateinit var binding: ActivityLoginBinding
        private set

    lateinit var presenter: LoginPresenter
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = LoginPresenter(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this
        binding.presenter = presenter

        setupUI()
    }
}