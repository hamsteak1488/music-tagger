package com.cookandroid.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cookandroid.myapplication.databinding.TagViewBinding
import com.tftf.util.MusicTag

class TagAdapter(private val tag: MusicTag)
    : RecyclerView.Adapter<TagAdapter.TagHolder>() {

    val tagList = tag.toList()

    ///뮤직 뷰 binding
    class TagHolder(binding: TagViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val tagEV = binding.tagEmojiView
        val root = binding.root
    }

    override fun getItemCount(): Int {
        return tag.size
    }

    //뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagHolder {
        return TagHolder(TagViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    ///뷰 홀더에 내용 입력
    override fun onBindViewHolder(holder: TagHolder, pos: Int) {

        var imgSrc = R.drawable.logo_white
        when (tagList[pos].first) {
            "시간" -> {
                imgSrc = when (tagList[pos].second) {
                    "새벽", "이른 아침", "늦은 아침" -> R.drawable.morining
                    "이른 오후", "늦은 오후" -> R.drawable.afternoon
                    "저녁" -> R.drawable.evening
                    "밤" -> R.drawable.night
                    else ->R.drawable.logo_white
                }
            }
            "날씨" -> {
                imgSrc = when (tagList[pos].second) {
                    "맑음" -> R.drawable.sun
                    "구름", "구름 흩뿌려짐", "구름 많이 낌", "안개" -> R.drawable.cloudy
                    "소나기", "비" -> R.drawable.rain
                    "천둥" -> R.drawable.thunder
                    "눈" -> R.drawable.snow
                    "예외 날씨" -> R.drawable.exept
                    else -> R.drawable.logo_white
                }
            }
            else -> {
                holder.root.visibility = View.GONE
            }
        }
        holder.tagEV.setImageResource(imgSrc)
    }
}