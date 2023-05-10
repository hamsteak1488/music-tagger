package com.cookandroid.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.MusicServiceConnection.serverUrl
import com.cookandroid.myapplication.PlaylistManager.formatDuration
import com.cookandroid.myapplication.R
import com.cookandroid.myapplication.databinding.RankMusicViewBinding
import com.tftf.util.Music
import com.tftf.util.MusicTag
import com.cookandroid.myapplication.adapters.TopRankAdapter.MusicHolder

class TopRankAdapter(private val context: Context,
                     private val musicList: ArrayList<Music>,
                     private val tagList: ArrayList<MusicTag>? = null,
                     private val itemClickListener: OnItemClickListener? = null
)
    : RecyclerView.Adapter<MusicHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view:View, pos:Int)
    }

    ///뮤직 뷰 binding
    class MusicHolder(binding: RankMusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val artist = binding.songArtistMV
        val image = binding.imageMV
        val duration = binding.songDuration
        val rv = binding.musicTagRV
        val root = binding.root
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    //뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        return MusicHolder(RankMusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    ///뷰 홀더에 내용 입력
    override fun onBindViewHolder(holder: MusicHolder, pos: Int) {

        holder.title.text = musicList[pos].title
        holder.artist.text = musicList[pos].artist
        holder.duration.text = formatDuration(musicList[pos].duration)

        //glide = uri로 이미지 적용
        Glide.with(context)
            .load(serverUrl + "img?id=" + (musicList[pos].id))
            .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
            .into(holder.image)

        if (tagList != null) {
            holder.rv.adapter = TagAdapter(tagList[pos])
        }

        holder.root.setOnClickListener {
            itemClickListener?.onItemClick(it, pos)
        }
    }
}