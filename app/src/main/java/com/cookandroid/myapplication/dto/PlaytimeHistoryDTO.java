package com.cookandroid.myapplication.dto;

import com.google.gson.JsonObject;

import java.util.HashMap;

public class PlaytimeHistoryDTO {
    String email;
    int musicId;
    JsonObject tagInfo;

    public PlaytimeHistoryDTO(String email, int musicId, JsonObject tagInfo) {
        this.email = email;
        this.musicId = musicId;
        this.tagInfo = tagInfo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    public JsonObject getTagInfo() {
        return tagInfo;
    }

    public void setTagInfo(JsonObject tagInfo) {
        this.tagInfo = tagInfo;
    }
}