package com.thinkstu.Service;

// 功能：音乐播放服务
public interface MusicService {
    int PLAY_ORDER  = 1; // 顺序播放
    int PLAY_RANDOM = 4; // 随机播放

    void loadMusic(String musicName);

    // 播放musicName，音乐名称 包括.mp3 如果参数为null,则播放当前音乐，若音乐已播放则继续播放。
    void play(String musicName);

    void seekTo(int progress);

    // 暂停
    void onPause();

    // 继续播放
    void onResume();

    // 更改播放顺序，i为PLAY_ORDER时，为顺序播放；为PLAY_RANDOM为随机播放
    void setPlayOrder(int i);

    // 下一首
    void next();

    // 上一首
    void last();

    // 获取当前进度
    // 返回当前进度 单位：毫秒
    int getCurrentProgress();

    // destroy MusicService
    void onDestroy();

    // 获取播放顺序
    // 返回 1 为顺序播放， 4 为随机播放
    int getPlayOrder();

    // 获取当前播放音乐的信息
    // 返回 "歌曲名称\n歌手”
    String getCurrentMusicInfo();

    // 获得总时长
    // 返回 单位：毫秒
    int getDuration();

//    void loadMusic(String musicName);

    // 获取音乐列表
    // 返回音乐名称数组
    String[] getMusicNames();

    boolean isPlaying();

    void setMusicChangedListener(MusicChangedListener musicChangedListener);

    void setMusicPlayingChangedListener(MusicPlayingChangedListener musicPlayingChangedListener);
}
