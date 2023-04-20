package com.cookandroid.myapplication

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.tftf.util.PlaylistManagerDTO

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