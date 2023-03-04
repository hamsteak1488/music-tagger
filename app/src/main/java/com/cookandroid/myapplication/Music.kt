package com.cookandroid.myapplication

import android.content.Context
import android.graphics.Color
import android.media.MediaMetadataRetriever
import androidx.appcompat.app.AlertDialog
import com.google.android.material.color.MaterialColors
import java.util.concurrent.TimeUnit

///데이터 클래스(Music, Playlist, MusicPlaylist)
data class Music(val title:String, val album:String, val artist:String,
                 val duration:Long=0, val path: String, val artUri:String)

//플레이리스트
class Playlist{
    lateinit var name: String //플레이리스트 명
    lateinit var playlist: ArrayList<Music> //포함된 음악 ArrayList
    lateinit var artUri: String //테마 이미지
}

class AllPlaylist {
    var ref: ArrayList<Playlist> = ArrayList() //플레이리스트의 리스트
}

fun formatDuration(duration: Long):String{
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            minutes* TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%2d:%2d", minutes, seconds)
}

///Dialog 버튼 컬러
fun setDialogBtnBackground(context: Context, dialog: AlertDialog){
    dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setBackgroundColor(
        MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
    )
    dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setBackgroundColor(
        MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
    )
}