package com.ipvc.maintrack

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var passwordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailField = findViewById<EditText>(R.id.email)
        val passwordField = findViewById<EditText>(R.id.password)
        val showPassword = findViewById<ImageView>(R.id.showPassword)
        val loginButton = findViewById<Button>(R.id.login_button)
        val registerButton = findViewById<Button>(R.id.register_button)

        // Toggle visibilidade da password
        showPassword.setOnClickListener {
            passwordVisible = !passwordVisible
            if (passwordVisible) {
                passwordField.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showPassword.setImageResource(R.drawable.ic_visibility) // mudar para ic_visibility_off quando tiver o icon
            } else {
                passwordField.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showPassword.setImageResource(R.drawable.ic_visibility)
            }
            passwordField.setSelection(passwordField.text.length) // manter cursor no fim
        }

        // Ação de login
        loginButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null && user.isEmailVerified) {
                            Toast.makeText(this, "Login com sucesso!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, GestorHomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(this, "Verifica o teu email antes de continuar", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // Verificar se o erro é relacionado à senha incorreta
                        val errorMessage = task.exception?.message
                        if (errorMessage != null && errorMessage.contains("auth credential")) {
                            Toast.makeText(this, "Senha incorreta. Tente novamente.", Toast.LENGTH_LONG).show()
                        } else {
                            // Se for qualquer outro erro, exibe a mensagem geral
                            Toast.makeText(this, "Erro: $errorMessage", Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }


        // Ir para ecrã de registo
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegistoAccActivity::class.java))
            finish()
        }
    }
}
