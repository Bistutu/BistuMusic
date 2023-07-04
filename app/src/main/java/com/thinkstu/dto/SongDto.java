package com.thinkstu.dto;

import com.thinkstu.entity.SongBean;
import com.thinkstu.entity.SongSheetBean;

import java.util.List;

/**
 * 歌曲和对应所处的歌单形成的复合类，负责处理对应关系
 */
public class SongDto {
    private boolean        isLocal;           // 是否是本地歌单
    private SongSheetBean  songSheetBean;       // 歌单对象
    private List<SongBean> songBeanList;    // 歌曲列表

    public boolean isLocal() {
        return isLocal;
    }

    public SongDto(SongSheetBean songSheetBean, List<SongBean> songBeanList) {
        this.songSheetBean = songSheetBean;
        this.songBeanList  = songBeanList;
    }

    public SongDto(SongSheetBean songSheetBean, List<SongBean> songBeanList, boolean isLocal) {
        this.songSheetBean = songSheetBean;
        this.songBeanList  = songBeanList;
        this.isLocal       = isLocal;
    }

    public SongSheetBean getSongSheetBean() {
        return songSheetBean;
    }


    public List<SongBean> getSongBeanList() {
        return songBeanList;
    }

}
