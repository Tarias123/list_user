package com.example.list_user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.list_user.R
import com.example.list_user.model.User
import com.squareup.picasso.Picasso

class UserAdapter(private val users: List<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFoto: ImageView = itemView.findViewById(R.id.ivFoto)
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.tvNombre.text = user.nombre
        holder.tvEmail.text = user.email

        if (user.fotoUrl.isNotEmpty()) {
            Picasso.get()
                .load(user.fotoUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.ivFoto)
        } else {
            holder.ivFoto.setImageResource(R.drawable.ic_launcher_foreground)
        }
    }

    override fun getItemCount(): Int = users.size
}
