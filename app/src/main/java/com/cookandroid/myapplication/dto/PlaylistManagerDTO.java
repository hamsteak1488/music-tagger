package com.cookandroid.myapplication.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PlaylistManagerDTO {
    String email;
    JsonArray musicIdList;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public JsonArray getMusicIdList() {
        return musicIdList;
    }

    public void setMusicIdList(JsonArray musicIdList) {
        this.musicIdList = musicIdList;
    }

    public PlaylistManagerDTO(String email, JsonArray musicIdList) {
        this.email = email;
        this.musicIdList = musicIdList;
    }
}
