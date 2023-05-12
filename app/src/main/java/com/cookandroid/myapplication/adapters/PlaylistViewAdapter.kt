package com.cookandroid.myapplication.adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.MusicServiceConnection
import com.cookandroid.myapplication.MusicServiceConnection.serverUrl
import com.cookandroid.myapplication.PlaylistManager
import com.cookandroid.myapplication.R
import com.cookandroid.myapplication.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tftf.util.Playlist

class PlaylistViewAdapter(
    private val context: Context, private var listOfPlaylist: List<Playlist>,
    private val itemClickListener: OnItemClickListener,
    private val deleteClickListener: OnItemClickListener? = null
)
    : RecyclerView.Adapter<PlaylistViewAdapter.PlaylistHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view: View, pos:Int)
    }

    class PlaylistHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImg
        val name = binding.playlistName
        val songCnt = binding.totalSongsPV
        val root = binding.root
        val delete = binding.playlistDeleteBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistHolder {
        return PlaylistHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: PlaylistHolder, pos: Int) {
        holder.name.text = listOfPlaylist[pos].name
        holder.name.isSelected = true
        holder.songCnt.text = listOfPlaylist[pos].musicIDList.size.toString() + " songs"
        holder.delete.setOnClickListener {
            deleteClickListener?.onItemClick(it, pos)
        }
        holder.root.setOnClickListener{
            itemClickListener.onItemClick(it, pos)

        }
        if(listOfPlaylist[pos].musicIDList.isNotEmpty()){
            Glide.with(context)
                .load(serverUrl + "img?id=" + (listOfPlaylist[pos].musicIDList[0]))
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
                .into(holder.image)
        }
    }
    override fun getItemCount(): Int {
        return listOfPlaylist.size
    }

    /*
    ///플레이리스트 목록 갱신
    fun refreshPlaylist(){
        playlists = ArrayList()
        playlists.addAll(PlaylistManager.playlists)
        notifyDataSetChanged()
    }
    */
}