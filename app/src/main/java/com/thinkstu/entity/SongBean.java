package com.thinkstu.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

// 歌曲实体类
public class SongBean extends LitePalSupport {
    private int    id;
    @Column(unique = true)
    private String name;        // 歌曲名，唯一性约束
    private int    songSheetId; // 所属歌单id

    public SongBean(String name, int songSheetId) {
        this.name        = name;
        this.songSheetId = songSheetId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSongSheetId() {
        return songSheetId;
    }

    public void setSongSheetId(int songSheetId) {
        this.songSheetId = songSheetId;
    }
}
