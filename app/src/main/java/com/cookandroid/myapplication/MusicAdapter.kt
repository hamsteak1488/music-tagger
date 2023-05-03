package com.cookandroid.myapplication

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemLongClickListener
import androidx.core.util.TimeUtils.formatDuration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.MusicAdapter.MusicHolder
import com.cookandroid.myapplication.MusicServiceConnection.serverUrl
import com.cookandroid.myapplication.databinding.MusicViewBinding
import com.tftf.util.Music
import com.tftf.util.MusicTag

class MusicAdapter(private val context: Context,
                   private val musicList: ArrayList<Music>,
                   private val tagList: ArrayList<MusicTag>? = null,
                   private val itemClickListener: OnItemClickListener? = null,
                   private val itemOptionClickListener: OnItemClickListener? = null,
                   private val itemLongClickListener: OnItemLongClickListener? = null,
                   private val itemCheckedChangeListener : OnItemCheckedChangeListener? = null,
                   private val selectionMode:Boolean = false
)
    : RecyclerView.Adapter<MusicHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view:View, pos:Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(view:View, pos:Int)
    }

    interface OnItemCheckedChangeListener {
        fun onItemCheckedChange(isChecked:Boolean, pos:Int)
    }

    ///뮤직 뷰 binding
    class MusicHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val artist = binding.songArtistMV
        val image = binding.imageMV
        val duration = binding.songDuration
        val option = binding.optionMV
        val rv = binding.musicTagRV
        val checkbox = binding.checkBox
        val root = binding.root
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    //뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicHolder {
        return MusicHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    ///뷰 홀더에 내용 입력
    override fun onBindViewHolder(holder: MusicHolder, pos: Int) {

        holder.title.text = musicList[pos].title
        holder.artist.text = musicList[pos].artist
        holder.duration.text = PlaylistManager.formatDuration(musicList[pos].duration)

        //glide = uri로 이미지 적용
        Glide.with(context)
            .load(serverUrl + "img?id=" + (musicList[pos].id))
            .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
            .into(holder.image)

        if (tagList != null) {
            holder.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.rv.adapter = TagAdapter(tagList[pos])
        }

        holder.root.setOnClickListener {
            itemClickListener?.onItemClick(it, pos)
        }

        holder.option.setOnClickListener {
            itemOptionClickListener?.onItemClick(it, pos)
        }

        holder.root.setOnLongClickListener {
            itemLongClickListener?.onItemLongClick(it, pos)
            true
        }

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            itemCheckedChangeListener?.onItemCheckedChange(isChecked, pos)
        }

        holder.checkbox.visibility = when (selectionMode) {
            true -> View.VISIBLE
            false -> View.GONE
        }
    }
}