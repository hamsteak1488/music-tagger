package com.cookandroid.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.myapplication.databinding.TagViewBinding
import com.tftf.util.MusicTag

class TagAdapter(private val tag: MusicTag)
    : RecyclerView.Adapter<TagAdapter.TagHolder>() {

    val tagList:ArrayList<CharSequence> = tag.toList()

    ///뮤직 뷰 binding
    class TagHolder(binding: TagViewBinding) : RecyclerView.ViewHolder(binding.root) {
//        val tagTV = binding.tagTextView
        val tagTV = binding.tagEmojiView
    }

    override fun getItemCount(): Int {
//        return tag.size
        return 2
    }

    //뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagHolder {
        return TagHolder(TagViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    ///뷰 홀더에 내용 입력
    override fun onBindViewHolder(holder: TagHolder, pos: Int) {
//        TODO(날씨, 시간 태그만 반영하여 이모지 적용)
//        holder.tagTV.text = tagList[pos]
    }
}