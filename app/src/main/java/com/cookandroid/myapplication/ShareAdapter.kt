package com.cookandroid.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.databinding.ShareViewBinding
import com.tftf.util.Playlist

class ShareAdapter(private val context: Context, private var sharedlists: ArrayList<Playlist>): RecyclerView.Adapter<ShareAdapter.SharedlistHolder>() {

    class SharedlistHolder(binding: ShareViewBinding): RecyclerView.ViewHolder(binding.root){
        val image = binding.imageSV
        val sharedlistName = binding.playlistNameSV
        val userName = binding.uploaderNameSV
        val downloadBtn = binding.downloadSV
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedlistHolder {
        return SharedlistHolder(ShareViewBinding.inflate(LayoutInflater.from(context), parent,false))
    }

    override fun getItemCount(): Int {
        return sharedlists.size
    }

    override fun onBindViewHolder(holder: SharedlistHolder, position: Int) {
        //TODO SharedPlaylist 클래스를 새로 만들지? 아니면 기존 Playlist에 UserName, PlaylistName, Description을 추가할지?
        holder.sharedlistName.text = sharedlists[position].name
//        holder.userName.text = sharedlists[position].userName
//        holder.artists.text = 플레이리스트 position 별 artist 표시
        if(PlaylistManager.playlists[position].musicList.isNotEmpty()){
            Glide.with(context)
                .load("http://10.0.2.2:8080/img?id=" + (PlaylistManager.playlists[position].musicList[0]))
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
                .into(holder.image)
        }

        holder.downloadBtn.setOnClickListener{
            //TODO 선택한 플레이리스트 다운로드 -> Playlist에 추가
        }
    }
}