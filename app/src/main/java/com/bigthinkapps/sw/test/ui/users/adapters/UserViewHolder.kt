package com.bigthinkapps.sw.test.ui.users.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bigthinkapps.sw.test.databinding.UserListItemBinding
import com.bigthinkapps.sw.test.models.User
import com.bumptech.glide.Glide

class UserViewHolder(private val binding: UserListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(user: User): View {
        Glide.with(itemView).load(user.tinyPicture).into(binding.picture)
        binding.model = user

        return itemView
    }
}