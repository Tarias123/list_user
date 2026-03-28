package com.example.list_user

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.list_user.adapter.UserAdapter
import com.example.list_user.model.User
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MainActivity : AppCompatActivity() {

    private lateinit var rvUsuarios: RecyclerView
    private lateinit var adapter: UserAdapter
    private val users = mutableListOf<User>()
    private var listenerRegistration: ListenerRegistration? = null
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        rvUsuarios = findViewById(R.id.rvUsuarios)
        adapter = UserAdapter(users)
        rvUsuarios.layoutManager = LinearLayoutManager(this)
        rvUsuarios.adapter = adapter

        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        cargarUsuarios()
    }

    private fun cargarUsuarios() {
        listenerRegistration = db.collection("usuarios")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MainActivity", "Error al escuchar usuarios", error)
                    return@addSnapshotListener
                }
                users.clear()
                snapshot?.documents?.forEach { doc ->
                    val user = User(
                        id = doc.id,
                        nombre = doc.getString("nombre") ?: "",
                        email = doc.getString("email") ?: "",
                        fotoUrl = doc.getString("fotoUrl") ?: ""
                    )
                    users.add(user)
                }
                adapter.notifyDataSetChanged()
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }
}
