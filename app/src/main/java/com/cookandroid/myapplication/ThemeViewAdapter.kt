package com.cookandroid.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.activities.MainActivity
import com.cookandroid.myapplication.activities.ThemePlaylistActivity
import com.cookandroid.myapplication.databinding.ThemeViewBinding

class ThemeViewAdapter (private val context: Context, private var themelistList: ArrayList<Playlist>)
    : RecyclerView.Adapter<ThemeViewAdapter.MyHolder>(){

    class MyHolder(binding: ThemeViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.themeImg
        val name = binding.themeName
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ThemeViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = themelistList[position].name
        holder.name.isSelected = true
        holder.root.setOnClickListener {
            val intent = Intent(context, ThemePlaylistActivity::class.java)
            intent.putExtra("index", position)
            ContextCompat.startActivity(context, intent, null)
        }
        Glide.with(context)
            .load(MainActivity.allThemePlaylist[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.spring).centerCrop())
            .into(holder.image)

    }

    override fun getItemCount(): Int {
        return themelistList.size
    }
}