package com.example.list_user

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etEmail = findViewById(R.id.etEmail)

        findViewById<android.widget.ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<com.google.android.material.button.MaterialButton>(R.id.btnEnviar)
            .setOnClickListener { enviarCorreo() }
        findViewById<android.widget.TextView>(R.id.tvVolverLogin).setOnClickListener { finish() }
    }

    private fun enviarCorreo() {
        val email = etEmail.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                findViewById<android.widget.TextView>(R.id.tvExito).visibility = View.VISIBLE
                findViewById<android.widget.TextView>(R.id.tvVolverLogin).visibility = View.VISIBLE
                findViewById<com.google.android.material.button.MaterialButton>(R.id.btnEnviar).isEnabled = false
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
