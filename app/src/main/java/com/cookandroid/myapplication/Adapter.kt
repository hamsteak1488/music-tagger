package com.cookandroid.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.Adapter.MyHolder
import com.cookandroid.myapplication.databinding.MusicViewBinding

class Adapter(private val context: Context, private var musicList: ArrayList<Music>, private val playlistDetails: Boolean = false,
              private val searchActivity: Boolean = false)
    : RecyclerView.Adapter<MyHolder>() {

    ///뮤직 뷰 binding
    class MyHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val artist = binding.artistMV
        val image = binding.imageMV
        val duration = binding.songDuration
        val root = binding.root
        val option = binding.optionMV
    }

    //뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }
    ///뷰 홀더에 내용 입력
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.artist.text = musicList[position].artist
        holder.duration.text = formatDuration(musicList[position].duration)
        //glide = uri로 이미지 적용
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
            .into(holder.image)
        holder.root.setOnClickListener {
            val intent = Intent(context, PlayMusicActivity::class.java)
            intent.putExtra("index", position)
            intent.putExtra("class", "MusicAdapter")
            ContextCompat.startActivity(context, intent, null)
        }
        //롱클릭 동작으로 음악 추가 수행
        holder.root.setOnLongClickListener{

            return@setOnLongClickListener true
        }

        //페이지별 음악 클릭 동작
        when{
            playlistDetails ->{
                holder.root.setOnClickListener {
                    sendIntent(ref = "PlaylistDetailsAdapter", pos = position)
                }
            }
            searchActivity ->{
                holder.root.setOnClickListener {
                    if(addSong(musicList[position]))
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.customBlue))
                    else
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

                }
            }/*
            else ->{
                holder.root.setOnClickListener {

                    when{
                        MainActivity.search -> sendIntent(ref = "MusicAdapterSearch", pos = position)
                        musicList[position].id == PlayerActivity.nowPlayingId ->
                            sendIntent(ref = "NowPlaying", pos = PlayerActivity.songPosition)
                        else->sendIntent(ref="MusicAdapter", pos = position) } }
            }*/
        }
        
        /*//더보기 옵션 이벤트
        holder.option.setOnClickListener {
            PopupMenu popup = new PopupMenu(mCtx, holder.option)
        }*/

    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    //검색한 음악 리스트로 초기화
    fun updateMusicList(searchList: ArrayList<Music>){
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun sendIntent(ref: String, pos: Int){
        val intent = Intent(context, PlayMusicActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }

    private fun addSong(song: Music): Boolean{
        PlaylistManager.allPlayList[PlaylistDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if(song.title == music.title){
                PlaylistManager.allPlayList[PlaylistDetails.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        PlaylistManager.allPlayList[PlaylistDetails.currentPlaylistPos].playlist.add(song)
        return true
    }

    fun refreshPlaylist(){
        musicList = ArrayList()
        musicList = PlaylistManager.allPlayList[PlaylistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged() //리스트의 크기와 아이템이 둘 다 변경되는 경우 사용
    }

}