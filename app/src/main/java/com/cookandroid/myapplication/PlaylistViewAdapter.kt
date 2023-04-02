package com.cookandroid.myapplication

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistViewAdapter(private val context: Context, private var playlists: ArrayList<Playlist>, private val listener:OnItemClickListener? = null)
    : RecyclerView.Adapter<PlaylistViewAdapter.PlaylistHolder>() {

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

    override fun onBindViewHolder(holder: PlaylistHolder, position: Int) {
        holder.name.text = playlists[position].name
        holder.name.isSelected = true
        holder.songCnt.text = playlists[position].musicList.size.toString() + " songs"
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlists[position].name)
                .setMessage("delete playlist?")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlaylistManager.playlists.removeAt(position)
                    MusicServiceConnection.musicService!!.savePlaylistManager(MusicServiceConnection.musicService!!.email) { }
                    refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog,_ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
        holder.root.setOnClickListener{
            if (listener != null) {
                listener.onItemClick(it, position)
            }

        }
        if(PlaylistManager.playlists[position].musicList.isNotEmpty()){
            Glide.with(context)
                .load("http://10.0.2.2:8080/img?id=" + (PlaylistManager.playlists[position].musicList[0]))
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
                .into(holder.image)
        }
    }
    override fun getItemCount(): Int {
        return playlists.size
    }
    ///플레이리스트 목록 갱신
    fun refreshPlaylist(){
        playlists = ArrayList()
        playlists.addAll(PlaylistManager.playlists)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, pos:Int)
    }
}