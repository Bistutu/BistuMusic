package com.thinkstu.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

// 歌单实体类
public class SongSheetBean extends LitePalSupport {
    private int id;
    @Column(unique = true)          // 唯一性约束
    private String name;            // 歌单名
    private String imgAddress;      // 歌单图片地址

    public SongSheetBean(String name, String imgAddress) {
        this.name = name;
        this.imgAddress = imgAddress;
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

    public String getImgAddress() {
        return imgAddress;
    }

    public void setImgAddress(String imgAddress) {
        this.imgAddress = imgAddress;
    }
}
