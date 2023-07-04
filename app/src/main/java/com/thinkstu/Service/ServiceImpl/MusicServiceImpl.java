package com.thinkstu.Service.ServiceImpl;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;

import com.thinkstu.Service.*;
import com.thinkstu.entity.*;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class MusicServiceImpl implements MusicService {
    private static MusicServiceImpl musicServiceImpl = null;

    private MediaPlayer mediaPlayer;
    Context myContext;
    private String[] musicNames, temp_musicNames;
    private int currentPosition, currentIndex, order = PLAY_ORDER;
    private AssetManager                assetManager;
    private String                      currentMusicName;
    private MusicChangedListener        musicChangedListener;
    private MusicPlayingChangedListener musicPlayingChangedListener;

    private MusicServiceImpl(Context context) {
        myContext = context;
        // 播放器对象
        mediaPlayer = new MediaPlayer();
        try {
            // 获取 assets/music 下所有文件
            assetManager = context.getAssets();
            musicNames   = assetManager.list("music");
            // TODO 打标记📌：下面注释的五首英文歌
            musicNames = new String[]{
                    "Ketsa - Rain Man.mp3",
                    "Polkavant - Minor Piano.mp3",
                    "TimTaj - Melody of Love.mp3",
                    "Kathrin Klimek - Liquid Sun.mp3",
                    "Kathrin Klimek - Lucky Tears.mp3"};

            if (musicNames != null) {
                temp_musicNames = new String[musicNames.length];
                SongSheetService songSheetService = new SongSheetServiceImpl();
                //深拷贝
//                for (int i = 0; i < musicNames.length; i++) {
//                    temp_musicNames[i] = musicNames[i];
//                    //运行一次即可，用于将本地音乐存入 SongBean，参数：歌曲名，歌单id
//                    SongBean songBean = new SongBean(musicNames[i], songSheetService.findAll().get(0).getId());
//                }
                // 创建默认歌单，有 5 首歌曲，第一首已经下载
                new SongBean(musicNames[0], songSheetService.findAll().get(0).getId()).save();
                new SongBean(musicNames[1], songSheetService.findAll().get(0).getId()).save();
                new SongBean(musicNames[2], songSheetService.findAll().get(0).getId()).save();
                new SongBean(musicNames[3], songSheetService.findAll().get(0).getId()).save();
                new SongBean(musicNames[4], songSheetService.findAll().get(0).getId()).save();
                // 底部播放栏目默认显示第一首歌曲
                currentMusicName = musicNames[0];
                loadMusic(currentMusicName);    // 加载
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   /* @Override
    public void loadMusic(String musicName) {
        currentMusicName = musicName;
        try {
            mediaPlayer.reset();    // 重置
            AssetFileDescriptor afd = assetManager.openFd("music/" + currentMusicName); // 获取文件描述符
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();  // 准备
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void loadMusic(String musicName) {
        currentMusicName = musicName;
        try {
            mediaPlayer.reset(); // 重置
            FileInputStream fis = myContext.openFileInput(currentMusicName);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare(); // 准备
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static MusicService getInstance(Context context) {
        if (musicServiceImpl == null) {
            synchronized (MusicServiceImpl.class) {
                if (musicServiceImpl == null) {
                    musicServiceImpl = new MusicServiceImpl(context);
                }
            }
        }
        return musicServiceImpl;
    }

    @Override
    public void play(String musicName) {
        if (musicName == null) {
            if (mediaPlayer.isPlaying()) {
                onPause();
            } else {
                if (currentPosition > 0) {
                    onResume();
                } else {
                    start();
                }
            }
        } else if (!currentMusicName.equals(musicName)) {
            loadMusic(musicName);
            this.musicChangedListener.refresh();
            start();
        } else {
            if (!mediaPlayer.isPlaying()) {
                start();
            }
        }
    }

    private void start() {
        mediaPlayer.start();
        this.musicPlayingChangedListener.afterChanged();
        mediaPlayer.setOnCompletionListener(mediaPlayer -> next());
    }

    @Override
    public void onPause() {
        if (mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();     //获取当前播放位置
            mediaPlayer.pause();
            this.musicPlayingChangedListener.afterChanged();
        }
    }

    @Override
    public void seekTo(int progress) {
        if (!mediaPlayer.isPlaying()) {
            play(null);
        }
        mediaPlayer.seekTo(progress);
    }

    @Override
    public void onResume() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            this.musicPlayingChangedListener.afterChanged();
            mediaPlayer.seekTo(currentPosition);
            currentPosition = 0;
        }
    }

    @Override
    public void setPlayOrder(int i) {
        if (i == PLAY_ORDER) {
            order        = PLAY_ORDER;
            currentIndex = Arrays.binarySearch(musicNames, currentMusicName);     //二分法查找当前播放音乐的索引
        } else {
            order = PLAY_RANDOM;
            shuffleCard(musicNames);
            currentIndex = search(temp_musicNames, currentMusicName);    //二分法查找当前播放音乐的索引
        }
    }

    @Override
    public void next() {
        if (order == PLAY_ORDER) {  //顺序播放
            if (currentIndex < musicNames.length - 1) {
                play(musicNames[++currentIndex]);
            } else {
                currentIndex = 0;
                play(musicNames[currentIndex]);
            }
        } else {    //随机播放
            if (currentIndex < temp_musicNames.length - 1) {
                play(temp_musicNames[++currentIndex]);
            } else {
                currentIndex = 0;
                play(temp_musicNames[currentIndex]);
            }
        }
        this.musicChangedListener.refresh();
    }

    @Override
    public void last() {
        if (order == PLAY_ORDER) {  //顺序播放
            if (currentIndex > 0) {
                play(musicNames[--currentIndex]);
            } else {
                currentIndex = musicNames.length - 1;
                play(musicNames[currentIndex]);
            }
        } else {    //随机播放
            if (currentIndex > 0) {
                play(temp_musicNames[--currentIndex]);
            } else {
                currentIndex = temp_musicNames.length - 1;
                play(temp_musicNames[currentIndex]);
            }
        }
        this.musicChangedListener.refresh();
    }

    @Override
    public int getCurrentProgress() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void onDestroy() {
        assetManager.close();
        mediaPlayer.release();
    }

    /**
     * 洗牌算法
     *
     * @param names 顺序播放的musicNames
     */
    private void shuffleCard(String[] names) {
        int    len = names.length;
        Random r   = new Random();
        for (int i = 0; i < len; i++) {
            int    index = r.nextInt(len);
            String temp  = temp_musicNames[i];
            temp_musicNames[i]     = temp_musicNames[index];
            temp_musicNames[index] = temp;
        }
    }

    @Override
    public int getPlayOrder() {
        return order;
    }

    @Override
    public String getCurrentMusicInfo() {
        String   str  = currentMusicName.substring(0, currentMusicName.length() - 4);
        String[] info = str.split(" - ");
        return info[1] + "\n" + info[0];
//        return "111";
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public String[] getMusicNames() {
        return musicNames;
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public void setMusicChangedListener(MusicChangedListener musicChangedListener) {
        this.musicChangedListener = musicChangedListener;
    }

    private int search(String[] randomNames, String a) {
        for (int i = 0; i < randomNames.length; i++) {
            if (a.equals(randomNames[i])) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void setMusicPlayingChangedListener(MusicPlayingChangedListener musicPlayingChangedListener) {
        this.musicPlayingChangedListener = musicPlayingChangedListener;
    }

}
