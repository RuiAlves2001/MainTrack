package com.example.maintrack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            // Aqui colocarias a lógica real de login (verificação de credenciais, etc.)

            // Guardar que o utilizador está autenticado
            val sharedPref = getSharedPreferences("prefs", MODE_PRIVATE)
            sharedPref.edit().putBoolean("user_logged_in", true).apply()

            // Ir para o ecrã principal
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}
