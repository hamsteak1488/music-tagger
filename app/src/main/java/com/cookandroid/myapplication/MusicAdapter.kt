package com.cookandroid.myapplication

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemLongClickListener
import androidx.core.util.TimeUtils.formatDuration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.cookandroid.myapplication.MusicAdapter.MusicHolder
import com.cookandroid.myapplication.PlaylistManager.exploringListPos
import com.cookandroid.myapplication.databinding.MusicViewBinding
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
//        holder.duration.text = formatDuration(musicList[pos].duration)
        holder.duration.text = "12:34"

        //glide = uri로 이미지 적용
        Glide.with(context)
            .load("http://10.0.2.2:8080/img?id=" + (musicList[pos].id))
            .apply(RequestOptions().placeholder(R.drawable.ic_baseline_music_note_24).centerCrop())
            .into(holder.image)

        if (tagList != null) {
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
            true ->  View.VISIBLE
            false -> View.GONE
        }

        /*
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
            }
            else ->{
                holder.root.setOnClickListener {

                    when{
                        MainActivity.search -> sendIntent(ref = "MusicAdapterSearch", pos = position)
                        musicList[position].id == PlayerActivity.nowPlayingId ->
                            sendIntent(ref = "NowPlaying", pos = PlayerActivity.songPosition)
                        else->sendIntent(ref="MusicAdapter", pos = position) } }
            }
        }
        */
    }

    /*
    //검색한 음악 리스트로 초기화
    fun updateMusicList(searchList: ArrayList<Music>){
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }
    */

    /*
    private fun sendIntent(ref: String, pos: Int){
        val intent = Intent(context, PlayMusicActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }
    */

    /*
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
    */
}