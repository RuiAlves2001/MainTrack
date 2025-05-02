package com.example.maintrack

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("prefs", MODE_PRIVATE)
        val onboardingDone = sharedPref.getBoolean("onboarding_done", false)
        val userLoggedIn = sharedPref.getBoolean("user_logged_in", false)

        val nextActivity = when {
            !onboardingDone -> MainActivity::class.java
            !userLoggedIn -> LoginActivity::class.java
            else -> HomeActivity::class.java
        }

        startActivity(Intent(this, nextActivity))
        finish()
    }
}