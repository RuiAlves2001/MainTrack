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
import com.google.firebase.firestore.FirebaseFirestore

class RegistoAccActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var passwordVisible = false
    private var confirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registo_acc)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val nameField = findViewById<EditText>(R.id.name)
        val emailField = findViewById<EditText>(R.id.email)
        val contactField = findViewById<EditText>(R.id.contact)
        val passwordField = findViewById<EditText>(R.id.password)
        val confirmPasswordField = findViewById<EditText>(R.id.passwordConfirm)

        val showPassword = findViewById<ImageView>(R.id.showPassword)
        val showConfirmPassword = findViewById<ImageView>(R.id.showPasswordConfirm)

        val registerButton = findViewById<Button>(R.id.register_button)
        val loginButton = findViewById<Button>(R.id.login_button)

        // Toggle visibilidade password
        showPassword.setOnClickListener {
            passwordVisible = !passwordVisible
            if (passwordVisible) {
                passwordField.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showPassword.setImageResource(R.drawable.ic_visibility)
            } else {
                passwordField.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showPassword.setImageResource(R.drawable.ic_visibility)
            }
            passwordField.setSelection(passwordField.text.length)
        }

        // Toggle visibilidade confirmação de password
        showConfirmPassword.setOnClickListener {
            confirmPasswordVisible = !confirmPasswordVisible
            if (confirmPasswordVisible) {
                confirmPasswordField.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showConfirmPassword.setImageResource(R.drawable.ic_visibility)
            } else {
                confirmPasswordField.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showConfirmPassword.setImageResource(R.drawable.ic_visibility)
            }
            confirmPasswordField.setSelection(confirmPasswordField.text.length)
        }

        // Botão registar
        registerButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val contact = contactField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || contact.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "As palavras-passe não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val passwordError = checkPasswordRequirements(password)
            if (passwordError != null) {
                Toast.makeText(this, passwordError, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Criar conta no Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Enviar email de verificação
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { emailTask ->
                                if (emailTask.isSuccessful) {
                                    Toast.makeText(this, "Conta criada. Verifica o teu email.", Toast.LENGTH_LONG).show()

                                    // Criar dados na Firestore com os campos 'role' e 'language'
                                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                                    val userData = hashMapOf(
                                        "name" to name,
                                        "email" to email,
                                        "contact" to contact,
                                        "role" to "utilizador",
                                        "language" to "PT",
                                    )

                                    // Salvar dados do utilizador na Firestore
                                    db.collection("users").document(userId)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            // Voltar para o login após registo
                                            auth.signOut()
                                            startActivity(Intent(this, LoginActivity::class.java))
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                } else {
                                    Toast.makeText(this, "Erro ao enviar email de verificação: ${emailTask.exception?.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Erro ao criar conta: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        // Ir para ecrã de login
        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    // Validação da password com mensagens específicas
    private fun checkPasswordRequirements(password: String): String? {
        if (password.length < 6)
            return "A palavra-passe deve ter pelo menos 6 caracteres"
        if (!password.any { it.isUpperCase() })
            return "A palavra-passe deve conter pelo menos uma letra maiúscula"
        if (!password.any { it.isLowerCase() })
            return "A palavra-passe deve conter pelo menos uma letra minúscula"
        if (!password.any { it.isDigit() })
            return "A palavra-passe deve conter pelo menos um número"
        if (!password.any { !it.isLetterOrDigit() })
            return "A palavra-passe deve conter pelo menos um símbolo (ex: @, #, !)"
        return null
    }
}
