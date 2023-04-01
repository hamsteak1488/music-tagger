package com.cookandroid.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.activities.PlaylistDetails
import com.cookandroid.myapplication.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlaylistViewAdapter(private val context: Context, private var playlistList: ArrayList<Playlist>)
    : RecyclerView.Adapter<PlaylistViewAdapter.MyHolder>() {

    class MyHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImg
        val name = binding.playlistName
        val songCnt = binding.totalSongsPV
        val root = binding.root
        val delete = binding.playlistDeleteBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    // TODO : 이벤트 처리 전부 외부로 빼놓기
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = playlistList[position].name
        holder.name.isSelected = true
        holder.songCnt.text = playlistList[position].musicList.size.toString() + " songs"
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistList[position].name)
                .setMessage("delete playlist?")
                .setPositiveButton("Yes") { dialog, _ ->
                    PlaylistManager.playlists.removeAt(position)
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
            val intent = Intent(context, PlaylistDetails::class.java)
            intent.putExtra("index", position)
            ContextCompat.startActivity(context, intent, null)
        }
        if(PlaylistManager.playlists[position].musicList.isNotEmpty()){
            Glide.with(context)
                .load("http://10.0.2.2:8080/img?id=" + (PlaylistManager.playlists[position].musicList[0]))
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
                .into(holder.image)
        }
    }
    override fun getItemCount(): Int {
        return playlistList.size
    }
    ///플레이리스트 목록 갱신
    fun refreshPlaylist(){
        playlistList = ArrayList()
        playlistList.addAll(PlaylistManager.playlists)
        notifyDataSetChanged()
    }
}