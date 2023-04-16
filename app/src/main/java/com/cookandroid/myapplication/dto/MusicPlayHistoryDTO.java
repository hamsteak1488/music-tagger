package com.cookandroid.myapplication.dto;

import com.google.gson.JsonArray;

public class MusicPlayHistoryDTO {

    int musicId;
    JsonArray jsonArray;

    public MusicPlayHistoryDTO(int musicId, JsonArray jsonArray) {
        this.musicId = musicId;
        this.jsonArray = jsonArray;
    }

    public int getMusicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    public JsonArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JsonArray jsonArray) {
        this.jsonArray = jsonArray;
    }
}
