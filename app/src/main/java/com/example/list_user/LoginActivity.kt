package com.example.list_user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var callbackManager: CallbackManager
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tvErrorEmail: TextView
    private lateinit var tvErrorPassword: TextView
    private val db = FirebaseFirestore.getInstance()

    private val webClientId = "53346534125-77hr129p9rh28fan6os6ohbd604gj26n.apps.googleusercontent.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            irAMain()
            return
        }

        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()

        setContentView(R.layout.activity_login)

        credentialManager = CredentialManager.create(this)

        tilEmail = findViewById(R.id.tilEmail)
        tilPassword = findViewById(R.id.tilPassword)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        tvErrorEmail = findViewById(R.id.tvErrorEmail)
        tvErrorPassword = findViewById(R.id.tvErrorPassword)

        etEmail.doOnTextChanged { _, _, _, _ -> clearError(tilEmail, tvErrorEmail) }
        etPassword.doOnTextChanged { _, _, _, _ -> clearError(tilPassword, tvErrorPassword) }

        findViewById<MaterialButton>(R.id.btnLogin).setOnClickListener { loginConEmail() }
        findViewById<MaterialButton>(R.id.btnGoogle).setOnClickListener { loginConGoogle() }
        findViewById<MaterialButton>(R.id.btnFacebook).setOnClickListener { loginConFacebook() }
        findViewById<TextView>(R.id.tvRegistrarse).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        findViewById<TextView>(R.id.tvOlvidePassword).setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val token = result.accessToken.token
                    val credential = FacebookAuthProvider.getCredential(token)
                    auth.signInWithCredential(credential)
                        .addOnSuccessListener { authResult ->
                            val user = authResult.user ?: return@addOnSuccessListener
                            val datos = mapOf(
                                "nombre" to (user.displayName ?: ""),
                                "email" to (user.email ?: ""),
                                "fotoUrl" to (user.photoUrl?.toString() ?: "")
                            )
                            db.collection("usuarios").document(user.uid)
                                .set(datos, SetOptions.merge())
                                .addOnSuccessListener { irAMain() }
                                .addOnFailureListener { irAMain() }
                        }
                        .addOnFailureListener { e ->
                            Log.e("LoginActivity", "Error Facebook Firebase", e)
                            Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }

                override fun onCancel() {
                    Log.d("LoginActivity", "Facebook login cancelado")
                }

                override fun onError(error: FacebookException) {
                    Log.e("LoginActivity", "Error Facebook", error)
                    Toast.makeText(this@LoginActivity, "Error Facebook: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun loginConEmail() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        var hayError = false

        if (email.isEmpty()) {
            mostrarError(tilEmail, tvErrorEmail, etEmail, "Este campo es requerido")
            hayError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarError(tilEmail, tvErrorEmail, etEmail, "Formato de correo inválido")
            hayError = true
        }

        if (password.isEmpty()) {
            mostrarError(tilPassword, tvErrorPassword, etPassword, "Este campo es requerido")
            hayError = true
        }

        if (hayError) return

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { irAMain() }
            .addOnFailureListener { e ->
                Log.e("LoginActivity", "Error email login", e)
                when (e) {
                    is FirebaseAuthInvalidUserException ->
                        mostrarError(tilEmail, tvErrorEmail, etEmail, "Este correo no está registrado")
                    is FirebaseAuthInvalidCredentialsException ->
                        mostrarError(tilPassword, tvErrorPassword, etPassword, "Contraseña incorrecta")
                    else ->
                        mostrarError(tilPassword, tvErrorPassword, etPassword, "Error: ${e.message}")
                }
            }
    }

    private fun loginConFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(
            this,
            listOf("email", "public_profile")
        )
    }

    private fun mostrarError(til: TextInputLayout, tvError: TextView, campo: View, mensaje: String) {
        til.boxStrokeColor = getColor(android.R.color.holo_red_light)
        til.hintTextColor = android.content.res.ColorStateList.valueOf(
            getColor(android.R.color.holo_red_light)
        )
        tvError.text = mensaje
        tvError.visibility = View.VISIBLE
        campo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
    }

    private fun clearError(til: TextInputLayout, tvError: TextView) {
        til.boxStrokeColor = getColor(R.color.colorPrimary)
        til.hintTextColor = android.content.res.ColorStateList.valueOf(
            getColor(R.color.colorTextSecondary)
        )
        tvError.visibility = View.GONE
    }

    private fun loginConGoogle() {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(this@LoginActivity, request)
                val credential = result.credential

                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken.idToken, null)

                    auth.signInWithCredential(firebaseCredential)
                        .addOnSuccessListener { result ->
                            val user = result.user ?: return@addOnSuccessListener
                            val datos = mapOf(
                                "nombre" to (user.displayName ?: ""),
                                "email" to (user.email ?: ""),
                                "fotoUrl" to (user.photoUrl?.toString() ?: "")
                            )
                            db.collection("usuarios").document(user.uid)
                                .set(datos, SetOptions.merge())
                                .addOnSuccessListener { irAMain() }
                                .addOnFailureListener { irAMain() }
                        }
                        .addOnFailureListener { e ->
                            Log.e("LoginActivity", "Error Google Firebase", e)
                        }
                }
            } catch (e: GetCredentialException) {
                Log.e("LoginActivity", "Error Google Credential", e)
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun irAMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
