package com.cookandroid.myapplication

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.tftf.util.Playlist
import com.tftf.util.PlaylistForShare
import com.tftf.util.PlaylistForShareDTO
import com.tftf.util.PlaylistManagerDTO
import java.util.concurrent.TimeUnit

object PlaylistManager {

    var exploringListPos:Int = -1

    var playlists: ArrayList<Playlist> = ArrayList() //플레이리스트의 리스트


    init {
        initTempPlaylist()
    }

    fun initTempPlaylist() {
        if (playlists.size == 0) {
            playlists.add(Playlist("tempPlaylist", ArrayList()))
        }
        else if (playlists[0].name.compareTo("tempPlaylist") != 0) {
            playlists.add(0, Playlist("tempPlaylist", ArrayList()))
        }
        else {
            playlists[0].musicList.clear()
        }
    }

    /*
    fun toDto(email:String) : PlaylistManagerDTO {
        return PlaylistManagerDTO(email, Gson().toJsonTree(playlists) as JsonArray)
    }

    fun set(dto: PlaylistManagerDTO) {
        playlists = Gson().fromJson(dto.musicIdList, Array<Playlist>::class.java).toCollection(ArrayList())
    }


    fun createPlaylistForShareToDto(playlistForShare: PlaylistForShare): PlaylistForShareDTO {
        return PlaylistForShareDTO(
            playlistForShare.name,
            Gson().toJsonTree(playlistForShare.musicList) as JsonArray,
            playlistForShare.email,
            playlistForShare.description,
            playlistForShare.likeCount,
            playlistForShare.copyCount
        )
    }

    fun getPlaylistForShareFromDto(dto: PlaylistForShareDTO) : PlaylistForShare {
        return PlaylistForShare(
            dto.name,
            Gson().fromJson(dto.musicListJson, Array<Int>::class.java).toCollection(ArrayList()),
            dto.email,
            dto.description,
            dto.likeCount,
            dto.copyCount
        )
    }
    */

    fun formatDuration(duration: Long):String{
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
                minutes* TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%2d:%2d", minutes, seconds)
    }
}