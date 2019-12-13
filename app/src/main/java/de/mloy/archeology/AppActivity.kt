package de.mloy.archeology

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

abstract class AppActivity : AppCompatActivity() {

    protected lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handler = Handler()
    }
}