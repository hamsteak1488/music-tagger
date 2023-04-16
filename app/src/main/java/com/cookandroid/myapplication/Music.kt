package com.cookandroid.myapplication

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AlertDialog
import com.cookandroid.myapplication.dto.PlaylistManagerDTO
import com.google.android.material.color.MaterialColors
import com.google.gson.Gson
import com.google.gson.JsonArray
import java.util.concurrent.TimeUnit

///데이터 클래스(Music, Playlist, MusicPlaylist)
data class Music(val id:Int, val title:String, val album:String, val artist:String,
                 val duration:Long=0, val path: String, val artUri:String)

//플레이리스트
class Playlist(var name:String, var musicList:ArrayList<Int>) {

}

object PlaylistManager {
    var playlists: ArrayList<Playlist> = ArrayList() //플레이리스트의 리스트

    init {
        playlists.add(Playlist("tempPlaylist", ArrayList()))
    }

    fun toDto(email:String) : PlaylistManagerDTO {
        return PlaylistManagerDTO(email, Gson().toJsonTree(playlists) as JsonArray)
    }

    fun set(dto: PlaylistManagerDTO) {
        playlists = Gson().fromJson(dto.musicIdList, Array<Playlist>::class.java).toCollection(ArrayList())
    }
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