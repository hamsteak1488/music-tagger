package com.cookandroid.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.R
import com.cookandroid.myapplication.SettingsManager.serverUrl
import com.cookandroid.myapplication.databinding.ShareViewBinding
import com.tftf.util.SharedPlaylist

class ShareAdapter(private val context: Context,
                   private var sharedLists: ArrayList<SharedPlaylist>,
                   private val downloadClickListener: OnDownloadClickListener? = null,
                   private val likeClickListener: OnLikeClickListener? = null
                   )

    : RecyclerView.Adapter<ShareAdapter.SharedListHolder>() {

    interface OnDownloadClickListener {
        fun onItemClick(textView: TextView, pos:Int)
    }

    interface OnLikeClickListener {
        fun onItemClick(imageView:ImageView, textView:TextView, pos:Int, checked:Boolean)
    }

    var likeChecked:Boolean = false

    class SharedListHolder(binding: ShareViewBinding): RecyclerView.ViewHolder(binding.root) {
        val image = binding.imageSV
        val sharedListName = binding.playlistNameSV
        val userName = binding.uploaderNameSV
        val likeCount = binding.likeCountSV
        val downloadCount = binding.downloadCountSV
        val description = binding.descriptionSV
        val likeBtn = binding.likeBtnSV
        val downloadBtn = binding.downloadBtnSV
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SharedListHolder {
        return SharedListHolder(ShareViewBinding.inflate(LayoutInflater.from(context), parent,false))
    }

    override fun getItemCount(): Int {
        return sharedLists.size
    }

    override fun onBindViewHolder(holder: SharedListHolder, pos: Int) {
        holder.sharedListName.text = sharedLists[pos].name
        holder.userName.text = sharedLists[pos].userID
        holder.likeCount.text = "Like : " + sharedLists[pos].likeCount
        holder.downloadCount.text = "Downloaded : " + sharedLists[pos].downloadCount
        holder.description.text = sharedLists[pos].description

        if(sharedLists[pos].musicIDList.isNotEmpty()){
            Glide.with(context)
                .load(serverUrl + "img?id=" + sharedLists[pos].musicIDList[0])
                .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
                .into(holder.image)
        }

        holder.downloadBtn.setOnClickListener {
            downloadClickListener?.onItemClick(holder.downloadCount, pos)
        }

        holder.likeBtn.setOnClickListener {
            likeChecked = likeChecked.xor(true)
            likeClickListener?.onItemClick(it as ImageView, holder.likeCount, pos, likeChecked)
        }
    }
}