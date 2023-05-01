package com.cookandroid.myapplication

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.tftf.util.Playlist
import com.tftf.util.PlaylistManagerDTO

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

    fun toDto(email:String) : PlaylistManagerDTO {
        return PlaylistManagerDTO(email, Gson().toJsonTree(playlists) as JsonArray)
    }

    fun set(dto: PlaylistManagerDTO) {
        playlists = Gson().fromJson(dto.musicIdList, Array<Playlist>::class.java).toCollection(ArrayList())
    }
}