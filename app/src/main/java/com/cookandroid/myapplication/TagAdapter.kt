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
//        TODO(날씨, 시간 태그만 반영하여 이모지 적용) // 날씨랑 시간
//        holder.tagTV.text = tagList[pos]
        when(tagList[pos]){
            "봄"->holder.tagTV.setImageResource(R.drawable.springtag)
            "겨울"->holder.tagTV.setImageResource(R.drawable.winter)
            "가을"->holder.tagTV.setImageResource(R.drawable.fall)
            "여름"->holder.tagTV.setImageResource(R.drawable.summer)
            "새벽", "이른 아침", "늦은 아침" ->holder.tagTV.setImageResource(R.drawable.morining)
            "이른 오후", "늦은 오후" -> holder.tagTV.setImageResource(R.drawable.afternoon)
            "저녁"->holder.tagTV.setImageResource(R.drawable.evening)
            "밤"->holder.tagTV.setImageResource(R.drawable.night)
        }


    }
}