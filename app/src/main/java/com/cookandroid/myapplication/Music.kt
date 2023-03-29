package com.cookandroid.myapplication

import android.content.Context
import android.graphics.Color
import android.media.MediaMetadataRetriever
import androidx.appcompat.app.AlertDialog
import com.cookandroid.myapplication.dto.PlaylistManagerDTO
import com.google.android.material.color.MaterialColors
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

///데이터 클래스(Music, Playlist, MusicPlaylist)
data class Music(val id:Int, val title:String, val album:String, val artist:String,
                 val duration:Long=0, val path: String, val artUri:String)

//플레이리스트
class Playlist() {
    var name: String//플레이리스트 명
    var musicList: ArrayList<Int> //포함된 음악 ArrayList
    var artUri: String //테마 이미지

    init {
        name = ""
        musicList = ArrayList()
        artUri = ""
    }
}

object PlaylistManager {
    var playlists: ArrayList<Playlist> = ArrayList() //플레이리스트의 리스트

    fun toDto(email:String) : PlaylistManagerDTO {
        val parser = JsonParser()

        return PlaylistManagerDTO(email, Gson().toJsonTree(playlists) as JsonArray)
    }

    fun set(dto: PlaylistManagerDTO) {
        val gson = Gson()

        playlists = gson.fromJson(dto.musicIdList, Array<Playlist>::class.java).toCollection(ArrayList())

        /*
        val lsKeys = dto.musicIdList.keySet().iterator()
        while (lsKeys.hasNext()) {
            val ls = Playlist()
            val lsKey = lsKeys.next()
            val lsJo = dto.musicIdList.getAsJsonObject(lsKey)
            ls.name = lsJo.get("name").toString()
            ls.artUri = lsJo.get("artUri").toString()

            ls.musicList = gson.fromJson(lsJo.get("musicList"), Array<Playlist>::class.java)
        }
        */
    }
}



fun formatDuration(duration: Long):String{
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            minutes* TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%2d:%2d", minutes, seconds)
}


fun getImgArt(path: String): ByteArray?{
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
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