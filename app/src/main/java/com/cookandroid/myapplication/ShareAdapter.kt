package com.cookandroid.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.databinding.ShareViewBinding
import com.tftf.util.PlaylistForShare

class ShareAdapter(private val context: Context,
                   private var sharedLists: ArrayList<PlaylistForShare>,
                   private val downloadClickListener: OnItemClickListener? = null,
                   )

    : RecyclerView.Adapter<ShareAdapter.SharedListHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view: View, pos:Int)
    }

    class SharedListHolder(binding: ShareViewBinding): RecyclerView.ViewHolder(binding.root){
        val image = binding.imageSV
        val sharedListName = binding.playlistNameSV
        val userName = binding.uploaderNameSV
        val downloadBtn = binding.downloadSV
        val likeCount = binding.likeCountSV
        val downloadCount = binding.downloadCountSV
        val description = binding.descriptionSV
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedListHolder {
        return SharedListHolder(ShareViewBinding.inflate(LayoutInflater.from(context), parent,false))
    }

    override fun getItemCount(): Int {
        return sharedLists.size
    }

    override fun onBindViewHolder(holder: SharedListHolder, pos: Int) {
        //TODO SharedPlaylist 클래스를 새로 만들지? 아니면 기존 Playlist에 UserName, PlaylistName, Description을 추가할지?
        holder.sharedListName.text = sharedLists[pos].name
        holder.userName.text = sharedLists[pos].email
        holder.likeCount.text = "Like : " + sharedLists[pos].likeCount
        holder.downloadCount.text = "Downloaded : " + sharedLists[pos].copyCount
        holder.description.text = sharedLists[pos].description

        if(sharedLists[pos].musicList.isNotEmpty()){
            Glide.with(context)
                .load("http://10.0.2.2:8080/img?id=" + sharedLists[pos].musicList[0])
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
                .into(holder.image)
        }

        holder.downloadBtn.setOnClickListener {
            downloadClickListener?.onItemClick(it, pos)
        }
    }
}