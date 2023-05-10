package com.cookandroid.myapplication

import com.tftf.util.Playlist
import java.util.concurrent.TimeUnit

object PlaylistManager {

    var exploringListPos:Int = -1

    // var playlists: ArrayList<Playlist> = ArrayList() //플레이리스트의 리스트

    lateinit var playlistInUse:Playlist;


    init {
        initTempPlaylist()
    }

    fun initTempPlaylist() {
        playlistInUse = Playlist("user1", "playlist1", "no playlists are in use", ArrayList());

        /*
        if (playlists.size == 0) {
            playlists.add(Playlist("no playlists are in use", ArrayList()))
        }
        else if (playlists[0].name.compareTo("tempPlaylist") != 0) {
            playlists.add(0, Playlist("tempPlaylist", ArrayList()))
        }
        else {
            playlists[0].musicList.clear()
        }
        */
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