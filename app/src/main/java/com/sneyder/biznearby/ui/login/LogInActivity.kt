package com.sneyder.biznearby.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.sneyder.biznearby.R
import com.sneyder.biznearby.ui.signup.SignUpActivity
import com.sneyder.biznearby.utils.base.DaggerActivity
import kotlinx.android.synthetic.main.activity_log_in.*

class LogInActivity : DaggerActivity() {

    companion object {

        fun starterIntent(context: Context): Intent {
            val starter = Intent(context, LogInActivity::class.java)
            //starter.putExtra(EXTRA_, )
            return starter
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        registerTextView.setOnClickListener {
            startActivity(SignUpActivity.starterIntent(this))
            finish()
        }
    }
}