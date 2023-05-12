package com.cookandroid.myapplication

import androidx.annotation.RestrictTo
import com.tftf.util.Playlist
import java.util.concurrent.TimeUnit

object PlaylistManager {

    var exploringPlaylist:Playlist? = null

    // var playlists: ArrayList<Playlist> = ArrayList() //플레이리스트의 리스트

    var playlistInUse:Playlist? = null;


    init {
        initTempPlaylist()
    }

    private fun initTempPlaylist() {
        // playlistInUse = Playlist("user1", "playlist1", "no playlists are in use", ArrayList());

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

    fun addPlaylist(playlist: Playlist) {
        RetrofitManager.saveUserPlaylist(playlist) { }
    }

    fun savePlaylist(playlist: Playlist) {
        RetrofitManager.saveUserPlaylist(playlist) { }
    }

    fun removePlaylist(name: String) {
        RetrofitManager.deleteUserPlaylist(UserManager.userID, name) { }
    }

    fun addPlaylistItem(playlist: Playlist, musicIDList: List<Int>) {
        musicIDList.forEach { musicID ->
            playlist.musicIDList.add(musicID)
        }
        RetrofitManager.saveUserPlaylist(playlist) { }
    }

    fun removePlaylistItem(playlist: Playlist, musicIDList: List<Int>) {
        musicIDList.forEach { musicID ->
            playlist.musicIDList.remove(musicID)

            if (playlistInUse != null) {
                val mService = MusicServiceConnection.musicService!!
                if (exploringPlaylist == playlistInUse && playlistInUse!!.musicIDList[mService.currentMusicPos] == musicID) {
                    mService.currentMusicPos = -1
                }
            }
        }
        RetrofitManager.saveUserPlaylist(playlist) { }
    }

    fun formatDuration(duration: Long):String{
        val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
        val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
                minutes* TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
        return String.format("%2d:%2d", minutes, seconds)
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
}