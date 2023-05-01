package com.cookandroid.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.activities.ThemePlaylistActivity
import com.cookandroid.myapplication.databinding.ThemeViewBinding
import com.tftf.util.Playlist

class ThemeViewAdapter (private val context: Context,
                        private var themelists: ArrayList<Playlist>,
                        private val itemClickListener: OnItemClickListener? = null)
    : RecyclerView.Adapter<ThemeViewAdapter.MyHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view: View, pos:Int)
    }

    class MyHolder(binding: ThemeViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.themeImg
        val name = binding.themeName
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ThemeViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, pos: Int) {
        holder.name.text = themelists[pos].name
        holder.name.isSelected = true
        holder.root.setOnClickListener {
            itemClickListener?.onItemClick(it, pos)
        }
        Glide.with(context)
            .load("http://10.0.2.2:8080/img?id=" + (themelists[pos].musicList[0]))
            .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_video_24).centerCrop())
            .into(holder.image)
    }

    override fun getItemCount(): Int {
        return themelists.size
    }
}