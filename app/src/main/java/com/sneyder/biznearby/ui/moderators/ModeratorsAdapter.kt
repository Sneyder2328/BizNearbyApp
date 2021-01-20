package com.sneyder.biznearby.ui.moderators

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sneyder.biznearby.R
import com.sneyder.biznearby.data.model.user.UserProfile
import kotlinx.android.synthetic.main.fragment_moderators_item.view.*

class ModeratorsAdapter() : RecyclerView.Adapter<ModeratorsAdapter.ModeratorViewHolder>() {

    var moderators: List<UserProfile> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModeratorViewHolder {
        return ModeratorViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_moderators_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ModeratorViewHolder, position: Int) {
        holder.bind(moderators[position])
    }

    override fun getItemCount(): Int = moderators.count()

    inner class ModeratorViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(profile: UserProfile) {
            Glide.with(view).load(profile.thumbnailUrl).centerCrop().into(view.profileImageView)
            view.fullnameTextView.text = profile.fullname
        }

    }
}