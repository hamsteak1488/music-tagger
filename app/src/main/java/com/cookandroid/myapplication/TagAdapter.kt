package com.cookandroid.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.myapplication.databinding.TagViewBinding
import com.tftf.util.MusicTag

// todo : 음악 태그들을 골랐을 때, 표시할 레이아웃 찾고 없으면 만들기

class TagAdapter(private val tagList: MusicTag)
    : RecyclerView.Adapter<TagAdapter.TagHolder>() {

    ///뮤직 뷰 binding
    class TagHolder(binding: TagViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val tagTV = binding.tagTextView
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    //뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagHolder {
        return TagHolder(TagViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    ///뷰 홀더에 내용 입력
    override fun onBindViewHolder(holder: TagHolder, pos: Int) {
        holder.tagTV.text = tagList.at(pos)
    }
}