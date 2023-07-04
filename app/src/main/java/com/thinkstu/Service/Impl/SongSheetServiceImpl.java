package com.thinkstu.Service.Impl;


import com.thinkstu.Service.*;
import com.thinkstu.entity.*;

import org.litepal.LitePal;

import java.util.List;

public class SongSheetServiceImpl implements SongSheetService {
    @Override
    public boolean add(String name, String imgAddress) {
        SongSheetBean songSheet = new SongSheetBean(name, imgAddress);
        return songSheet.save();
    }

    @Override
    public int delete(SongSheetBean songSheet) {
        return songSheet.delete();
    }

    public List<SongSheetBean> findAll() {
        //如果歌单数为0，创建新歌单 将assert中的music存入SongBean
        if (LitePal.findAll(SongSheetBean.class).size() == 0) {
            SongSheetBean songSheet = new SongSheetBean("默认歌单", null);
            songSheet.save();
        }
        return LitePal.findAll(SongSheetBean.class);
    }

    @Override
    public List<SongBean> findSongBeanBySongSheetId(int songSheetId) {
        return LitePal.where("songSheetId = ?", songSheetId + "").find(SongBean.class);
    }

    @Override
    public boolean addSongBean(SongBean songBean, SongSheetBean songSheetBean) {
        songBean.setSongSheetId(songSheetBean.getId());
        songBean.save();
        return songBean.getSongSheetId() == songSheetBean.getId();
    }
}
